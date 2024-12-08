package net.gahvila.gahvilacore.Gondom.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Gondom.API.nbt.checks.NbtCheck;

public class NbtCheck_PaperRange extends NbtCheck {

    public NbtCheck_PaperRange() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        tag = tag.getCompound(getName());

        if (tag.hasKeyOfType("Paper.Range", NbtDataType.DOUBLE)) {
            double paperRange = tag.getDouble("Paper.Range");
            if (paperRange > 2048) {
                return NbtCheckResult.CRITICAL;
            }
        }
        return NbtCheckResult.PASS;
    }

}
