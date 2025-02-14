/*
 * MIT License
 *
 * Copyright (c) 2019 Ruinscraft, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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