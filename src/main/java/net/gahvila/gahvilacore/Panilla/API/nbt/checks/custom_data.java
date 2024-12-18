package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class custom_data extends NbtCheck {

    public custom_data() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound(getName());

        if (tag.hasKey("weBrushJson") && panilla.getPConfig().preventFaweBrushNbt) return NbtCheckResult.FAIL;

        if (tag.hasKeyOfType("Paper.Range", NbtDataType.DOUBLE)
                && tag.getDouble("Paper.Range") > 2048) return NbtCheckResult.CRITICAL;
        return NbtCheckResult.PASS;
    }

}
