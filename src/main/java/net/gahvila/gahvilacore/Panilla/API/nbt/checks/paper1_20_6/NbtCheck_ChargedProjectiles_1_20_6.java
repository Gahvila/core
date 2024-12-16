package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_ChargedProjectiles_1_20_6 extends NbtCheck {

    public NbtCheck_ChargedProjectiles_1_20_6() {
        super("minecraft:charged_projectiles", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;
        INbtTagList chargedProjectiles = tag.getList(getName(), NbtDataType.COMPOUND);

        for (int i = 0; i < chargedProjectiles.size(); i++) {
            INbtTagCompound chargedProjectile = chargedProjectiles.getCompound(i);

            if (chargedProjectile.hasKey("components")) {
                INbtTagCompound chargedProjectileTag = chargedProjectile.getCompound("components");

                if (chargedProjectileTag.hasKey("potion_contents")) {
                    String potion = chargedProjectileTag.getCompound("potion_contents").getString("potion");

                    if (potion.endsWith("empty")) {
                        result = NbtCheckResult.FAIL;
                        break;
                    }
                }
            }
        }

        return result;
    }

}
