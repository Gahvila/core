package net.gahvila.gahvilacore.Panilla.API.exception;

import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;

import java.util.ArrayList;

public class FailedNbtList extends ArrayList<FailedNbt> {

    private FailedNbt criticalFailedNbt;

    @Override
    public boolean add(FailedNbt failedNbt) {
        if (failedNbt.result == NbtCheck.NbtCheckResult.CRITICAL) {
            this.criticalFailedNbt = failedNbt;
        }
        return super.add(failedNbt);
    }

    public boolean containsCritical() {
        return criticalFailedNbt != null;
    }

    public FailedNbt getCritical() {
        return criticalFailedNbt;
    }

    public FailedNbt findFirstNonCritical() {
        for (FailedNbt failedNbt : this) {
            if (failedNbt.result == NbtCheck.NbtCheckResult.FAIL) {
                return failedNbt;
            }
        }
        return null;
    }

}
