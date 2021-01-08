package mods.banana.economy2.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.interfaces.PlayerInterface;
import mods.banana.economy2.items.EconomyItems;
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

import java.util.UUID;

public class banknote {
    public static int run(ServerPlayerEntity target, long amount) {
        ItemStack itemStack = EconomyItems.BANKNOTE.getItemStack();
        CompoundTag tag = itemStack.getTag();

        // add amount to tag
        tag.getCompound(EconomyItems.BANKNOTE.getIdentifier().getNamespace()).putLong("amount", amount);

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