package net.gahvila.gahvilacore.Paper.RankFeatures.Pro.Prefix.Menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ProPrefixMainMenu {


    //Prefix menu
    public static void openPrefixMainMenu(Player p){
        Inventory inv = Bukkit.createInventory(p, 27, "§8» §8Prefixmenu" );
        updatePrefixMenu(p, inv);
        p.openInventory(inv);
    }
    public static void updatePrefixMenu(Player player, Inventory inventory) {
        //ITEMIT

        //PLAYER NAME
        switch (PrefixManager.getPrefix(player, 4)){
            case "0" -> inventory.setItem(15, createItem(Material.BLACK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "1" -> inventory.setItem(15, createItem(Material.BLUE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "2" -> inventory.setItem(15, createItem(Material.GREEN_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "3" -> inventory.setItem(15, createItem(Material.CYAN_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "4" -> inventory.setItem(15, createItem(Material.RED_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "5" -> inventory.setItem(15, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "6" -> inventory.setItem(15, createItem(Material.ORANGE_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "7" -> inventory.setItem(15, createItem(Material.LIGHT_GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "8" -> inventory.setItem(15, createItem(Material.GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "9" -> inventory.setItem(15, createItem(Material.BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "a" -> inventory.setItem(15, createItem(Material.LIME_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "b" -> inventory.setItem(15, createItem(Material.LIGHT_BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "c" -> inventory.setItem(15, createItem(Material.RED_TERRACOTTA, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "d" -> inventory.setItem(15, createItem(Material.PINK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "e" -> inventory.setItem(15, createItem(Material.YELLOW_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "f" -> inventory.setItem(15, createItem(Material.WHITE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player, 4) + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            default -> inventory.setItem(15, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §5" + player.getName(), Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        }

        //P
        switch (PrefixManager.getPrefix(player, 1)){
            case "0" -> inventory.setItem(11, createItem(Material.BLACK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "1" -> inventory.setItem(11, createItem(Material.BLUE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "2" -> inventory.setItem(11, createItem(Material.GREEN_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "3" -> inventory.setItem(11, createItem(Material.CYAN_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "4" -> inventory.setItem(11, createItem(Material.RED_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "5" -> inventory.setItem(11, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "6" -> inventory.setItem(11, createItem(Material.ORANGE_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "7" -> inventory.setItem(11, createItem(Material.LIGHT_GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "8" -> inventory.setItem(11, createItem(Material.GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "9" -> inventory.setItem(11, createItem(Material.BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "a" -> inventory.setItem(11, createItem(Material.LIME_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "b" -> inventory.setItem(11, createItem(Material.LIGHT_BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "c" -> inventory.setItem(11, createItem(Material.RED_TERRACOTTA, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "d" -> inventory.setItem(11, createItem(Material.PINK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "e" -> inventory.setItem(11, createItem(Material.YELLOW_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "f" -> inventory.setItem(11, createItem(Material.WHITE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,1) + "§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            default -> inventory.setItem(11, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §5§lP", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        }
        //R
        switch (PrefixManager.getPrefix(player, 2)){
            case "0" -> inventory.setItem(12, createItem(Material.BLACK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "1" -> inventory.setItem(12, createItem(Material.BLUE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "2" -> inventory.setItem(12, createItem(Material.GREEN_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "3" -> inventory.setItem(12, createItem(Material.CYAN_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "4" -> inventory.setItem(12, createItem(Material.RED_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "5" -> inventory.setItem(12, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "6" -> inventory.setItem(12, createItem(Material.ORANGE_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "7" -> inventory.setItem(12, createItem(Material.LIGHT_GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "8" -> inventory.setItem(12, createItem(Material.GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "9" -> inventory.setItem(12, createItem(Material.BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "a" -> inventory.setItem(12, createItem(Material.LIME_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "b" -> inventory.setItem(12, createItem(Material.LIGHT_BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "c" -> inventory.setItem(12, createItem(Material.RED_TERRACOTTA, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "d" -> inventory.setItem(12, createItem(Material.PINK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "e" -> inventory.setItem(12, createItem(Material.YELLOW_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "f" -> inventory.setItem(12, createItem(Material.WHITE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,2) + "§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            default -> inventory.setItem(12, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §5§lr", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        }
        //O
        switch (PrefixManager.getPrefix(player, 3)){
            case "0" -> inventory.setItem(13, createItem(Material.BLACK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "1" -> inventory.setItem(13, createItem(Material.BLUE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "2" -> inventory.setItem(13, createItem(Material.GREEN_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "3" -> inventory.setItem(13, createItem(Material.CYAN_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "4" -> inventory.setItem(13, createItem(Material.RED_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "5" -> inventory.setItem(13, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "6" -> inventory.setItem(13, createItem(Material.ORANGE_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "7" -> inventory.setItem(13, createItem(Material.LIGHT_GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "8" -> inventory.setItem(13, createItem(Material.GRAY_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "9" -> inventory.setItem(13, createItem(Material.BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "a" -> inventory.setItem(13, createItem(Material.LIME_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "b" -> inventory.setItem(13, createItem(Material.LIGHT_BLUE_WOOL, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "c" -> inventory.setItem(13, createItem(Material.RED_TERRACOTTA, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "d" -> inventory.setItem(13, createItem(Material.PINK_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "e" -> inventory.setItem(13, createItem(Material.YELLOW_CONCRETE_POWDER, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            case "f" -> inventory.setItem(13, createItem(Material.WHITE_CONCRETE, "§fUlkonäkö: §" + PrefixManager.getPrefix(player,3) + "§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
            default -> inventory.setItem(13, createItem(Material.PURPLE_CONCRETE, "§fUlkonäkö: §5§lo", Arrays.asList("§7Klikkaa vaihtaaksesi väriä")));
        }
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
