package mods.banana.economy2.chestshop.modules;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JsonNbtItem {
    private final String item;
    private final String identifier;
    private final String tag;
    private final String parent;

    public JsonNbtItem(String item, String identifier, String tag, String parent) {
        this.item = item;
        this.identifier = identifier;
        this.tag = tag;
        this.parent = parent;
    }

    public NbtItem toNbtItem() throws CommandSyntaxException {
        if(parent != null)
            return new NbtItem(Registry.ITEM.get(new Identifier(item)), new Identifier(identifier), new Identifier(parent), StringNbtReader.parse(tag));
        else
            return new NbtItem(Registry.ITEM.get(new Identifier(item)), new Identifier(identifier), StringNbtReader.parse(tag));
    }
}
