package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_CustomPotionColor extends NbtCheck {

    public NbtCheck_CustomPotionColor() {
        super("CustomPotionColor", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        int bgr = tag.getInt(getName());
        boolean validColor = true;  // TODO:
        if (!validColor) {
            return NbtCheckResult.CRITICAL;
        }
        return NbtCheckResult.PASS;
    }

}
