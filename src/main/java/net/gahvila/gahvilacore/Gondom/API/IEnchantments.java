package net.gahvila.gahvilacore.Gondom.API;

public interface IEnchantments {

    int getMaxLevel(EnchantmentCompat enchCompat);

    int getStartLevel(EnchantmentCompat enchCompat);

    boolean conflicting(EnchantmentCompat enchCompat, EnchantmentCompat _enchCompat);

}
