package net.gahvila.gahvilacore.Gondom.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Gondom.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Gondom.API.nbt.checks.NbtCheck;

public class NbtCheck_Fireworks extends NbtCheck {

    public NbtCheck_Fireworks() {
        super("minecraft:fireworks", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;
        INbtTagCompound fireworks = tag.getCompound("minecraft:fireworks");

        int flight = fireworks.getInt("flight_duration");

        if (flight > panilla.getProtocolConstants().maxFireworksFlight()
                || flight < panilla.getProtocolConstants().minFireworksFlight()) {
            result = NbtCheckResult.FAIL;
        }

        INbtTagList explosions = fireworks.getList("explosions", NbtDataType.COMPOUND);

        if (explosions != null
                && explosions.size() > panilla.getProtocolConstants().maxFireworksExplosions()) {
            result = NbtCheckResult.FAIL;
        }

        return result;
    }

}
