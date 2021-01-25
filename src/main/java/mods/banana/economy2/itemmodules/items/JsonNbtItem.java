package mods.banana.economy2.itemmodules.items;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JsonNbtItem {
    private final String identifier;
    private final String predicate;
    private final String parent;

    public JsonNbtItem(String identifier, String predicate, String parent) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = parent;
    }

    public JsonNbtItem(String identifier, String predicate) {
        this.identifier = identifier;
        this.predicate = predicate;
        this.parent = null;
    }

    public NbtItem toNbtItem() {
        return new NbtItem(new Identifier(identifier), new Identifier(predicate), parent != null ? new Identifier(parent) : null);
    }
}
