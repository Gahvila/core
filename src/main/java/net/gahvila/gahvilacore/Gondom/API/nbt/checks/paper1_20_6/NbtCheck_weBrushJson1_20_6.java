package net.gahvila.gahvilacore.Gondom.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.checks.NbtCheck;

public class NbtCheck_weBrushJson1_20_6 extends NbtCheck {

    public NbtCheck_weBrushJson1_20_6() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        if (tag.hasKey("weBrushJson") && panilla.getPConfig().preventFaweBrushNbt) {
            return NbtCheckResult.FAIL;
        } else {
            return NbtCheckResult.PASS;
        }
    }

}
