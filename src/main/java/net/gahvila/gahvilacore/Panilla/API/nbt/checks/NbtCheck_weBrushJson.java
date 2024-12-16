package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_weBrushJson extends NbtCheck {

    public NbtCheck_weBrushJson() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        if (tag.hasKey("weBrushJson") && panilla.getPConfig().preventFaweBrushNbt) {
            return NbtCheckResult.FAIL;
        } else {
            return NbtCheckResult.PASS;
        }
    }

}
