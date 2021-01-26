package mods.banana.economy2.itemmodules.items;

import mods.banana.economy2.Economy2;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class JsonNbtItem {
    private final Identifier identifier;
    private final Identifier predicate;
    private final String item;
    @Nullable
    private final Identifier parent;

    public JsonNbtItem(Identifier identifier, Identifier predicate, @Nullable String item, @Nullable Identifier parent) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
        this.item = item;
    }

    public JsonNbtItem(String identifier, String predicate, String item, String parent) {
        this(new Identifier(identifier), new Identifier(predicate), item, parent != null ? new Identifier(parent) : null);
    }

    public NbtMatcher toNbtItem() {
        if(item == null || item.startsWith("#")) { // modifiers are either declared without an item or with a tag
            return new NbtModifier(new Identifier(item.replaceAll("^#", "")), identifier, predicate, parent);
        } else {
            return new NbtItem(Registry.ITEM.getOrEmpty(new Identifier(item)).orElse(null), identifier, predicate, parent);
        }
    }
}
