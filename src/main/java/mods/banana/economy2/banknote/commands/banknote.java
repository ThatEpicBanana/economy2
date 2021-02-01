package mods.banana.economy2.banknote.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.balance.PlayerInterface;
import mods.banana.economy2.EconomyItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class banknote {
    public static int run(ServerPlayerEntity target, long amount) {
        if(((PlayerInterface)target).getBal() >= amount) {
            ItemStack itemStack = EconomyItems.Banknote.BANKNOTE.getItemStack();
            CompoundTag tag = itemStack.getTag();

            // add amount to tag
            tag.getCompound(EconomyItems.Banknote.BANKNOTE.getIdentifier().getNamespace()).putLong("amount", amount);

            // add lore
            ListTag loreTag = tag.getCompound("display").getList("Lore", 9);
            loreTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(Economy2.addCurrencySign(amount)).formatted(Formatting.WHITE))));
            tag.getCompound("display").put("Lore", loreTag);

            // set tag
            itemStack.setTag(tag);
            // give player the item stack
            target.giveItemStack(itemStack);

            ((PlayerInterface)target).addBal(-amount);
            return 1;
        } else return 0;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("banknote")
                .then(
                        CommandManager.argument("amount", LongArgumentType.longArg(0))
                                .executes(context -> run(context.getSource().getPlayer(), LongArgumentType.getLong(context, "amount")))
                )
                .build();
    }
}