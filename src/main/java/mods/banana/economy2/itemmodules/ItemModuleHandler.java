package mods.banana.economy2.itemmodules;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.oroarmor.config.ConfigItem;
import mods.banana.economy2.itemmodules.items.NbtItem;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import mods.banana.economy2.itemmodules.items.NbtModifier;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemModuleHandler {
    public static ArrayList<ItemModule> registeredModules = new ArrayList<>();
    public static ArrayList<ItemModule> activeModules = new ArrayList<>();

    public static void register(ItemModule module) {
        registeredModules.add(module);

        // register all of the children
        for(NbtMatcher item : module.getValues().values()) {
            if(item.hasParent()) getRegisteredItem(item.getParent()).addChild(item);
        }
    }

    public static void activate(String name) {
        activeModules.add(getModule(name));
    }

    public static ItemModule getModule(String name) {
        for(ItemModule module : registeredModules) if(module.getName().equals(name)) return module;
        return null;
    }

    public static boolean contains(String name) {
        for(ItemModule module : registeredModules) if(module.getName().equals(name)) return true;
        return false;
    }


    public static NbtMatcher getActiveMatcher(Identifier identifier) {
        for(ItemModule module : activeModules) {
            if(module.getValues().containsKey(identifier)) {
                return module.getValues().get(identifier);
            }
        }
        return null;
    }

    public static NbtItem getActiveItem(Identifier identifier) {
        NbtMatcher matcher = getActiveMatcher(identifier);
        return matcher instanceof NbtItem ? (NbtItem) matcher : null;
    }

    public static NbtModifier getActiveModifier(Identifier identifier) {
        NbtMatcher matcher = getActiveMatcher(identifier);
        return matcher instanceof NbtModifier ? (NbtModifier) matcher : null;
    }

    public static NbtMatcher getRegisteredItem(Identifier identifier) {
        for(ItemModule module : registeredModules) {
            if(module.getValues().containsKey(identifier)) {
                return module.getValues().get(identifier);
            }
        }
        return null;
    }

    public static Identifier getMatchOfUnsureStack(ItemStack itemStack, NbtMatcher.Type type) {
        Identifier nbtItem = getMatch(itemStack, type);
        if(nbtItem != null) return nbtItem;
        else return Registry.ITEM.getId(itemStack.getItem());
    }


    public static Identifier getMatch(ItemStack itemStack, NbtMatcher.Type type) {
        List<Identifier> matches = getMatches(itemStack, type);
        return matches.size() == 0 ? null : matches.get(0);
    }

    public static List<Identifier> getMatches(ItemStack stack, NbtMatcher.Type type) {
        ArrayList<Identifier> matches = new ArrayList<>();

        // for each nbt item
        for(ItemModule module : activeModules) {
            for(NbtMatcher current : module.getValues().values()) {
                // if it matches, add it to list
                if(current.matches(stack, type)) {
                    matches.add(current.getIdentifier());
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


//    // gets eldest ancestor of nbt item
//    private static NbtItem getParent(NbtItem item) {
//        NbtItem current = item.copy();
//        while(current.hasParent()) {
//            current = getActiveItem(current.getParent()).copy();
//        }
//        return current;
//    }

//    // finds the child with the best fit to the item stack
//    private static Pair<Identifier, Integer> getBestFit(NbtItem item, ItemStack stack, int specificity) {
//        // visualizer
//        System.out.println(StringUtils.repeat("    ", specificity) + item.getIdentifier());
//
//        // increment specificity
//        specificity += 1;
//
//        // initialize best fit of children
//        Pair<Identifier, Integer> bestpair = new Pair<>(item.getIdentifier(), specificity);
//
//        // get best fit of children (if it has any)
//        for(NbtItem child : item.getChildren()) {
//            if(child.softMatches(stack)) {
//                // get it's fit
//                Pair<Identifier, Integer> childFit = getBestFit(child, stack, specificity);
//                // if it's more than the current best fit, update it
//                if(childFit.getRight() > bestpair.getRight()) bestpair = childFit;
//            }
//        }
//
//        // return the best fit
//        return bestpair;
//    }

    public static void onChange(ConfigItem<Boolean> item) {
//        System.out.println(item);
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

    /**
     * creates suggestions of all item registry identifiers + all nbt item identifiers
     */
    public static class ItemModuleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        private final boolean negators;
        private final NbtMatcher.Type typeShown;

        public ItemModuleSuggestionProvider() { this(false, NbtMatcher.Type.ITEM); }

        public ItemModuleSuggestionProvider(boolean negators, NbtMatcher.Type typeShown) {
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
}
