package net.gahvila.gahvilacore.Panilla.API.nbt;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

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

    public abstract NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla);

    public enum NbtCheckResult {
        PASS,
        FAIL,
        CRITICAL
    }

}
