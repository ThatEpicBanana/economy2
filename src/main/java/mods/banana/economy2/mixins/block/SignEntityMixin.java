package mods.banana.economy2.mixins.block;

import mods.banana.bananaapi.helpers.ItemStackHelper;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.balance.OfflinePlayer;
import mods.banana.economy2.chestshop.interfaces.mixin.HopperInterface;
import mods.banana.economy2.chestshop.interfaces.mixin.ChestInterface;
import mods.banana.economy2.chestshop.interfaces.mixin.ChestShopPlayerInterface;
import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.chestshop.interfaces.mixin.SignInterface;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(SignBlockEntity.class)
public class SignEntityMixin extends BlockEntity implements SignInterface {
    @Shadow @Final private Text[] text;
    private boolean chestShop;
    private UUID parent;
    private BlockPos chest;

    public SignEntityMixin(BlockEntityType<?> itemType) { super(itemType); }

    public boolean isChestShop() { return chestShop && Economy2.CONFIG.getValue("chestShop.enabled", Boolean.class); }

    public UUID getParent() { return parent; }

    public void create(ServerPlayerEntity player, BlockPos chest) {
        if(!Economy2.CONFIG.getValue("chestShop.enabled", Boolean.class) || (chest == null && !Economy2.CONFIG.getValue("chestShop.adminshops", Boolean.class))) return;
        chestShop = true;
        this.parent = player.getUuid();
        if(!isAdmin()) {
            this.chest = chest;
        }
    }

    public void destroy(boolean destroyOther) {
        this.chestShop = false;
        this.parent = null;
        if(destroyOther && chest != null) ((ChestInterface)world.getBlockEntity(chest)).destroy(false);
        this.chest = null;
    }

    public boolean isAdmin() {
        return chestShop && text[0].getString().matches("Admin");
    }

    public long getBuy() {
        if(isChestShop()) {
            if(text[2].getString().toLowerCase().matches("^b (\\d+) : (\\d+) s$")) { // if it has both buy and sell
                return Long.parseLong(text[2].getString().toLowerCase().replaceAll("^b (\\d+) : (\\d+) s$", "$1"));
            } else if (text[2].getString().toLowerCase().matches("^b (\\d+)$")) { // if it is buy only
                return Long.parseLong(text[2].getString().toLowerCase().replaceAll("^b (\\d+)$", "$1"));
            } else return -1; // if it is sell only or not a valid sign
        } else return -1; // not a chest shop sign
    }

    public long getSell() {
        if(isChestShop()) {
            if(text[2].getString().toLowerCase().matches("^b (\\d+) : (\\d+) s$")) { // if it has both buy and sell
                return Long.parseLong(text[2].getString().toLowerCase().replaceAll("^b (\\d+) : (\\d+) s$", "$2"));
            } else if (text[2].getString().toLowerCase().matches("^s (\\d+)$")) { // if it is sell only
                return Long.parseLong(text[2].getString().toLowerCase().replaceAll("^s (\\d+)$", "$1"));
            } else return -1; // if it is buy only or not a valid sign
        } else return -1; // not a chest shop sign
    }

    public NbtItem getItem() {
        if(isChestShop()) {
//            return BaseItem.fromIdentifier(new Identifier(text[3].getString()));
            NbtItem item = (NbtItem) ItemModuleHandler.getActiveMatcher(new Identifier(text[3].getString()), NbtMatcher.Type.ITEM);
            if(item == null) return new NbtItem(Registry.ITEM.get(new Identifier(text[3].getString())));
            else return item;
        } else return null;
    }

    public ItemStack getItemStack() {return getItemStack(1);}

    public ItemStack getItemStack(int amount) {
        if(isChestShop()) {
            return ItemStackHelper.setCount(getItem().toItemStack(), amount);
        } else return null;
    }

    public int getAmount() {
        if(isChestShop()) {
            return Integer.parseInt(text[1].getString());
        } else return -1;
    }

    public ChestInterface getChest() {
        return (ChestInterface) world.getBlockEntity(chest);
    }

    @Inject(method = "onActivate", at = {@At("HEAD")})
    public void onBuy(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) { onBuy(player); }

