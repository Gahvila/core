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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class writable_book_content extends NbtCheck {

    public writable_book_content() {
        super("minecraft:writable_book_content", PStrictness.AVERAGE);
    }

    private static final int MINECRAFT_UNICODE_MAX = 65535;
    private static final short[] EMPTY_CHAR_MAP = new short[MINECRAFT_UNICODE_MAX];

    public static short[] createCharMap(String string) {
        short[] charMap = Arrays.copyOf(EMPTY_CHAR_MAP, EMPTY_CHAR_MAP.length);

        if (string == null || string.isEmpty()) {
            return charMap;
        }

        for (char c : string.toCharArray()) {
            if (c < MINECRAFT_UNICODE_MAX) {
                charMap[c]++;
            }
        }

        return charMap;
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound("minecraft:writable_book_content");

        NbtTagList pages = tag.getList("pages", NbtDataType.COMPOUND);

        if (pages.size() > panilla.getProtocolConstants().maxBookPages()) {
            return NbtCheckResult.CRITICAL; // too many pages
        }

        // iterate through book pages
        for (int i = 0; i < pages.size(); i++) {
            String page = pages.getCompound(i).getString("raw");
            String pageContent;

            if (page.startsWith("{")) {
                JsonObject data = new JsonParser().parse(page).getAsJsonObject();

                if (data.has("text")) {
                    pageContent = data.get("text").getAsString();
                } else {
                    pageContent = "";
                }

                if (data.has("hoverEvent")) {
                    if (data.get("hoverEvent").getAsString().getBytes(StandardCharsets.UTF_8).length > 32000) {
                        return NbtCheckResult.CRITICAL;
                    }
                }
            } else {
                pageContent = page;
            }

            final String pageNoSpaces = pageContent.replace(" ", "");

            // check char map
            short[] charMap = createCharMap(pageNoSpaces);
            int uniqueChars = 0;

            for (short count : charMap) {
                if (count > 0) {
                    uniqueChars++;
                }
            }

            if (uniqueChars > 180) { // 180 is a reasonable guess for max characters on a book page. It does depend on character size
                return NbtCheckResult.CRITICAL; // most likely a "crash" book
            }
        }

        return NbtCheckResult.PASS;
    }


    private static int getCharCountForItem(NbtTagCompound item) {
        int charCount = 0;

        if (item.hasKey("tag")) {
            NbtTagCompound tag = item.getCompound("tag");

            if (tag.hasKey("pages")) {
                NbtTagList pages = tag.getList("pages", NbtDataType.STRING);

                for (int i = 0; i < pages.size(); i++) {
                    final String page = pages.getString(i);
                    final String pageNoSpaces = page.replace(" ", "");
                    charCount += pageNoSpaces.length();
                }
            }
        }

        return charCount;
    }

    // Gets the amount of characters of books within in a list of items
    public static int getCharCountForItems(NbtTagList items) {
        int charCount = 0;

        for (int i = 0; i < items.size(); i++) {
            charCount += getCharCountForItem(items.getCompound(i));
        }

        return charCount;
    }

}
