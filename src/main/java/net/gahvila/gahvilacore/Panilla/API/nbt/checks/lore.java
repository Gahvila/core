package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class lore extends NbtCheck {

    public lore() {
        super("minecraft:lore", PStrictness.LENIENT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        if (tag.hasKeyOfType(getName(), NbtDataType.LIST)) {
            NbtTagList lore = tag.getList(getName());

            if (lore.size() > panilla.getProtocolConstants().maxLoreLines()) {
                return NbtCheckResult.CRITICAL; // can cause crashes
            }

            for (int i = 0; i < lore.size(); i++) {
                String line = lore.getString(i);

                if (line.length() > panilla.getProtocolConstants().maxLoreLineLength()) {
                    return NbtCheckResult.CRITICAL; // can cause crashes
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}
