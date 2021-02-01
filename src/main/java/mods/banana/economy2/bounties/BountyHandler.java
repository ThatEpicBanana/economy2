package mods.banana.economy2.bounties;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BountyHandler {
    private final List<Bounty> bounties;

    public BountyHandler(List<Bounty> bounties) {
        this.bounties = bounties;
    }

    public void add(Bounty bounty) { bounties.add(bounty); }
    public List<Bounty> getBounties() { return bounties; }

    public static class Serializer implements JsonSerializer<BountyHandler>, JsonDeserializer<BountyHandler> {
        public static final Gson GSON;

        @Override
        public BountyHandler deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<Bounty> bounties = new ArrayList<>();

            for(JsonElement element : json.getAsJsonArray()) {
                bounties.add(context.deserialize(element, Bounty.class));
            }

            return new BountyHandler(bounties);
        }

        @Override
        public JsonElement serialize(BountyHandler src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();

            for(Bounty bounty : src.getBounties()) {
                array.add(context.serialize(bounty));
            }

            return array;
        }

        static {
            GSON = new GsonBuilder()
                    .registerTypeAdapter(BountyHandler.class, new BountyHandler.Serializer())
                    .registerTypeAdapter(Bounty.class, new Bounty.Serializer())
                    .setPrettyPrinting()
                    .create();
        }
    }
}
