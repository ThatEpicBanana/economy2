package mods.banana.economy2.itemmodules.items.accepts;

import com.google.gson.*;
import mods.banana.economy2.itemmodules.items.NbtMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface MatcherAccepts {
    boolean accepts(ItemStack stack);
    boolean accepts(NbtMatcher matcher, Item baseItem);

    class Serializer implements JsonSerializer<MatcherAccepts>, JsonDeserializer<MatcherAccepts> {
        public static Gson GSON = new GsonBuilder()
                .registerTypeAdapter(MatcherAccepts.class, new Serializer())
                .create();

        @Override
        public MatcherAccepts deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            if(object.has("default")) {
                return new DefaultedAccepts(true);
            } else if(object.has("whitelist")) {
                return new ListAccepts(getIds(object.getAsJsonArray("whitelist")), true);
            } else if(object.has("blacklist")) {
                return new ListAccepts(getIds(object.getAsJsonArray("blacklist")), false);
            } else {
                return new DefaultedAccepts(true);
            }
        }

        private List<Identifier> getIds(JsonArray array) {
            ArrayList<Identifier> out = new ArrayList<>();
            for(JsonElement element : array) {
                out.add(new Identifier(element.getAsString()));
            }
            return out;
        }

        public MatcherAccepts fromJson(JsonElement element) {
            return GSON.fromJson(element, MatcherAccepts.class);
        }

        @Override
        public JsonElement serialize(MatcherAccepts src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            if(src instanceof ListAccepts) {
                JsonArray list = new JsonArray();

                for(Identifier i : ((ListAccepts) src).getList()) {
                    list.add(i.toString());
                }

                object.add(((ListAccepts) src).getType() ? "whitelist" : "blacklist", list);
            } else if(src instanceof DefaultedAccepts) {
                object.addProperty("default", ((DefaultedAccepts) src).getDefaultValue());
            } else {
                object.addProperty("default", true);
            }
            return object;
        }
    }
}
