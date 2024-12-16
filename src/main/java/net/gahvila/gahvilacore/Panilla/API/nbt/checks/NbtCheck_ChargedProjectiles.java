package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_ChargedProjectiles extends NbtCheck {

    public NbtCheck_ChargedProjectiles() {
        super("ChargedProjectiles", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;
        INbtTagList chargedProjectiles = tag.getList("ChargedProjectiles", NbtDataType.COMPOUND);

        for (int i = 0; i < chargedProjectiles.size(); i++) {
            INbtTagCompound chargedProjectile = chargedProjectiles.getCompound(i);

            if (chargedProjectile.hasKey("tag")) {
                INbtTagCompound chargedProjectileTag = chargedProjectile.getCompound("tag");

                if (chargedProjectileTag.hasKey("Potion")) {
                    String potion = chargedProjectileTag.getString("Potion");

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
