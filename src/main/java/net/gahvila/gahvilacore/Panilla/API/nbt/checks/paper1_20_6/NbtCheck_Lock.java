package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_Lock extends NbtCheck {

    public NbtCheck_Lock() {
        super("minecraft:lock", PStrictness.STRICT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        return NbtCheckResult.FAIL;
    }

}
