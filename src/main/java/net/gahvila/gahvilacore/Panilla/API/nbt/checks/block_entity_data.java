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

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedBlockEntityTagItemsNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtChecks;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;
import net.minecraft.world.item.component.WritableBookContent;

import java.util.HashMap;
import java.util.Map;

public class block_entity_data extends NbtCheck {

    public block_entity_data() {
        super("minecraft:block_entity_data", PStrictness.STRICT);
    }

    public static FailedBlockEntityTagItemsNbt checkItems(String nbtTagName, NbtTagList items, String itemName, Panilla panilla) {
        int charCount = writable_book_content.getCharCountForItems(items);

        if (charCount > 100_000) {
            return new FailedBlockEntityTagItemsNbt(nbtTagName, NbtCheck.NbtCheckResult.CRITICAL);
        }

        Map<Integer, FailedNbt> itemFails = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            FailedNbtList failedNbtList = checkItem(items.getCompound(i), itemName, panilla);

            for (FailedNbt failedNbt : failedNbtList) {
                if (FailedNbt.fails(failedNbt)) {
                    itemFails.put(i, failedNbt);
                }
            }
        }

        if (!itemFails.isEmpty()) {
            return new FailedBlockEntityTagItemsNbt(nbtTagName, NbtCheckResult.FAIL, itemFails);
        }

        return new FailedBlockEntityTagItemsNbt(nbtTagName, NbtCheckResult.PASS);
    }

    public static FailedNbtList checkItem(NbtTagCompound item, String itemName, Panilla panilla) {
        if (item.hasKey("tag")) {
            return NbtChecks.checkAll(item.getCompound("tag"), itemName, panilla);
        } else {
            return new FailedNbtList();
        }
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound(getName());

        int sizeBytes = tag.getStringSizeBytes();
        int maxSizeBytes = panilla.getProtocolConstants().maxBlockEntityTagLengthBytes();
        if (sizeBytes > maxSizeBytes) return NbtCheckResult.CRITICAL;

        if (tag.hasKey("LootTable")) {
            String lootTable = tag.getString("LootTable").trim();
            if (lootTable.isEmpty() || lootTable.split(":").length < 2) return NbtCheckResult.CRITICAL;
        }

        if (panilla.getPConfig().strictness == PStrictness.STRICT) {
            if (tag.hasKey("Lock") || tag.hasKey("front_text") || tag.hasKey("back_text")) return NbtCheckResult.FAIL;
        }

        if (tag.hasKey("Items")) {
            itemName = itemName.toLowerCase();

            if (panilla.getPConfig().noBlockEntityTag || (panilla.getPConfig().strictness == PStrictness.STRICT
                    && !(itemName.contains("shulker") || itemName.contains("itemstack") || itemName.contains("itemblock")))
                    || itemName.contains("campfire")) return NbtCheckResult.FAIL;

            NbtTagList items = tag.getList("Items", NbtDataType.COMPOUND);
            FailedBlockEntityTagItemsNbt failedNbt = checkItems(getName(), items, itemName, panilla);
            if (failedNbt.critical()) return NbtCheckResult.CRITICAL;
        }

        if (itemName.equals("jukebox")) {
            NbtTagCompound item = tag.getCompound("RecordItem");
            FailedNbtList failedNbtList = checkItem(item, itemName, panilla);
            return failedNbtList.containsCritical() ? NbtCheckResult.CRITICAL :
                    (failedNbtList.findFirstNonCritical() != null ? NbtCheckResult.FAIL : null);
        }

        if (tag.hasKey("cursors")) return NbtCheckResult.FAIL;

        return NbtCheckResult.PASS;
    }

}
