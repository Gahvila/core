package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_Explosion extends NbtCheck {

    public NbtCheck_Explosion() {
        super("Explosion", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        return NbtCheckResult.PASS;
    }

}
