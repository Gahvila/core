package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_Decorations extends NbtCheck {

    public NbtCheck_Decorations() {
        super("Decorations", PStrictness.AVERAGE);
    }

    // for treasure maps
    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        return NbtCheckResult.PASS;
    }

}
