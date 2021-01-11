package mods.banana.economy2.mixins;

import mods.banana.economy2.Economy2;
import mods.banana.economy2.OfflinePlayer;
import mods.banana.economy2.interfaces.ChestInterface;
import mods.banana.economy2.interfaces.ChestShopPlayerInterface;
import mods.banana.economy2.interfaces.PlayerInterface;
import mods.banana.economy2.interfaces.SignInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(SignBlockEntity.class)
public class SignEntityMixin extends BlockEntity implements SignInterface {
    @Shadow @Final private Text[] text;
    private boolean chestShop;
    private UUID parent;
    private BlockPos chest;

    public SignEntityMixin(BlockEntityType<?> type) { super(type); }

    public boolean isChestShop() { return chestShop; }

    public UUID getParent() { return parent; }

    public void create(ServerPlayerEntity player, BlockPos chest) {
        chestShop = true;
        if(!isAdmin()) {
            this.parent = player.getUuid();
            this.chest = chest;
        }
    }

    public void destroy(boolean destroyOther) {
        this.chestShop = false;
        this.parent = null;
        if(destroyOther) ((ChestInterface)world.getBlockEntity(chest)).destroy(false);
        this.chest = null;
    }

    public boolean isAdmin() {
        return chestShop && text[0].getString().matches("Admin");
    }

    public long getBuy() {
        if(chestShop) {
            if(text[2].getString().matches("^B (\\d+) : (\\d+) S$")) { // if it has both buy and sell
                return Long.parseLong(text[2].getString().replaceAll("^B (\\d+) : (\\d+) S$", "$1"));
            } else if (text[2].getString().matches("B (\\d+)$")) { // if it is buy only
                return Long.parseLong(text[2].getString().replaceAll("B (\\d+)$", "$1"));
            } else return -1; // if it is sell only or not a valid sign
        } else return -1; // not a chest shop sign
    }

    public long getSell() {
        if(chestShop) {
            if(text[2].getString().matches("^B (\\d+) : (\\d+) S$")) { // if it has both buy and sell
                return Long.parseLong(text[2].getString().replaceAll("^B (\\d+) : (\\d+) S$", "$2"));
            } else if (text[2].getString().matches("S (\\d+)$")) { // if it is sell only
                return Long.parseLong(text[2].getString().replaceAll("B (\\d+)$", "$1"));
            } else return -1; // if it is buy only or not a valid sign
        } else return -1; // not a chest shop sign
    }

    public Item getItem() {
        if(chestShop) {
            return Registry.ITEM.get(new Identifier(text[3].getString()));
        } else return null;
    }

    public int getAmount() {
        if(chestShop) {
            return Integer.parseInt(text[1].getString());
        } else return -1;
    }

    public ChestInterface getChest() {
        return (ChestInterface) world.getBlockEntity(chest);
    }

    @Inject(method = "onActivate", at = {@At("HEAD")})
    public void onBuy(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        onBuy(player);
    }

    public void onBuy(PlayerEntity player) {
        if(chestShop) {
            long buy = getBuy();
            ChestShopPlayerInterface buyer = (ChestShopPlayerInterface) player;
            if(!isAdmin()) {
                PlayerInterface owner = OfflinePlayer.getPlayer(parent);
                if(buy > -1) {
                    if(buyer.getBal() >= buy) {
                        if(getChest().countItem(getItem()) >= getAmount()) {
                            getChest().removeItemStack(new ItemStack(getItem(), getAmount()));
                            buyer.giveStack(new ItemStack(getItem(), getAmount()));
                            owner.addBal(buy);
                            buyer.addBal(-buy);
                        } else player.sendSystemMessage(new LiteralText("This shop does not have enough items for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                    } else player.sendSystemMessage(new LiteralText("You do not have enough balance for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is sell only.").formatted(Formatting.RED), UUID.randomUUID());
            } else {
                if(buy > -1) {
                    if(buyer.getBal() >= buy) {
                        buyer.giveStack(new ItemStack(getItem(), getAmount()));
                        buyer.addBal(-buy);
                    } else player.sendSystemMessage(new LiteralText("You do not have enough balance for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is sell only.").formatted(Formatting.RED), UUID.randomUUID());
            }
        }
    }

    public void onSell(PlayerEntity player) {
        if(chestShop) {
            long sell = getSell();
            int amount = getAmount();
            Item item = getItem();
            ChestShopPlayerInterface seller = (ChestShopPlayerInterface) player;
            if(!isAdmin()) {
                ChestInterface chest = getChest();
                PlayerInterface owner = OfflinePlayer.getPlayer(parent);
                if(sell > -1) {
                    if(seller.countItem(item) >= amount) {
                        if(getChest().countSpace(item) >= amount) {
                            if(owner.getBal() >= sell) {
                                seller.removeItemStack(new ItemStack(item, amount));
                                chest.insertItemStack(new ItemStack(item, amount));
                                seller.addBal(sell);
                                owner.addBal(-sell);
                            } else player.sendSystemMessage(new LiteralText("The shop owner does not have enough " + Economy2.currencyName + " for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                        } else player.sendSystemMessage(new LiteralText("The shop does not have enough space for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                    } else player.sendSystemMessage(new LiteralText("You do not have enough items for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is buy only.").formatted(Formatting.RED), UUID.randomUUID());
            } else {
                if(sell > -1) {
                    if(seller.countItem(item) >= amount) {
                        seller.removeItemStack(new ItemStack(item, amount));
                        seller.addBal(sell);
                    } else player.sendSystemMessage(new LiteralText("You do not have enough items for this trade.").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("This shop is buy only.").formatted(Formatting.RED), UUID.randomUUID());
            }
        }
    }

    @Inject(method = "toTag", at = {@At("HEAD")})
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(chestShop) {
            CompoundTag chestShopTag = new CompoundTag();
            if(isAdmin()) {
                chestShopTag.putBoolean("admin", true);
            } else {
                CompoundTag chestTag = new CompoundTag();
                chestTag.putInt("x", chest.getX());
                chestTag.putInt("y", chest.getY());
                chestTag.putInt("z", chest.getZ());
                chestShopTag.put("chest", chestTag);

                chestShopTag.putUuid("parent", parent);
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
            if(!chestShopTag.getBoolean("admin")) {
                parent = chestShopTag.getUuid("parent");

                CompoundTag chestTag = chestShopTag.getCompound("chest");
                chest = new BlockPos(chestTag.getInt("x"), chestTag.getInt("y"), chestTag.getInt("z"));
            }
        }
    }
}
