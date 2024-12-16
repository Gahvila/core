package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_CustomPotionColor extends NbtCheck {

    public NbtCheck_CustomPotionColor() {
        super("CustomPotionColor", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        int bgr = tag.getInt(getName());
        boolean validColor = true;  // TODO:
        if (!validColor) {
            return NbtCheckResult.CRITICAL;
        }
        return NbtCheckResult.PASS;
    }

}
