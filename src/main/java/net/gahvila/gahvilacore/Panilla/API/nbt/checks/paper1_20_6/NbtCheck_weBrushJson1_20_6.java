package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_weBrushJson1_20_6 extends NbtCheck {

    public NbtCheck_weBrushJson1_20_6() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        if (tag.hasKey("weBrushJson") && panilla.getPConfig().preventFaweBrushNbt) {
            return NbtCheckResult.FAIL;
        } else {
            return NbtCheckResult.PASS;
        }
    }

}
