package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_CustomPotionEffects_1_20_6 extends NbtCheck {

    public NbtCheck_CustomPotionEffects_1_20_6() {
        super("minecraft:potion_contents", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        tag = tag.getCompound("minecraft:potion_contents");

        if (!tag.hasKey("custom_effects")) return NbtCheckResult.PASS;
        INbtTagList effectsList = tag.getList("custom_effects", NbtDataType.COMPOUND);

        for (int i = 0; i < effectsList.size(); i++) {
            INbtTagCompound effect = effectsList.getCompound(i);

            if (effect.hasKeyOfType("amplifier", NbtDataType.BYTE)) {
                short amplifier = effect.getByte("amplifier");
                if (amplifier > 32) {
                    return NbtCheckResult.CRITICAL;
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}
