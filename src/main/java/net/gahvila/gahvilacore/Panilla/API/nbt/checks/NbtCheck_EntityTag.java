package net.gahvila.gahvilacore.Panilla.API.nbt.checks;

import net.gahvila.gahvilacore.Panilla.API.config.PStrictness;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbt;
import net.gahvila.gahvilacore.Panilla.API.exception.FailedNbtList;
import net.gahvila.gahvilacore.Panilla.API.nbt.NbtDataType;

import java.util.Locale;
import java.util.UUID;

import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagCompound;
import net.gahvila.gahvilacore.Panilla.NMS.nbt.NbtTagList;
import net.gahvila.gahvilacore.Panilla.Panilla;

public class NbtCheck_EntityTag extends NbtCheck {

    private static final String[] ARMOR_STAND_TAGS = new String[]{"NoGravity", "ShowArms", "NoBasePlate", "Small", "Rotation", "Marker", "Pose", "Invisible"};

    public NbtCheck_EntityTag() {
        super("EntityTag", PStrictness.LENIENT);
    }

    private static FailedNbt checkItems(NbtTagList items, String nmsItemClassName, Panilla panilla) {
        FailedNbt failedNbt = null;

        for (int i = 0; i < items.size(); i++) {
            NbtTagCompound item = items.getCompound(i);

            if (item.hasKey("tag")) {
                FailedNbtList failedNbtList = NbtChecks.checkAll(item.getCompound("tag"), nmsItemClassName, panilla);

                if (failedNbtList.containsCritical()) {
                    return failedNbtList.getCritical();
                }

                failedNbt = failedNbtList.findFirstNonCritical();
            }
        }

        return failedNbt;
    }

