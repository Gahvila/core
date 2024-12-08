package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Gondom.API.nbt.NbtDataType;

public class NbtCheck_ChargedProjectiles extends NbtCheck {

    public NbtCheck_ChargedProjectiles() {
        super("ChargedProjectiles", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
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
