package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class container_loot extends NbtCheck {

    public container_loot() {
        super("minecraft:container_loot", PStrictness.STRICT);
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        tag = tag.getCompound(getName());

        String lootTable = tag.getString("loot_table");

        lootTable = lootTable.trim();

        if (lootTable.isEmpty()) {
            return NbtCheckResult.CRITICAL;
        }

        if (lootTable.contains(":")) {
            String[] keySplit = lootTable.split(":");

            if (keySplit.length < 2) {
                return NbtCheckResult.CRITICAL;
            }

            String namespace = keySplit[0];
            String key = keySplit[1];

            if (namespace.isEmpty() || key.isEmpty()) {
                return NbtCheckResult.CRITICAL;
            }
        }

        return NbtCheckResult.PASS;
    }

}
