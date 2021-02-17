package mods.banana.economy2.itemmodules.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.bananaapi.helpers.TextHelper;
import mods.banana.bananaapi.itemsv2.StackReader;
import mods.banana.economy2.Economy2;
import mods.banana.economy2.itemmodules.ItemModule;
import mods.banana.economy2.itemmodules.ItemModuleHandler;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.UUID;
import java.util.logging.Logger;

public class ActivateModule {
    public static int activate(String moduleName) {
        ItemModule module = ItemModuleHandler.getModule(moduleName);
        if(module != null) {
            ItemModuleHandler.activate(module);
            return 1;
        } else return 0;
    }

    public static int deactivate(String moduleName) {
        ItemModule module = ItemModuleHandler.getModule(moduleName);
        if(module != null) {
            ItemModuleHandler.deactivate(module);
            return 1;
        } else return 0;
    }

    private static MutableText getModuleText(ItemModule module) {
        MutableText text = new LiteralText("");

        text.append(new LiteralText(" - Module: " + module.getName() + " - ").formatted(Formatting.YELLOW));

        text.append(new LiteralText("\nDisplay: "));

        if(module.hasDisplay() && module.getDisplay().getStack() != null)
            text.append(new LiteralText(Registry.ITEM.getId(module.getItemStack().getItem()).toString()))
                .setStyle(TextHelper.TRUE_RESET.withHoverEvent(new StackReader(module.getItemStack()).toHoverEvent()));
        else
            text.append(new LiteralText("none"));

        text.append(new LiteralText("\nItems: "));
        text.append(new LiteralText(module.getValues().size() + "").formatted(Formatting.GOLD));

        return text;
    }

    public static int about(Entity player, String moduleName) {
        ItemModule module = ItemModuleHandler.getModule(moduleName);
        if(module != null) {
            if(player instanceof ServerPlayerEntity) {
                player.sendSystemMessage(getModuleText(module), UUID.randomUUID());
            } else {
                Economy2.LOGGER.info(getModuleText(module));
            }

            return 1;
        } else return 0;
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager
                .literal("module")
                .requires(source -> source.hasPermissionLevel(3))
                .then(
                        CommandManager.argument("module", StringArgumentType.string())
                                .suggests(new ItemModuleHandler.ModuleSuggestionProvider())
                                .then(
                                        CommandManager.literal("activate")
                                                .executes(context -> activate(StringArgumentType.getString(context, "module")))
                                )
                                .then(
                                        CommandManager.literal("deactivate")
                                                .executes(context -> deactivate(StringArgumentType.getString(context, "module")))
                                )
                                .then(
                                        CommandManager.literal("about")
                                                .executes(context -> about(context.getSource().getEntity(), StringArgumentType.getString(context, "module")))
                                )
                )
                .build();
    }
}
