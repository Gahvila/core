package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_PotionContents extends NbtCheck {

    public NbtCheck_PotionContents() {
        super("minecraft:potion_contents", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound("minecraft:potion_contents");

        if (tag.hasKey("custom_color") && (tag.getInt("custom_color") < 0
                || tag.getInt("custom_color") > 16777215)) return NbtCheckResult.CRITICAL;

        if (tag.hasKey("custom_effects")) {
            NbtTagList effectsList = tag.getList("custom_effects", NbtDataType.COMPOUND);
            for (int i = 0; i < effectsList.size(); i++) {
                NbtTagCompound effect = effectsList.getCompound(i);
                if (effect.hasKeyOfType("amplifier", NbtDataType.BYTE) && effect.getByte("amplifier") > 32) {
                    return NbtCheckResult.CRITICAL;
                }
            }
        }
        return NbtCheckResult.PASS;
    }
}

