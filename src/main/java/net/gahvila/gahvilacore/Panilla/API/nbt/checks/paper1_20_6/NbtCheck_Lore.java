package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_Lore extends NbtCheck {

    public NbtCheck_Lore() {
        super("minecraft:lore", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
        if (tag.hasKeyOfType(getName(), NbtDataType.LIST)) {
            INbtTagList lore = tag.getList(getName());

            if (lore.size() > panilla.getProtocolConstants().NOT_PROTOCOL_maxLoreLines()) {
                return NbtCheckResult.CRITICAL; // can cause crashes
            }

            for (int i = 0; i < lore.size(); i++) {
                String line = lore.getString(i);

                if (line.length() > panilla.getProtocolConstants().NOT_PROTOCOL_maxLoreLineLength()) {
                    return NbtCheckResult.CRITICAL; // can cause crashes
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}
