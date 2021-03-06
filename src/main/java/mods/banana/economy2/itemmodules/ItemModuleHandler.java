package mods.banana.economy2.itemmodules;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.oroarmor.config.ConfigItem;
import mods.banana.bananaapi.itemsv2.StackBuilder;
import mods.banana.economy2.itemmodules.display.ModuleDisplay;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.item.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ItemModuleHandler {
    public static ArrayList<ItemModule> registeredModules = new ArrayList<>();
    public static ArrayList<ItemModule> activeModules = new ArrayList<>();
    public static ItemModule MINECRAFT_ITEMS;

    public static void reset() {
        registeredModules = new ArrayList<>();
        activeModules = new ArrayList<>();
    }

    /**
     * Registers a module, this handles adding each matcher's children, so you must call this function
     * @param module module to be added
     */
    public static void register(ItemModule module) {
        registeredModules.add(module);

        // register all of the children
        for(NbtMatcher item : module.getValues().values()) {
            if(item.hasParent()) getRegisteredMatcher(item.getParent()).addChild(item);
        }
    }

    /**
     * Activates a module of the name, usage not necessary
     * @param name name of module to be added
     */
    public static void activate(String name) {
        activate(getModule(name));
    }

    public static void activate(ItemModule module) {
        if(!activeModules.contains(module)) activeModules.add(module);
    }

    public static void deactivate(String name) {
        deactivate(getModule(name));
    }

    public static void deactivate(ItemModule module) {
        activeModules.remove(module);
    }

    public static ItemModule getModule(String name) {
        for(ItemModule module : registeredModules) if(module.getName().equals(name)) return module;
        return null;
    }

    public static boolean contains(String name) {
        for(ItemModule module : registeredModules) if(module.getName().equals(name)) return true;
        return false;
    }


    public static List<NbtMatcher> getActiveItemsOfType(NbtMatcher.Type type) {
        ArrayList<NbtMatcher> items = new ArrayList<>();
        for(ItemModule module : activeModules) {
            for(NbtMatcher matcher : module.getValues().values())
                if(matcher.typeMatches(type)) items.add(matcher);
        }
        return items;
    }


    public static NbtMatcher getActiveMatcher(Identifier identifier) { return getActiveMatcher(identifier, NbtMatcher.Type.BOTH); }

    public static NbtMatcher getActiveMatcher(Identifier identifier, NbtMatcher.Type type) {
        for(ItemModule module : activeModules) {
            if(module.getValues().containsKey(identifier)) {
                NbtMatcher matcher = module.getValues().get(identifier);
                if(matcher.typeMatches(type)) return matcher;
            }
        }
        return null;
    }

    public static NbtMatcher getRegisteredMatcher(Identifier identifier) { return getRegisteredMatcher(identifier, NbtMatcher.Type.BOTH); }

    public static NbtMatcher getRegisteredMatcher(Identifier identifier, NbtMatcher.Type type) {
        for(ItemModule module : registeredModules) {
            if(module.getValues().containsKey(identifier)) {
                NbtMatcher matcher = module.getValues().get(identifier);
                if(matcher.typeMatches(type)) return matcher;
            }
        }
        return null;
    }



    public static NbtMatcher getMatch(ItemStack itemStack, NbtMatcher.Type type) {
        List<NbtMatcher> matches = getMatches(itemStack, type);
        return matches.size() == 0 ? null : matches.get(0);
    }

    public static List<NbtMatcher> getMatches(ItemStack stack, NbtMatcher.Type type) {
        ArrayList<NbtMatcher> matches = new ArrayList<>();

        // for each nbt item
        for(ItemModule module : activeModules) {
            for(NbtMatcher current : module.getValues().values()) {
                // if it matches, add it to list
                if(current.matches(stack, type)) {
                    matches.add(current);
                }
            }
        }

        return matches;
    }

    public static List<Identifier> getSoftMatches(ItemStack stack, NbtMatcher.Type type) {
        ArrayList<Identifier> matches = new ArrayList<>();

        // for each nbt item
        for(ItemModule module : activeModules) {
            for(NbtMatcher current : module.getValues().values()) {
                // if it soft matches, add it to list
                if(current.softMatches(stack, type)) {
                    matches.add(current.getIdentifier());
                }
            }
        }

        return matches;
    }



    public static void onChange(ConfigItem<Boolean> item) {
        // for each module
        for(ItemModule module : ItemModuleHandler.registeredModules) {
            // check if module changed is this one
            if(item.getName().equals(module.getName())) {
                // if so, add or remove the module
                if(item.getValue()) {
                    // make sure the active modules do not already contain the module
                    if(!ItemModuleHandler.activeModules.contains(module)) ItemModuleHandler.activeModules.add(module);
                } else ItemModuleHandler.activeModules.remove(module);

                return;
            }
        }
    }

    public static void init() {
        Map<Identifier, NbtMatcher> items = new HashMap<>();

        for(Identifier itemId : Registry.ITEM.getIds()) {
            Item item = Registry.ITEM.get(itemId);
            if(
                    item.getGroup() != null && // only allow obtainable items
                    !item.equals(Items.BEDROCK) &&
                    !item.equals(Items.END_PORTAL_FRAME) &&
                    !(item instanceof SpawnEggItem)
            ) items.put(itemId, new NbtItem(item));
        }

        MINECRAFT_ITEMS = new ItemModule("Minecraft", items, new ModuleDisplay("Minecraft", new StackBuilder().item(Items.GRASS_BLOCK).name("Minecraft").build()));
    }



    /**
     * creates suggestions of all item registry identifiers + all nbt item identifiers
     */
    public static class NbtMatcherSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        private final boolean negators;
        private final NbtMatcher.Type typeShown;

        public NbtMatcherSuggestionProvider() { this(false, NbtMatcher.Type.ITEM); }

        public NbtMatcherSuggestionProvider(boolean negators, NbtMatcher.Type typeShown) {
            this.negators = negators;
            this.typeShown = typeShown;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            // add regular minecraft items
            ArrayList<Identifier> identifiers = new ArrayList<>(Registry.ITEM.getIds());

            // add all nbt items
            for(ItemModule module : activeModules) {
                for(NbtMatcher item : module.getValues().values()) {
                    if(item.typeMatches(typeShown)) identifiers.add(item.getIdentifier());
                }
            }

            // add all negations
            if(negators) {
                for(ItemModule module : activeModules) {
                    for(NbtMatcher item : module.getValues().values()) {
                        if(item.typeMatches(typeShown)) {
                            Identifier identifier = item.getIdentifier();
                            identifiers.add(new Identifier("-" + identifier.getNamespace(), identifier.getPath()));
                        }
                    }
                }
            }

            // suggest the identifiers
            return CommandSource.suggestIdentifiers(identifiers, builder);
        }
    }

    public static class ModuleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

        public ModuleSuggestionProvider() {}

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            List<String> modules = new ArrayList<>();

            for(ItemModule module : registeredModules) {
                modules.add(module.getName());
            }

            return CommandSource.suggestMatching(modules, builder);
        }
    }
}
