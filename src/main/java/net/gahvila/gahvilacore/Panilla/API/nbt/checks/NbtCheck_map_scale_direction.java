package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_map_scale_direction extends NbtCheck {

    public NbtCheck_map_scale_direction() {
        super("map_scale_direction", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        return NbtCheckResult.PASS;
    }

}
