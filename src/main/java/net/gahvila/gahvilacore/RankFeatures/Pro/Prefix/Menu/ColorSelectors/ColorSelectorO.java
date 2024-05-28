package net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.ColorSelectors;

import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.PrefixManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ColorSelectorO {

    //Prefix menu
    public static void openColorMenuO(Player p){

        Inventory inv = Bukkit.createInventory(p, 18, "§8» §8Värin valinta: §lO" );
        updatePrefixMenu(p, inv);
        p.openInventory(inv);
    }
    public static void updatePrefixMenu(Player player, Inventory inventory) {
        String name = "§" + PrefixManager.getPrefix(player, 4) + player.getName();
        String prefix = "§" + PrefixManager.getPrefix(player, 1) + ChatColor.BOLD + "P" + "§" + PrefixManager.getPrefix(player, 2) + ChatColor.BOLD + "r" + "§" + PrefixManager.getPrefix(player, 3) + ChatColor.BOLD + "o";
        //menu
        inventory.setItem(8, createItem(Material.PAPER, "§fPrefix + nimi:", Arrays.asList(prefix + "§r "+ name)));
        inventory.setItem(17, createItem(Material.BARRIER, "§cTakaisin", Arrays.asList("§7Klikkaa palataksesi takaisin")));
        //colors
        inventory.setItem(0, createItem(Material.BLUE_CONCRETE, "§1Tumma sininen", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(1, createItem(Material.GREEN_CONCRETE, "§2Tumma vihreä", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(2, createItem(Material.CYAN_WOOL, "§3Tumma turkoosi", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(3, createItem(Material.RED_CONCRETE, "§4Tumma punainen", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(4, createItem(Material.PURPLE_CONCRETE, "§5Tumma violetti", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(5, createItem(Material.ORANGE_CONCRETE_POWDER, "§6Kulta", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(6, createItem(Material.GRAY_WOOL, "§8Tumma Harmaa", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(7, createItem(Material.BLACK_CONCRETE, "§0Musta", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(9, createItem(Material.BLUE_WOOL, "§9Sininen", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(10, createItem(Material.LIME_CONCRETE, "§aVihreä", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(11, createItem(Material.LIGHT_BLUE_WOOL, "§bTurkoosi", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(12, createItem(Material.RED_TERRACOTTA, "§cPunainen", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(13, createItem(Material.PINK_CONCRETE, "§dPinkki", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(14, createItem(Material.YELLOW_CONCRETE_POWDER, "§eKeltainen", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(15, createItem(Material.LIGHT_GRAY_WOOL, "§7Harmaa", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        inventory.setItem(16, createItem(Material.WHITE_CONCRETE, "§fValkoinen", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
    }
    private static ItemStack createItem(Material material, String displayname, List<String> lore){
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayname);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
