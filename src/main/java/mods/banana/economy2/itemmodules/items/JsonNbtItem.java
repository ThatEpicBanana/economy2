package mods.banana.economy2.itemmodules.items;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JsonNbtItem {
    private String type = "item";
    private final String item;
    private final String identifier;
    private final String tag;
    private final String parent;

    public JsonNbtItem(String type, String item, String identifier, String tag, String parent) {
        this.type = type;
        this.item = item;
        this.identifier = identifier;
        this.tag = tag;
        this.parent = parent;
    }

    public JsonNbtItem(String type, String identifier, String tag, String parent) {
        this.type = type;
        this.item = null;
        this.identifier = identifier;
        this.tag = tag;
        this.parent = parent;
    }

    public NbtItem toNbtItem() throws CommandSyntaxException {
        return new NbtItem(
                type == null || type.equals("item") ? NbtItem.ItemType.ITEM : NbtItem.ItemType.MODIFIER,
                new Identifier(identifier),
                StringNbtReader.parse(tag),
                item != null ? Registry.ITEM.get(new Identifier(item)) : null,
                parent != null ? new Identifier(parent) : null
        );
//        if(type.equals("modifier"))
//            return new NbtItem(new Identifier(identifier), new Identifier(parent), StringNbtReader.parse(tag));
//        else if(type.equals("item"))
//            return new NbtItem(Registry.ITEM.get(new Identifier(item)), new Identifier(identifier), new Identifier(parent), StringNbtReader.parse(tag));
    }
}
