package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_weBrushJson extends NbtCheck {

    public NbtCheck_weBrushJson() {
        super("weBrushJson", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        if (panilla.getPConfig().preventFaweBrushNbt) {
            return NbtCheckResult.FAIL;
        } else {
            return NbtCheckResult.PASS;
        }
    }

}
