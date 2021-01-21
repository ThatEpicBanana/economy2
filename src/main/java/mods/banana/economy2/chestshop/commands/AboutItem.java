package mods.banana.economy2.chestshop.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.UUID;

public class AboutItem {
    public static int run(ServerPlayerEntity source) {
        ItemStack selectedItem = source.inventory.getStack(source.inventory.selectedSlot);
        if(!selectedItem.equals(ItemStack.EMPTY)) {
            // get matches
            List<Identifier> matches = ItemModuleHandler.getSoftMatches(selectedItem);
            // add regular minecraft item and set it to front
            matches.add(0, Registry.ITEM.getId(selectedItem.getItem()));

            // send header
            source.sendSystemMessage(new LiteralText(" - Identifiers - ").formatted(Formatting.GREEN), UUID.randomUUID());
            // send items
            for(Identifier item : matches) {
                // minecraft:
                MutableText text = new LiteralText(item.toString().replaceAll("[a-zA-Z-_]+$", "")).formatted(Formatting.GRAY);
                // dirt
                text.append(new LiteralText(item.toString().replaceAll("^\\w+:", "")).formatted(Formatting.WHITE));
                // minecraft:dirt
                source.sendSystemMessage(text, UUID.randomUUID());
            }


            return 1; // command succeeded
        } else return 0; // command failed
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("aboutitem")
                .executes(context -> run(context.getSource().getPlayer()))
                .build();
    }
}
