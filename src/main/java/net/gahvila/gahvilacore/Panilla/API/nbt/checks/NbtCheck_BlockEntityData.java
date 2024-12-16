package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

import static net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck_Container.checkItem;

public class NbtCheck_BlockEntityData extends NbtCheck {

    public NbtCheck_BlockEntityData() {
        super("minecraft:block_entity_data", PStrictness.STRICT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound(getName());

        if (tag.hasKey("front_text") || tag.hasKey("back_text")) return NbtCheckResult.FAIL;
        if (itemName.contains("campfire")) return NbtCheckResult.FAIL;
        if (tag.hasKey("cursors")) return NbtCheckResult.FAIL;
        if (itemName.equals("jukebox")) {
            NbtTagCompound item = tag.getCompound("RecordItem");

            FailedNbtList failedNbtList = checkItem(item, itemName, panilla);

            if (failedNbtList.containsCritical()) {
                return NbtCheckResult.CRITICAL;
            } else if (failedNbtList.findFirstNonCritical() != null) {
                return NbtCheckResult.FAIL;
            }
        }

        return NbtCheckResult.PASS;
    }

}
