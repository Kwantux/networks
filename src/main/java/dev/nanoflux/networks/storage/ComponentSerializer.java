package dev.nanoflux.networks.storage;

import com.google.gson.*;
import dev.nanoflux.networks.component.ComponentType;
import dev.nanoflux.networks.component.NetworkComponent;

import java.lang.reflect.Type;

public class ComponentSerializer implements JsonDeserializer<NetworkComponent>, JsonSerializer<NetworkComponent> {

    private  final Gson gson = new GsonBuilder().create();

    @Override
    public NetworkComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Class<? extends NetworkComponent> clazz = ComponentType.get(jsonObject.get("type").getAsString()).componentClass();
        if (clazz == null) throw new JsonParseException("Error while deserializing network: " + jsonObject.get("name").getAsString());
        return gson.fromJson(json, clazz);
    }

    @Override
    public JsonElement serialize(NetworkComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = gson.toJsonTree(src);
        jsonElement.getAsJsonObject().addProperty("type", ComponentType.get(src.getClass()).tag());
        return jsonElement;
    }
}
