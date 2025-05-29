package de.kwantux.networks.storage;

import com.google.gson.*;
import de.kwantux.networks.utils.Origin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static de.kwantux.networks.storage.Storage.gson;

public class OriginSerializer implements JsonDeserializer<Origin>, JsonSerializer<Origin> {

    @Override
    public @Nullable Origin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Class<? extends Origin> clazz = Origin.classes.get(jsonObject.get("type").getAsString());
        if (clazz == null) throw new JsonParseException("Error while deserializing network component. Unknown type: " + jsonObject.get("type").getAsString());
        return gson.fromJson(json, clazz);
    }

    @Override
    public JsonElement serialize(Origin src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = gson.toJsonTree(src);
        jsonElement.getAsJsonObject().addProperty("type", Origin.tags.get(src.getClass()));
        return jsonElement;
    }
}
