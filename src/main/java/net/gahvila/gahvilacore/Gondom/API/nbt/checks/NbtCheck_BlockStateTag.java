package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_BlockStateTag extends NbtCheck {

    // introduced in 1.14
    public NbtCheck_BlockStateTag() {
        super("BlockStateTag", PStrictness.STRICT, "minecraft:block_state");
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        return NbtCheckResult.FAIL;   // TODO: test variations of BlockStateTag to see what is potentially malicious
    }

}