    @Override
    public NbtCheckResult check(NbtTagCompound tag, String itemName, Panilla panilla) {
        NbtCheckResult result = NbtCheckResult.PASS;

        PStrictness strictness = panilla.getPConfig().strictness;

        NbtTagCompound entityTag = tag.getCompound(getName());

        if (strictness == PStrictness.STRICT) {
            for (String armorStandTag : ARMOR_STAND_TAGS) {
                if (entityTag.hasKey(armorStandTag)) {
                    result = NbtCheckResult.FAIL;
                }
            }
        }

        if (entityTag.hasKey("CustomName")) {
            String customName = entityTag.getString("CustomName");
            if (customName.length() > panilla.getProtocolConstants().NOT_PROTOCOL_maxEntityTagCustomNameLength()) {
                return NbtCheckResult.CRITICAL;
            }
            try {
                panilla.getPacketInspector().validateBaseComponentParse(customName);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (entityTag.hasKey("UUID")) {
            String uuid = entityTag.getString("UUID");

            try {
                UUID.fromString(uuid);
            } catch (Exception e) {
                return NbtCheckResult.CRITICAL;
            }
        }

        if (entityTag.hasKey("ExplosionPower")) {
            result = NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("Invulnerable")) {
            result = NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("Motion")) {
            result = NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("power")) {
            return NbtCheckResult.CRITICAL;
        }

        if (entityTag.hasKey("PuffState")) {
            result = NbtCheckResult.FAIL;
        }

        if (entityTag.hasKeyOfType("AbsorptionAmount", NbtDataType.STRING)) {
            result = NbtCheckResult.FAIL;
        }

        if (entityTag.hasKeyOfType("Health", NbtDataType.FLOAT)) {
            return NbtCheckResult.FAIL;
        }

        if (entityTag.hasKey("ArmorItems")) {
            NbtTagList items = entityTag.getList("ArmorItems", NbtDataType.COMPOUND);

            FailedNbt failedNbt = checkItems(items, itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                if (failedNbt.result == NbtCheckResult.CRITICAL) {
                    return NbtCheckResult.CRITICAL;
                } else {
                    result = NbtCheckResult.FAIL;
                }
            }
        }

        if (entityTag.hasKey("HandItems")) {
            NbtTagList items = entityTag.getList("HandItems", NbtDataType.COMPOUND);

            FailedNbt failedNbt = checkItems(items, itemName, panilla);

            if (FailedNbt.fails(failedNbt)) {
                if (failedNbt.result == NbtCheckResult.CRITICAL) {
                    return NbtCheckResult.CRITICAL;
                } else {
                    result = NbtCheckResult.FAIL;
                }
            }
        }

        boolean hasIdTag = entityTag.hasKey("id");

        if (hasIdTag) {
            if (strictness == PStrictness.STRICT) {
                result = NbtCheckResult.FAIL;
            }

            String id = entityTag.getString("id");
            String normalizedId = id.toLowerCase(Locale.US);

            // prevent lightning bolt eggs
            if (normalizedId.contains("lightning")) {
                result = NbtCheckResult.FAIL;
            }

            // prevent troll elder guardian eggs
            if (normalizedId.contains("elder_guardian")) {
                result = NbtCheckResult.FAIL;
            }

            // check for massive slime spawn eggs
            if (entityTag.hasKeyOfType("Size", NbtDataType.INT)) {
                if (entityTag.getInt("Size") > panilla.getProtocolConstants().maxSlimeSize()) {
                    return NbtCheckResult.CRITICAL;
                }
            }

            // below tags are mostly for EntityAreaEffectCloud
            // see nms.EntityAreaEffectCloud
            if (entityTag.hasKey("Age")) {
                return NbtCheckResult.CRITICAL;
            }

            if (entityTag.hasKeyOfType("Duration", NbtDataType.INT)) {
                int duration = entityTag.getInt("Duration");
                // Block area effect clouds with a duration longer than 15 seconds
                if (duration > (20 * 15)) {
                    return NbtCheckResult.CRITICAL;
                }
            }

            if (entityTag.hasKeyOfType("WaitTime", NbtDataType.INT)) {
                int waitTime = entityTag.getInt("WaitTime");
                // Block area effect clouds with a wait time longer than 5 seconds
                if (waitTime > (20 * 5)) {
                    result = NbtCheckResult.FAIL;
                }
            }

            if (entityTag.hasKey("ReapplicationDelay")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("DurationOnUse")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("RadiusOnUse")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("RadiusPerTick")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Radius")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Particle")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Color")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKey("Potion")) {
                result = NbtCheckResult.FAIL;
            }

            if (entityTag.hasKeyOfType("Effects", NbtDataType.LIST)) {
                NbtTagList effectsList = entityTag.getList("Effects");
                NbtCheckResult effectsResult = checkEffectsTag(effectsList);
                if (effectsResult == NbtCheckResult.CRITICAL) {
                    return NbtCheckResult.CRITICAL;
                } else if (effectsResult == NbtCheckResult.FAIL) {
                    result = NbtCheckResult.FAIL;
                }
            }

            if (entityTag.hasKeyOfType("effects", NbtDataType.LIST)) {
                NbtTagList effectsList = entityTag.getList("effects");
                NbtCheckResult effectsResult = checkEffectsTag(effectsList);
                if (effectsResult == NbtCheckResult.CRITICAL) {
                    return NbtCheckResult.CRITICAL;
                } else if (effectsResult == NbtCheckResult.FAIL) {
                    result = NbtCheckResult.FAIL;
                }
            }
        }

        return result;
    }

    private static NbtCheckResult checkEffectsTag(NbtTagList effectsList) {
        for (int i = 0; i < effectsList.size(); i++) {
            NbtTagCompound effect = effectsList.getCompound(i);

            if (effect.hasKeyOfType("amplifier", NbtDataType.BYTE)) {
                short amplifier = effect.getByte("amplifier");
                if (amplifier > 32) {
                    return NbtCheckResult.CRITICAL;
                }
            }

            if (effect.hasKeyOfType("Amplifier", NbtDataType.BYTE)) {
                short amplifier = effect.getByte("Amplifier");
                if (amplifier > 32) {
                    return NbtCheckResult.CRITICAL;
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}
