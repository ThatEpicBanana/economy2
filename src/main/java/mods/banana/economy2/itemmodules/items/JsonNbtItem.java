package mods.banana.economy2.itemmodules.items;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class JsonNbtItem {
    private final Identifier identifier;
    private final Identifier predicate;
    @Nullable
    private final String item;
    @Nullable
    private final Identifier parent;
    @Nullable
    private final Identifier accepts;

    public JsonNbtItem(Identifier identifier, Identifier predicate, @Nullable String item, @Nullable Identifier parent, @Nullable Identifier accepts) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
        this.item = item;
        this.accepts = accepts;
    }

    public JsonNbtItem(String identifier, String predicate, String item, String parent, String accepts) {
        this(new Identifier(identifier), new Identifier(predicate), item, parent != null ? new Identifier(parent) : null, accepts != null ? new Identifier(accepts) : null);
    }

    public NbtMatcher toNbtItem() {
        if(item == null || item.startsWith("#")) { // modifiers are either declared without an item or with a tag
            return new NbtModifier(item != null ? new Identifier(item.replaceAll("^#", "")) : null, identifier, predicate, parent, accepts);
        } else {
            return new NbtItem(Registry.ITEM.getOrEmpty(new Identifier(item)).orElse(null), identifier, predicate, parent, accepts);
        }
    }
}
