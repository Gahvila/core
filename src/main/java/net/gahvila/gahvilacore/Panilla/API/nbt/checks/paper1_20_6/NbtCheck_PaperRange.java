package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_PaperRange extends NbtCheck {

    public NbtCheck_PaperRange() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
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
