package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Gondom.API.nbt.NbtDataType;

public class NbtCheck_CustomPotionEffects extends NbtCheck {

    public NbtCheck_CustomPotionEffects() {
        super("CustomPotionEffects", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        String using = null;

        if (tag.hasKey(getName())) {
            using = getName();
        } else {
            for (String alias : getAliases()) {
                if (tag.hasKey(alias)) {
                    using = alias;
                }
            }
        }

        if (using == null) {
            throw new IllegalStateException("Unknown custom potion effects tag");
        }

        INbtTagList effectsList = tag.getList(using, NbtDataType.COMPOUND);

        for (int i = 0; i < effectsList.size(); i++) {
            INbtTagCompound effect = effectsList.getCompound(i);

            if (effect.hasKeyOfType("amplifier", NbtDataType.BYTE)) {
                short amplifier = effect.getByte("amplifier");
                if (amplifier > 32) {
                    return NbtCheckResult.CRITICAL;
                }
            }

            if (effect.hasKeyOfType("Amplifier", NbtDataType.BYTE)) {
                short amplifier = effect.getByte("Amplifier");
                if (amplifier > 32) {
                    return NbtCheckResult.CRITICAL;
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}
