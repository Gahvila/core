package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class block_state extends NbtCheck {

    public block_state() {
        super("minecraft:block_state", PStrictness.STRICT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        return NbtCheckResult.FAIL;   // TODO: test variations of BlockStateTag to see what is potentially malicious
    }

}
