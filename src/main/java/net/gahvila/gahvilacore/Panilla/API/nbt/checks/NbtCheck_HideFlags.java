package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_HideFlags extends NbtCheck {

    public NbtCheck_HideFlags() {
        super("HideFlags", PStrictness.AVERAGE, "minecraft:hide_tooltip", "minecraft:hide_additional_tooltip");
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        return NbtCheckResult.FAIL;
    }

}
