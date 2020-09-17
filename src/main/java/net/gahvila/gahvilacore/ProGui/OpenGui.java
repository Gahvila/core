package net.gahvila.gahvilacore.ProGui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class OpenGui {

    public static void openMainGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Pro menu");

        createMainInventory(player, inv);
        player.openInventory(inv);
    }

    public static void createMainInventory(Player player, Inventory inventory) {

        inventory.setItem(3, createItem(Material.FEATHER, ChatColor.DARK_PURPLE + "Fly", Arrays.asList(ChatColor.GRAY + "Vaihda lentotilaa.")));
        inventory.setItem(5, createItem(Material.SUGAR, ChatColor.DARK_PURPLE + "Vanish", Arrays.asList(ChatColor.GRAY + "Piiloudu pelaajilta.")));
        inventory.setItem(13, createItem(Material.WOOL, ChatColor.DARK_PURPLE + "Prefix menu", Arrays.asList(ChatColor.GRAY + "Vaihda prefixin väriä.")));
        if (PrefixManager.isPrefixEnabled(player)) {
            inventory.setItem(21, createShortItem(Material.INK_SACK, ChatColor.DARK_PURPLE + "Chatin muoto", Arrays.asList(ChatColor.GRAY + "Vaihda chatin muotoa."), (short) 10));
        } else {
            inventory.setItem(21, createShortItem(Material.INK_SACK, ChatColor.DARK_PURPLE + "Chatin muoto", Arrays.asList(ChatColor.GRAY + "Vaihda chatin muotoa."), (short) 8));
        }



        ItemStack lightblue = createShortItem(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), (short) 3);
        ItemStack blue = createShortItem(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), (short) 11);
        ItemStack gray = createShortItem(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), (short) 7);

        for (int i = 0; i < 27; i++) {
            if (i == 0 || i == 1 || i == 7 || i == 8 || i == 9 || i == 17 || i == 18 || i == 19 || i == 25 || i == 26) {
                inventory.setItem(i, lightblue);
            }
            if (i == 2 || i == 6 || i == 10 || i == 16 || i == 20 || i == 24) {
                inventory.setItem(i, blue);
            }
            if (i == 4 || i == 11 || i == 12 || i == 14 || i == 15 || i == 22 || i == 23) {
                inventory.setItem(i, gray);
            }
        }
    }



    public static void openPrefixGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Prefix menu");

        createPrefixInventory(player, inv);
        player.openInventory(inv);
    }

    public static void updatePrefixInventoryName(Player player, Inventory inv) {

        int slot = 13;
        int position = 4;

        if (PrefixManager.getPrefix(player, position) == null) {
            inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 8));
            PrefixManager.setPrefix(player, 4, "7");
        }



        switch (PrefixManager.getPrefix(player, position)) {
            case "4":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 14));
                PrefixManager.setPrefix(player, position, "c");
                break;
            case "c":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GOLD + "Oranssi", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 1));
                PrefixManager.setPrefix(player, position, "6");
                break;
            case "6":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.YELLOW + "Keltainen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 4));
                PrefixManager.setPrefix(player, position, "e");
                break;
            case "e":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 13));
                PrefixManager.setPrefix(player, position, "2");
                break;
            case "2":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 5));
                PrefixManager.setPrefix(player, position, "a");
                break;
            case "a":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 3));
                PrefixManager.setPrefix(player, position, "b");
                break;
            case "b":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 9));
                PrefixManager.setPrefix(player, position, "3");
                break;
            case "3":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 11));
                PrefixManager.setPrefix(player, position, "1");
                break;
            case "1":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 11));
                PrefixManager.setPrefix(player, position, "9");
                break;
            case "9":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.LIGHT_PURPLE + "Pinkki", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 2));
                PrefixManager.setPrefix(player, position, "d");
                break;
            case "d":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 10));
                PrefixManager.setPrefix(player, position, "5");
                break;
            case "5":
                inv.setItem(slot, createItem(Material.WOOL, ChatColor.WHITE + "Valkoinen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla.")));
                PrefixManager.setPrefix(player, position, "f");
                break;
            case "f":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 8));
                PrefixManager.setPrefix(player, position, "7");
                break;
            case "7":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 7));
                PrefixManager.setPrefix(player, position, "8");
                break;
            case "8":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLACK + "Musta", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 15));
                PrefixManager.setPrefix(player, position, "0");
                break;
            case "0":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 14));
                PrefixManager.setPrefix(player, position, "4");
                break;
            default:
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Ei tunnistettu väriä", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 0));
                break;

        }
        String name = "§" + PrefixManager.getPrefix(player, 4) + player.getName();
        inv.setItem(4, createItem(Material.PAPER, name, Arrays.asList(ChatColor.GRAY + "Nimesi näyttää tältä.")));


    }

    public static void updatePrefixInventory(Player player, Inventory inv, Integer slot, Integer position) {

        //Inventory inv = player.getInventory();

        if (PrefixManager.getPrefix(player, position) == null) {
            inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
            PrefixManager.setPrefix(player, position, "5");
            return;
        }

        switch (PrefixManager.getPrefix(player, position)) {
            case "4":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                PrefixManager.setPrefix(player, position, "c");
                break;
            case "c":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GOLD + "Oranssi", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 1));
                PrefixManager.setPrefix(player, position, "6");
                break;
            case "6":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.YELLOW + "Keltainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 4));
                PrefixManager.setPrefix(player, position, "e");
                break;
            case "e":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 13));
                PrefixManager.setPrefix(player, position, "2");
                break;
            case "2":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 5));
                PrefixManager.setPrefix(player, position, "a");
                break;
            case "a":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 3));
                PrefixManager.setPrefix(player, position, "b");
                break;
            case "b":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 9));
                PrefixManager.setPrefix(player, position, "3");
                break;
            case "3":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                PrefixManager.setPrefix(player, position, "1");
                break;
            case "1":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                PrefixManager.setPrefix(player, position, "9");
                break;
            case "9":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.LIGHT_PURPLE + "Pinkki", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 2));
                PrefixManager.setPrefix(player, position, "d");
                break;
            case "d":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                PrefixManager.setPrefix(player, position, "5");
                break;
            case "5":
                inv.setItem(slot, createItem(Material.WOOL, ChatColor.WHITE + "Valkoinen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla.")));
                PrefixManager.setPrefix(player, position, "f");
                break;
            case "f":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 8));
                PrefixManager.setPrefix(player, position, "7");
                break;
            case "7":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 7));
                PrefixManager.setPrefix(player, position, "8");
                break;
            case "8":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLACK + "Musta", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 15));
                PrefixManager.setPrefix(player, position, "0");
                break;
            case "0":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                PrefixManager.setPrefix(player, position, "4");
                break;
            default:
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Ei tunnistettu väriä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 0));
                break;

        }
        String prefix = "§" + PrefixManager.getPrefix(player, 1) + "" + ChatColor.BOLD + "P" + "§" + PrefixManager.getPrefix(player, 2) + "" + ChatColor.BOLD + "r" + "§" + PrefixManager.getPrefix(player, 3) + "" + ChatColor.BOLD + "o";
        inv.setItem(31, createItem(Material.PAPER, prefix, Arrays.asList(ChatColor.GRAY + "Etuliitteesi näyttää tältä.")));
    }




    public static void createPrefixInventory(Player player, Inventory inv) {


        int slot = 13;
        int position = 4;

        if (PrefixManager.getPrefix(player, position) == null) {
            inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 8));
            PrefixManager.setPrefix(player, 4, "7");
        }

        switch (PrefixManager.getPrefix(player, position)) {
            case "4":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 14));
                PrefixManager.setPrefix(player, position, "4");
                break;
            case "c":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 14));
                PrefixManager.setPrefix(player, position, "c");
                break;
            case "6":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GOLD + "Oranssi", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 1));
                PrefixManager.setPrefix(player, position, "6");
                break;
            case "e":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.YELLOW + "Keltainen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 4));
                PrefixManager.setPrefix(player, position, "e");
                break;
            case "2":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 13));
                PrefixManager.setPrefix(player, position, "2");
                break;
            case "a":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 5));
                PrefixManager.setPrefix(player, position, "a");
                break;
            case "b":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 3));
                PrefixManager.setPrefix(player, position, "b");
                break;
            case "3":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 9));
                PrefixManager.setPrefix(player, position, "3");
                break;
            case "1":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 11));
                PrefixManager.setPrefix(player, position, "1");
                break;
            case "9":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 11));
                PrefixManager.setPrefix(player, position, "9");
                break;
            case "d":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.LIGHT_PURPLE + "Pinkki", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 2));
                PrefixManager.setPrefix(player, position, "d");
                break;
            case "5":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 10));
                PrefixManager.setPrefix(player, position, "5");
                break;
            case "f":
                inv.setItem(slot, createItem(Material.WOOL, ChatColor.WHITE + "Valkoinen", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla.")));
                PrefixManager.setPrefix(player, position, "f");
                break;
            case "7":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 8));
                PrefixManager.setPrefix(player, position, "7");
                break;
            case "8":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 7));
                PrefixManager.setPrefix(player, position, "8");
                break;
            case "0":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLACK + "Musta", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 15));
                PrefixManager.setPrefix(player, position, "0");
                break;
            default:
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Ei tunnistettu väriä", Arrays.asList(ChatColor.GRAY + "Vaihda nimen väriä klikkaamalla."), (short) 0));
                break;

        }
        String name = "§" + PrefixManager.getPrefix(player, 4) + player.getName();
        inv.setItem(4, createItem(Material.PAPER, name, Arrays.asList(ChatColor.GRAY + "Nimesi näyttää tältä.")));



        slot = 39;
        position = 1;

        String prefix = "§" + PrefixManager.getPrefix(player, 1) + "" + ChatColor.BOLD + "P" + "§" + PrefixManager.getPrefix(player, 2) + "" + ChatColor.BOLD + "r" + "§" + PrefixManager.getPrefix(player, 3) + "" + ChatColor.BOLD + "o";
        inv.setItem(31, createItem(Material.PAPER, prefix, Arrays.asList(ChatColor.GRAY + "Etuliitteesi näyttää tältä.")));

        //POSITION 1
        if (PrefixManager.getPrefix(player, position) == null) {
            inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
            PrefixManager.setPrefix(player, position, "5");
            return;
        }
        switch (PrefixManager.getPrefix(player, position)) {
            case "4":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                break;

            case "c":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                break;
            case "6":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GOLD + "Oranssi", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 1));
                break;
            case "e":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.YELLOW + "Keltainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 4));
                break;
            case "2":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 13));
                break;
            case "a":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 5));
                break;
            case "b":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 3));
                break;
            case "3":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 9));
                break;
            case "1":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                break;
            case "9":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                break;
            case "d":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.LIGHT_PURPLE + "Pinkki", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 2));
                break;
            case "5":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                break;
            case "f":
                inv.setItem(slot, createItem(Material.WOOL, ChatColor.WHITE + "Valkoinen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla.")));
                break;
            case "7":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 8));
                break;
            case "8":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 7));
                break;
            case "0":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLACK + "Musta", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 15));
                break;

            default:
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                PrefixManager.setPrefix(player, position, "5");
                break;
        }

        slot = 40;
        position = 2;
        //POSITION 2
        if (PrefixManager.getPrefix(player, position) == null) {
            inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
            PrefixManager.setPrefix(player, position, "5");
            return;
        }
        switch (PrefixManager.getPrefix(player, position)) {
            case "4":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                break;

            case "c":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                break;
            case "6":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GOLD + "Oranssi", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 1));
                break;
            case "e":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.YELLOW + "Keltainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 4));
                break;
            case "2":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 13));
                break;
            case "a":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 5));
                break;
            case "b":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 3));
                break;
            case "3":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 9));
                break;
            case "1":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                break;
            case "9":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                break;
            case "d":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.LIGHT_PURPLE + "Pinkki", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 2));
                break;
            case "5":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                break;
            case "f":
                inv.setItem(slot, createItem(Material.WOOL, ChatColor.WHITE + "Valkoinen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla.")));
                break;
            case "7":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 8));
                break;
            case "8":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 7));
                break;
            case "0":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLACK + "Musta", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 15));
                break;

            default:
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                PrefixManager.setPrefix(player, position, "5");
                break;
        }


        slot = 41;
        position = 3;
        //POSITION 3
        if (PrefixManager.getPrefix(player, position) == null) {
            inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
            PrefixManager.setPrefix(player, position, "5");
            return;
        }
        switch (PrefixManager.getPrefix(player, position)) {
            case "4":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                break;

            case "c":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.RED + "Punainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 14));
                break;
            case "6":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GOLD + "Oranssi", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 1));
                break;
            case "e":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.YELLOW + "Keltainen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 4));
                break;
            case "2":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 13));
                break;
            case "a":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GREEN + "Vihreä", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 5));
                break;
            case "b":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 3));
                break;
            case "3":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_AQUA + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 9));
                break;
            case "1":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                break;
            case "9":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLUE + "Sininen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 11));
                break;
            case "d":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.LIGHT_PURPLE + "Pinkki", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 2));
                break;
            case "5":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                break;
            case "f":
                inv.setItem(slot, createItem(Material.WOOL, ChatColor.WHITE + "Valkoinen", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla.")));
                break;
            case "7":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 8));
                break;
            case "8":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_GRAY + "Harmaa", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 7));
                break;
            case "0":
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.BLACK + "Musta", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 15));
                break;

            default:
                inv.setItem(slot, createShortItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violetti", Arrays.asList(ChatColor.GRAY + "Vaihda kirjaimen väriä klikkaamalla."), (short) 10));
                PrefixManager.setPrefix(player, position, "5");
                break;
        }


        inv.setItem(49, createItem(Material.BARRIER, ChatColor.RED + "Nollaa", Arrays.asList(ChatColor.GRAY + "Nollaa etuliitteesi ja nimesi värit.")));
        ItemStack lightblue = createShortItem(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), (short) 3);
        ItemStack blue = createShortItem(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), (short) 11);
        ItemStack gray = createShortItem(Material.STAINED_GLASS_PANE, " ", Arrays.asList(""), (short) 7);


        for (int i = 0; i < 54; i++) {
            if (i == 0 || i == 1 || i == 7  || i == 8 || i == 9 || i == 17 || i == 18 || i == 19 || i == 25 || i == 26 || i == 27 || i == 28 || i == 34
                    || i == 35 || i == 36 || i == 44 || i == 45 || i == 46 || i == 52 || i == 53) {
                inv.setItem(i, lightblue);
            }
            if (i == 2 || i == 6 || i == 10 || i == 16 || i == 20 || i == 24 || i == 29 || i == 33 || i == 37 || i == 43 || i == 47 || i == 51) {
                inv.setItem(i, blue);
            }
            if (i == 3 || i == 5 || i == 11 || i == 12 || i == 14 || i == 15 || i == 21 || i == 22 || i == 23 || i == 30 || i == 32 || i == 38 || i == 42
                    || i == 48 || i == 50) {
                inv.setItem(i, gray);
            }
        }


    }






    private static ItemStack createItem(Material material, String name, List<String> lore) {

        ItemStack i;

        i = new ItemStack(material, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);

        i.setItemMeta(meta);

        return i;
    }

    public static ItemStack createShortItem(Material material, String name, List<String> lore, Short value) {

        ItemStack i;

        i = new ItemStack(material, 1, (short) value);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);

        i.setItemMeta(meta);

        return i;
    }
}
