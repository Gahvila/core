package net.gahvila.gahvilacore.Gondom.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.checks.NbtCheck;

public class NbtCheck_CustomModelData_1_20_6 extends NbtCheck {

    public NbtCheck_CustomModelData_1_20_6() {
        super("minecraft:custom_model_data", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        try {
            tag.getInt("minecraft:custom_model_data");
        } catch (Exception e) {
            return NbtCheckResult.CRITICAL;
        }

        return NbtCheckResult.PASS;
    }

}
