package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_author extends NbtCheck {

    public NbtCheck_author() {
        super("author", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        int authorLength = tag.getString("author").length();

        if (authorLength > panilla.getProtocolConstants().maxUsernameLength()) {
            return NbtCheckResult.FAIL;
        } else {
            return NbtCheckResult.PASS;
        }
    }

}