    public void onBuy(PlayerEntity player) {
        if(isChestShop()) {
            long buy = getBuy();
            int amount = getAmount();
            NbtItem item = getItem();
            ChestShopPlayerInterface buyer = (ChestShopPlayerInterface) player;
            if(!isAdmin()) {
                PlayerInterface owner = OfflinePlayer.getPlayer(parent);
                ChestInterface chest = getChest();
                if(buy > -1) {
                    if(buyer.getBal() >= buy) {
                        if(chest.countItem(item) >= amount) {
                            //remove stacks from chest and give to seller
                            buyer.giveStacks(chest.removeItem(item, amount));

                            // remove balance from buyer
                            buyer.addBal(-buy);
                            // add to owner
                            owner.addBal(buy);

                            player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 1, 1.5F);
                        } else player.sendSystemMessage(new LiteralText("This shop does not have enough items for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                    } else player.sendSystemMessage(new LiteralText("You do not have enough " + Economy2.CONFIG.getValue("currency.name", String.class) + " for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is sell only.").formatted(Formatting.RED), UUID.randomUUID());
            } else {
                if(buy > -1) {
                    if(buyer.getBal() >= buy) {
                        // give items to buyer
                        buyer.giveStack(getItemStack(amount));
                        // remove balance from buyer
                        buyer.addBal(-buy);
                        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 1, 1.5F);
                    } else player.sendSystemMessage(new LiteralText("You do not have enough " + Economy2.CONFIG.getValue("currency.name", String.class) + " for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is sell only.").formatted(Formatting.RED), UUID.randomUUID());
            }
        }
    }

    public void onSell(PlayerEntity player) {
        if(isChestShop()) {
            long sell = getSell();
            int amount = getAmount();
            NbtItem item = getItem();
            ChestShopPlayerInterface seller = (ChestShopPlayerInterface) player;
            if(!isAdmin()) {
                ChestInterface chest = getChest();
                PlayerInterface owner = OfflinePlayer.getPlayer(parent);
                if(sell > -1) {
                    if(seller.countItem(item) >= amount) {
                        if(getChest().countSpaceForStack(getItemStack()) >= amount) {
                            if(owner.getBal() >= sell) {
                                // remove items from seller and insert them into chest
                                chest.insertStacks(seller.removeItem(item, amount));

                                // give seller the sell amount
                                seller.addBal(sell);
                                // remove the sell amount from the owner
                                owner.addBal(-sell);

                                player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 1, 1);
                            } else player.sendSystemMessage(new LiteralText("The shop owner does not have enough " + Economy2.CONFIG.getValue("currency.name", String.class) + " for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                        } else player.sendSystemMessage(new LiteralText("The shop does not have enough space for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                    } else player.sendSystemMessage(new LiteralText("You do not have enough items for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is buy only.").formatted(Formatting.RED), UUID.randomUUID());
            } else {
                if(sell > -1) {
                    if(seller.countItem(item) >= amount) {
                        // remove items from seller
                        seller.removeItem(item, amount);

                        // add balance to seller
                        seller.addBal(sell);

                        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.BLOCKS, 1, 1);
                    } else player.sendSystemMessage(new LiteralText("You do not have enough items for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is buy only.").formatted(Formatting.RED), UUID.randomUUID());
            }
        }
    }

    public void onSell(HopperInterface hopper, PlayerInterface player) {
        long sell = getSell();
        int amount = getAmount();
        NbtItem item = getItem();
        if(isChestShop() && sell >= -1 && hopper.countItem(item) >= amount) {
            if(!isAdmin()) {
                ChestInterface chest = getChest();
                PlayerInterface owner = OfflinePlayer.getPlayer(parent);
                if(chest.countSpaceForStack(getItemStack()) >= amount && owner.getBal() >= sell) {
                    // remove items from seller and insert them into chest
                    chest.insertStacks(hopper.removeItem(item, amount));

                    // give seller the sell amount
                    player.addBal(sell);
                    // remove the sell amount from the owner
                    owner.addBal(-sell);
                }
            } else {
                // remove items from seller
                hopper.removeItem(item, amount);

                // add balance to seller
                player.addBal(sell);
            }
        }
    }

    @Inject(method = "toTag", at = {@At("HEAD")})
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(isChestShop()) {
            CompoundTag chestShopTag = new CompoundTag();

            chestShopTag.putUuid("parent", parent);

            if(isAdmin()) {
                chestShopTag.putBoolean("admin", true);
            } else {
                CompoundTag chestTag = new CompoundTag();
                chestTag.putInt("x", chest.getX());
                chestTag.putInt("y", chest.getY());
                chestTag.putInt("z", chest.getZ());
                chestShopTag.put("chest", chestTag);

                chestShopTag.putBoolean("admin", false);
            }
            tag.put("chestshop", chestShopTag);
        }
    }

    @Inject(method = "fromTag", at = {@At("HEAD")})
    private void load(BlockState state, CompoundTag tag, CallbackInfo ci) {
        if(tag.contains("chestshop")) {
            chestShop = true;

            CompoundTag chestShopTag = tag.getCompound("chestshop");
            parent = chestShopTag.getUuid("parent");

            if(!chestShopTag.getBoolean("admin")) {
                CompoundTag chestTag = chestShopTag.getCompound("chest");
                chest = new BlockPos(chestTag.getInt("x"), chestTag.getInt("y"), chestTag.getInt("z"));
            }
        }
    }
}
