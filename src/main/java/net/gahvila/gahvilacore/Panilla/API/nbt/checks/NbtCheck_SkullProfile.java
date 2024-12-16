package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_SkullProfile extends NbtCheck {

    // See: CraftMetaSkull, GameProfile
    public NbtCheck_SkullProfile() {
        super("SkullProfile", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        INbtTagCompound skullProfile = tag.getCompound("SkullProfile");

        // Avoid "Name and ID cannot both be blank" in GameProfile constructor
        if (!(skullProfile.hasKey("Name") && skullProfile.hasKey("Id"))) {
            return NbtCheckResult.CRITICAL;
        }

        return NbtCheckResult.PASS;
    }

}
