package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_PaperRange extends NbtCheck {

    public NbtCheck_PaperRange() {
        super("minecraft:custom_data", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
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
