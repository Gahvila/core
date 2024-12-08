package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Gondom.API.nbt.NbtDataType;

public class NbtCheck_Fireworks extends NbtCheck {

    public NbtCheck_Fireworks() {
        super("Fireworks", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;
        INbtTagCompound fireworks = tag.getCompound("Fireworks");

        int flight = fireworks.getInt("Flight");

        if (flight > panilla.getProtocolConstants().maxFireworksFlight()
                || flight < panilla.getProtocolConstants().minFireworksFlight()) {
            result = NbtCheckResult.FAIL;
        }

        INbtTagList explosions = fireworks.getList("Explosions", NbtDataType.COMPOUND);

        if (explosions != null
                && explosions.size() > panilla.getProtocolConstants().maxFireworksExplosions()) {
            result = NbtCheckResult.FAIL;
        }

        return result;
    }

}
