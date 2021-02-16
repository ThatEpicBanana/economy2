package mods.banana.economy2.bounties.gui;

import mods.banana.bananaapi.helpers.ItemStackHelper;

import static mods.banana.economy2.EconomyItems.Bounties.Create.*;
import static mods.banana.economy2.EconomyItems.Bounties.*;
import static mods.banana.economy2.EconomyItems.Gui.*;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.bounties.Bounty;
import mods.banana.economy2.bounties.items.MatcherItem;
import mods.banana.economy2.gui.GuiReturnValue;
import mods.banana.economy2.gui.mixin.GuiPlayer;
import mods.banana.economy2.gui.screens.GuiScreen;
import mods.banana.economy2.gui.screens.SignGui;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.gui.ModulesScreen;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class CreateBounty extends GuiScreen {
    private boolean customTags;
    ItemStack[] matchers = new ItemStack[5];
    ItemStack baseItem = SET_ITEM.getItemStack();
    NbtItem baseItemNbt = null;

    private static final Pattern checkInt = Pattern.compile("^\\d+$");
    private String error;

    private int amount = 0;
    private long price = 0;

    @Override
    public void withReturnValue(GuiReturnValue<?> returnValue) {
        if(returnValue != null) {
            Identifier id = returnValue.getParent().getId();

            if(id.getNamespace().equals("modifier")) {
                Identifier value = (Identifier) returnValue.getValue();

                // get index from path
                int i = Integer.parseInt(id.getPath());
                ItemStack matcherStack = matchers[i];

                if(returnValue.getValue() instanceof Identifier) {
                    NbtMatcher matcher = ItemModuleHandler.getActiveMatcher(value);

                    // get item stack
                    if(matcher != null) {
                        MatcherItem matcherType = UNSET_MATCHER.isActivated(matcherStack) ?
                                REQUIRED_MATCHER :
                                DENIED_MATCHER;

                        matcherStack = matcher.toItemStack();

                        matcherType.convertTag(matcherStack);
                    }

                    // set item modifier to value
                    matcherStack = UNSET_MATCHER.setValue(matcherStack, value);
                } else {
                    // return value is null, reset stack
                    matcherStack = UNSET_MATCHER.getItemStack();
                }

                // update item stack
                matchers[i] = matcherStack;
            }

            if(returnValue.getValue() != null && id.getNamespace().equals("item")) {
                Identifier value = (Identifier) returnValue.getValue();

                baseItemNbt = (NbtItem) ItemModuleHandler.getActiveMatcher(value, NbtMatcher.Type.ITEM);
                if(baseItemNbt == null) baseItemNbt = (NbtItem) ItemModuleHandler.MINECRAFT_ITEMS.getValues().get(value);

                baseItem = baseItemNbt.toItemStack();
                SET_ITEM.convertTag(baseItem);

                // clear matchers
                resetMatchers();
            }

            if(returnValue.getParent() instanceof SignGui) {
                String value = (String) returnValue.getValue();
                if(returnValue.getValue() != null && id.equals(new Identifier("bounty", "amount"))) {
                    if(checkInt.matcher(value).find()) {
                        this.amount = Integer.parseInt(value);
                    }
                }

                if(returnValue.getValue() != null && id.equals(new Identifier("bounty", "price"))) {
                    if(checkInt.matcher(value).find()) {
                        this.price = Long.parseLong(value);
                    }
                }
            }
        }

        super.withReturnValue(returnValue);
    }

    public CreateBounty() { this(0, new PlayerInventory(null)); }

    public CreateBounty(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(9 * 6), false);
    }

    public CreateBounty(int syncId, PlayerInventory playerInventory, Inventory inventory, boolean customTags) {
        super(syncId, playerInventory, inventory, 6, new Identifier("bounty", "create"));
        this.customTags = customTags;
        resetMatchers();
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Create bounty");
    }

    @Override
    public void updateState() {
        clear();

        if(customTags) {
            setStackInSlot(5, ALLOW_CUSTOM_TAGS.getItemStack());

            setStackInSlot(9 + 1, baseItem);

            for(int i = 0; i < 5; i++) {
                ItemStack matcher = matchers[i];

                setStackInSlot(9 + 3 + i, matcher);

                setStackInSlot(9 * 2 + 3 + i,
                        UNSET_MATCHER.isSet(matcher)
                                ? UNSET_MATCHER.isActivated(matcher)
                                        ? REQUIRED_MATCHER_DISPLAY.getItemStack() // if matcher is required
                                        : DENIED_MATCHER_DISPLAY.getItemStack() // if matcher is denied
                                : EMPTY.getItemStack() // if matcher is not set
                );
            }

        } else {
            setStackInSlot(4, ALLOW_CUSTOM_TAGS.getItemStack());

            setStackInSlot(9 + 4, baseItem);
        }

        ItemStack amount  = AMOUNT.getItemStack();
        ItemStackHelper.addLore(amount, List.of(new LiteralText(String.valueOf(this.amount))), 0);
        setStackInSlot(9 * 3 + 4, amount);

        ItemStack price  = PRICE.getItemStack();
        ItemStackHelper.addLore(price, List.of(new LiteralText(String.valueOf(this.price))), 0);
        setStackInSlot(9 * 3 + 6, price);

        if(error != null) setStackInSlot(9 * 3 + 2, ItemStackHelper.setLore(CONFIRM.getItemStack(), List.of(new LiteralText(error))));
        else setStackInSlot(9 * 3 + 2, CONFIRM.getItemStack());

        setStackInSlot(9 * 5 + 4, RETURN.getItemStack());
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if(i >= 0 && !playerEntity.world.isClient) {
            ItemStack stack = getSlot(i).getStack();

            if(ALLOW_CUSTOM_TAGS.matches(stack)) customTags = !customTags;

            if(j > 0) {
                if(UNSET_MATCHER_DISPLAY.idMatches(stack) || UNSET_MATCHER.idMatches(stack)) {
                    matchers[i % 9 - 3] = UNSET_MATCHER.setActivated(matchers[i % 9 - 3], !UNSET_MATCHER.isActivated(stack));
                }
            } else {
                if(UNSET_MATCHER.idMatches(stack)) {
                    ((GuiPlayer)playerEntity).openScreen(new ModulesScreen(
                            NbtMatcher.Type.BOTH,
                            false,
                            baseItem != null ? baseItem.getItem() : null,
                            new Identifier("modifier", "" + (i - (9 + 3)))
                    ));
                }
            }

            if(SET_ITEM.idMatches(stack)) {
                ((GuiPlayer)playerEntity).openScreen(new ModulesScreen(
                        NbtMatcher.Type.ITEM,
                        true,
                        null,
                        new Identifier("item", "" + (i - (9 + 3)))
                ));
            }

            if(AMOUNT.matches(stack)) {
                ((GuiPlayer)playerEntity).openSignGui(new Identifier("bounty", "amount"));
            }

            if(PRICE.matches(stack)) {
                ((GuiPlayer)playerEntity).openSignGui(new Identifier("bounty", "price"));
            }

            if(CONFIRM.matches(stack)) {
                Bounty bounty = new Bounty(playerEntity.getUuid(), playerEntity.getEntityName(), baseItemNbt, getMatchers(true), getMatchers(false), amount, price);

                Optional<String> validity = bounty.getValidity();
                if(validity.isPresent()) {
                    // doesn't match, throw error
                    error = validity.get();
                } else {
                    // matches, add to handler
                    Economy2.bountyHandler.add(bounty);
                    ((GuiPlayer)playerEntity).closeScreen();
                }
            }

//            setStackInSlot(i, stack);
        }

        return super.onSlotClick(i, j, actionType, playerEntity);
    }

    public List<NbtMatcher> getMatchers(boolean type) {
        ArrayList<NbtMatcher> list = new ArrayList<>();

        for(ItemStack stack : matchers) {
            if(UNSET_MATCHER.isSet(stack)) {
                if(UNSET_MATCHER.isActivated(stack) == type) {
                    list.add(ItemModuleHandler.getRegisteredMatcher(UNSET_MATCHER.getValue(stack)));
                }
            }
        }

        // remove duplicates
        list = new ArrayList<>(new HashSet<>(list));

        return list;
    }

    public void resetMatchers() {
        for(int i = 0; i < 5; i++) {
            matchers[i] = UNSET_MATCHER.getItemStack().copy();
        }
    }

//    @Override
//    public GuiScreen copy(int syncId, PlayerInventory inventory, PlayerEntity player) {
//        return new CreateBounty(syncId, inventory, getInventory(), customTags);
//    }
}
