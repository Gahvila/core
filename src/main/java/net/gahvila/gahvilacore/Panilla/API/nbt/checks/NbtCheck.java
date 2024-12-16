package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.nbt.INbtTagCompound;
import net.gahvila.gahvilacore.Panilla.PanillaPlugin;

public abstract class NbtCheck {

    private final String name;
    private final PStrictness tolerance;
    private final String[] aliases;

    public NbtCheck(String name, PStrictness tolerance, String... aliases) {
        this.name = name;
        this.tolerance = tolerance;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public PStrictness getTolerance() {
        return tolerance;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract NbtCheckResult check(INbtTagCompound tag, String itemName, PanillaPlugin panilla);

    public enum NbtCheckResult {
        PASS,
        FAIL,
        CRITICAL
    }

}
