package net.gahvila.gahvilacore.Gondom.API.nbt.checks;

import net.gahvila.gahvilacore.Gondom.API.IPanilla;
import net.gahvila.gahvilacore.Gondom.API.config.PStrictness;
import net.gahvila.gahvilacore.Gondom.API.nbt.INbtTagCompound;

public class NbtCheck_title extends NbtCheck {

    public NbtCheck_title() {
        super("title", PStrictness.AVERAGE);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, IPanilla panilla) {
        final int titleLength = tag.getString("title").length();

        if (panilla.getPConfig().strictness == PStrictness.STRICT) {
            if (titleLength > panilla.getProtocolConstants().maxBookTitleLength()) {
                return NbtCheckResult.CRITICAL;
            }
        } else {
            if (titleLength > panilla.getProtocolConstants().NOT_PROTOCOL_maxItemNameLength()) {
                return NbtCheckResult.CRITICAL;
            }
        }

        return NbtCheckResult.PASS;
    }

}
