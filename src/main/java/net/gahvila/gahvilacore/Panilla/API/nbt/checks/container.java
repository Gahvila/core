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

import java.util.HashMap;
import java.util.Map;

public class container extends NbtCheck {

    public container() {
        super("minecraft:container", PStrictness.LENIENT);
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
        NbtTagList items = tag.getList(getName(), NbtDataType.COMPOUND);

        int sizeBytes = 0;
        for (int i = 0; i < items.size(); i++) sizeBytes += items.getCompound(i).getStringSizeBytes();
        int maxSizeBytes = panilla.getProtocolConstants().maxBlockEntityTagLengthBytes();

        // ensure BlockEntityTag isn't huge
        if (sizeBytes > maxSizeBytes) {
            return NbtCheckResult.CRITICAL;
        }

        // tiles with items/containers (chests, hoppers, shulkerboxes, etc)
        // only ItemShulkerBoxes should have "Items" NBT tag in survival
        itemName = itemName.toLowerCase();

        if (panilla.getPConfig().noBlockEntityTag) {
            return NbtCheckResult.FAIL;
        }

        if (panilla.getPConfig().strictness == PStrictness.STRICT) {
            if (!(itemName.contains("shulker") || itemName.contains("itemstack") || itemName.contains("itemblock"))) {
                return NbtCheckResult.FAIL;
            }
        }

        FailedBlockEntityTagItemsNbt failedNbt = checkItems(getName(), items, itemName, panilla);

        // Only remove NBT from shulkerbox if it contains a CRITICAL item
        if (failedNbt.critical()) {
            return NbtCheckResult.CRITICAL;
        }

        return NbtCheckResult.PASS;
    }

}
