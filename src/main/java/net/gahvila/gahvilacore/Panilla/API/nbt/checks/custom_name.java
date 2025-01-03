package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class custom_name extends NbtCheck {

    public final JsonParser PARSER = new JsonParser();

    public custom_name() {
        super("minecraft:custom_name", PStrictness.LENIENT);
    }

    private static String createTextFromJsonArray(JsonArray jsonArray) {
        StringBuilder text = new StringBuilder();

        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject()) text.append(jsonElement.getAsJsonObject().get("text").getAsString());
        }

        return text.toString();
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        if (tag.hasKeyOfType(getName(), NbtDataType.STRING)) {
            String name = tag.getString(getName());

            // check for Json array
            if (name.startsWith("[{")) {
                try {
                    JsonElement jsonElement = PARSER.parse(name);
                    JsonArray jsonArray = jsonElement.getAsJsonArray();

                    name = createTextFromJsonArray(jsonArray);
                } catch (Exception e) {
                    // could not parse Json
                }
            }

            // check for Json object
            else if (name.startsWith("{")) {
                try {
                    JsonElement jsonElement = PARSER.parse(name);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("extra");

                    if (jsonArray != null) {
                        name = createTextFromJsonArray(jsonArray);
                    }
                } catch (Exception e) {
                    // could not parse Json
                    return NbtCheckResult.CRITICAL;  // can cause crashes
                }
            }

            final int maxNameLength;

            // if strict, use anvil length
            if (panilla.getPConfig().strictness.ordinal() >= PStrictness.STRICT.ordinal()) {
                maxNameLength = panilla.getProtocolConstants().maxAnvilRenameChars();
            } else {
                maxNameLength = panilla.getProtocolConstants().maxItemNameLength();
            }

            if (name.length() > maxNameLength) {
                return NbtCheckResult.CRITICAL; // can cause crashes
            }
        }

        return NbtCheckResult.PASS;
    }

}