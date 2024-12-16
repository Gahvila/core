package net.gahvila.gahvilacore.Panilla.API.nbt.checks.paper1_20_6;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.API.nbt.checks.NbtCheck;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public class NbtCheck_ContainerLoot extends NbtCheck {

    public NbtCheck_ContainerLoot() {
        super("minecraft:container_loot", PStrictness.STRICT);
    }

    @Override
    public NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla) {
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
