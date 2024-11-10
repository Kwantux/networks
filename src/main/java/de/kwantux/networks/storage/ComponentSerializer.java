package de.kwantux.networks.storage;

import com.google.gson.*;
import de.kwantux.networks.component.NetworkComponent;
import de.kwantux.networks.component.ComponentType;
import de.kwantux.networks.component.component.SortingContainer;
import de.kwantux.networks.component.util.FilterTranslator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;

import static de.kwantux.networks.Main.logger;

public class ComponentSerializer implements JsonDeserializer<NetworkComponent>, JsonSerializer<NetworkComponent> {

    private  final Gson gson = new GsonBuilder().create();

    @Override
    public @Nullable NetworkComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ComponentType type = ComponentType.get(jsonObject.get("type").getAsString());
        if (type == null) throw new JsonParseException("Error while deserializing network component. Unknown type: " + jsonObject.get("type").getAsString());

        // Compat with old versions
        if (type.equals(ComponentType.SORTING)) {
            if (!jsonObject.get("filters").getAsJsonArray().isEmpty()) {
            try {
                jsonObject.get("filters").getAsJsonArray().get(0).getAsInt();
            } catch (NumberFormatException e) {
                jsonObject.add("filters", gson.toJsonTree(
                        SortingContainer.convertLegacyFilters(
                                jsonObject.get("filters").getAsJsonArray().asList().stream().map(
                                    (jsonPrimitive) -> jsonPrimitive.getAsString()
                                ).toArray(String[]::new)
                        )
                ));
            }

            }
        }

        Class<? extends NetworkComponent> clazz = type.clazz;
        NetworkComponent component = gson.fromJson(json, clazz);
        if (!component.fillMissingData()) {
            logger.severe("A network component is missing crucial data. It will be skipped during loading phase.");
            logger.severe("If you did not manually edit the network files, report this to the Networks developers.");
            return null;
        }
        return component;
    }

    @Override
    public JsonElement serialize(NetworkComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = gson.toJsonTree(src);
        jsonElement.getAsJsonObject().addProperty("type", ComponentType.get(src.getClass()).tag());
        return jsonElement;
    }
}
