package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_AttributeModifiers extends NbtCheck {

    public NbtCheck_AttributeModifiers() {
        super("AttributeModifiers", PStrictness.AVERAGE, "minecraft:attribute_modifiers");
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        return NbtCheckResult.FAIL;
    }

}