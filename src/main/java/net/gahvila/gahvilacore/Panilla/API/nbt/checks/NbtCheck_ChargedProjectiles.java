package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_ChargedProjectiles extends NbtCheck {

    public NbtCheck_ChargedProjectiles() {
        super("minecraft:charged_projectiles", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;
        NbtTagList chargedProjectiles = tag.getList(getName(), NbtDataType.COMPOUND);

        for (int i = 0; i < chargedProjectiles.size(); i++) {
            NbtTagCompound chargedProjectile = chargedProjectiles.getCompound(i);

            if (chargedProjectile.hasKey("components")) {
                NbtTagCompound chargedProjectileTag = chargedProjectile.getCompound("components");

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
