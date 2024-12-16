package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_map extends NbtCheck {

    public NbtCheck_map() {
        super("map", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        return NbtCheckResult.PASS;
    }

}
