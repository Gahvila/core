package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_SkullProfile extends NbtCheck {

    // See: CraftMetaSkull, GameProfile
    public NbtCheck_SkullProfile() {
        super("SkullProfile", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        NbtTagCompound skullProfile = tag.getCompound("SkullProfile");

        // Avoid "Name and ID cannot both be blank" in GameProfile constructor
        if (!(skullProfile.hasKey("Name") && skullProfile.hasKey("Id"))) {
            return NbtCheckResult.CRITICAL;
        }

        return NbtCheckResult.PASS;
    }

}
