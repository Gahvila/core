package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_SkullProfile extends NbtCheck {

    // See: CraftMetaSkull, GameProfile
    public NbtCheck_SkullProfile() {
        super("SkullProfile", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        INbtTagCompound skullProfile = tag.getCompound("SkullProfile");

        // Avoid "Name and ID cannot both be blank" in GameProfile constructor
        if (!(skullProfile.hasKey("Name") && skullProfile.hasKey("Id"))) {
            return NbtCheckResult.CRITICAL;
        }

        return NbtCheckResult.PASS;
    }

}
