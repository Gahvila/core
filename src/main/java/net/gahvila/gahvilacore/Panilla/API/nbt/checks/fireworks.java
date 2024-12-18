package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class fireworks extends NbtCheck {

    public fireworks() {
        super("minecraft:fireworks", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;
        NbtTagCompound fireworks = tag.getCompound("minecraft:fireworks");

        int flight = fireworks.getInt("flight_duration");

        if (flight > panilla.getProtocolConstants().maxFireworksFlight()
                || flight < panilla.getProtocolConstants().minFireworksFlight()) {
            result = NbtCheckResult.FAIL;
        }

        NbtTagList explosions = fireworks.getList("explosions", NbtDataType.COMPOUND);

        if (explosions != null
                && explosions.size() > panilla.getProtocolConstants().maxFireworksExplosions()) {
            result = NbtCheckResult.FAIL;
        }

        return result;
    }

}
