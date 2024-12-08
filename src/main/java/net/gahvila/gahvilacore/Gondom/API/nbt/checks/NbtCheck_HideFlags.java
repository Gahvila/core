package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_HideFlags extends NbtCheck {

    public NbtCheck_HideFlags() {
        super("HideFlags", PStrictness.AVERAGE, "minecraft:hide_tooltip", "minecraft:hide_additional_tooltip");
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        return NbtCheckResult.FAIL;
    }

}
