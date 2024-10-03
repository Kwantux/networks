package de.kwantux.networks.storage;

import com.google.gson.*;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.component.ComponentType;

import java.lang.reflect.Type;

public class ComponentSerializer implements JsonDeserializer<NetworkComponent>, JsonSerializer<NetworkComponent> {

    private  final Gson gson = new GsonBuilder().create();

    @Override
    public NetworkComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ComponentType type = ComponentType.get(jsonObject.get("type").getAsString());
        if (type == null) throw new JsonParseException("Error while deserializing network component. Unknown type: " + jsonObject.get("type").getAsString());
        Class<? extends NetworkComponent> clazz = type.clazz;
        return gson.fromJson(json, clazz);
    }

    @Override
    public JsonElement serialize(NetworkComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = gson.toJsonTree(src);
        jsonElement.getAsJsonObject().addProperty("type", ComponentType.get(src.getClass()).tag());
        return jsonElement;
    }
}
