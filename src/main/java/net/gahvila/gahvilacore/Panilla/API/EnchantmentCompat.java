package net.gahvila.gahvilacore.Panilla.API;

public enum EnchantmentCompat {

    AQUA_AFFINITY("minecraft:aqua_affinity"),
    BANE_OF_ARTHROPODS("minecraft:bane_of_arthropods"),
    BINDING_CURSE("minecraft:binding_curse"),
    BLAST_PROTECTION("minecraft:blast_protection"),
    CHANNELING("minecraft:channeling"), // 1.13
    DEPTH_STRIDER("minecraft:depth_strider"),
    EFFICIENCY("minecraft:efficiency"),
    FEATHER_FALLING("minecraft:feather_falling"),
    FIRE_ASPECT("minecraft:fire_aspect"),
    FIRE_PROTECTION("minecraft:fire_protection"),
    FLAME("minecraft:flame"),
    FORTUNE("minecraft:fortune"),
    FROST_WALKER("minecraft:frost_walker"),
    IMPALING("minecraft:impaling"), // 1.13
    INFINITY("minecraft:infinity"),
    KNOCKBACK("minecraft:knockback"),
    LOOTING("minecraft:looting"),
    LOYALTY("minecraft:loyalty"), // 1.13
    LUCK_OF_THE_SEA("minecraft:luck_of_the_sea"),
    LURE("minecraft:lure"),
    MENDING("minecraft:mending"),
    POWER("minecraft:power"),
    PROJECTILE_PROTECTION("minecraft:projectile_protection"),
    PROTECTION("minecraft:protection"),
    PUNCH("minecraft:punch"),
    RESPIRATION("minecraft:respiration"),
    RIPTIDE("minecraft:riptide"), // 1.13
    SHARPNESS("minecraft:sharpness"),
    SILK_TOUCH("minecraft:silk_touch"),
    SMITE("minecraft:smite"),
    SWEEPING("minecraft:sweeping"),
    THORNS("minecraft:thorns"),
    UNBREAKING("minecraft:unbreaking"),
    VANISHING_CURSE("minecraft:vanishing_curse"),

    // 1.14
    MULTISHOT("minecraft:multishot"),
    PIERCING("minecraft:piercing"),
    QUICK_CHARGE("minecraft:quick_charge"),

    // 1.16
    SOUL_SPEED("minecraft:soul_speed"),

    // 1.19
    SWIFT_SNEAK("minecraft:swift_sneak"),

    // 1.21
    DENSITY("minecraft:density"),
    BREACH("minecraft:breach"),
    WIND_BURST("minecraft:wind_burst"),

    ;

    public final String namedKey;

    EnchantmentCompat(String namedKey) {
        this.namedKey = namedKey;
    }

    public static EnchantmentCompat getByNamedKey(String namedKey) {
        namedKey = namedKey.toLowerCase();

        for (EnchantmentCompat enchantmentCompat : EnchantmentCompat.values()) {
            if (enchantmentCompat.namedKey.contains(namedKey)) {
                return enchantmentCompat;
            }
        }

        return null;
    }
}
