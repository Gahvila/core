package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_BlockStateTag extends NbtCheck {

    // introduced in 1.14
    public NbtCheck_BlockStateTag() {
        super("BlockStateTag", PStrictness.STRICT, "minecraft:block_state");
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        return NbtCheckResult.FAIL;   // TODO: test variations of BlockStateTag to see what is potentially malicious
    }

}
