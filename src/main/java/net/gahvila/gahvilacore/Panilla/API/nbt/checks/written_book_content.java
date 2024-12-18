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

import static net.gahvila.gahvilacore.Panilla.API.nbt.checks.writable_book_content.createCharMap;

public class written_book_content extends NbtCheck {

    public written_book_content() {
        super("minecraft:written_book_content", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound("minecraft:written_book_content");

        final int titleLength = tag.getCompound("title").getString("raw").length();

        if (panilla.getPConfig().strictness == PStrictness.STRICT) {
            if (titleLength > panilla.getProtocolConstants().maxBookTitleLength()) {
                return NbtCheckResult.CRITICAL;
            }
        } else {
            if (titleLength > panilla.getProtocolConstants().maxItemNameLength()) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (tag.getString("author").length() > panilla.getProtocolConstants().maxUsernameLength())
            return NbtCheckResult.FAIL;

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

}
