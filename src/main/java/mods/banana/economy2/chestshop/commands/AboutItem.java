package mods.banana.economy2.chestshop.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy2.chestshop.itemmodules.ItemModuleHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class AboutItem {
    public static int run(ServerPlayerEntity source) {
        ItemStack selectedItem = source.inventory.getStack(source.inventory.selectedSlot);
        if(!selectedItem.equals(ItemStack.EMPTY)) {
            // initialize item identifier name
            String item;

            // get nbt item from modules
            Identifier nbtItem = ItemModuleHandler.getIdentifierOfStack(selectedItem);

            // if it is a default minecraft item, set the identifier to it's one
            if(nbtItem == null) item = Registry.ITEM.getId(selectedItem.getItem()).toString();
            // if the item is a set item, set the identifier to it's custom identifier.
            else item = nbtItem.toString();

            // send message to source
            source.sendSystemMessage(new LiteralText(item), UUID.randomUUID());

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
