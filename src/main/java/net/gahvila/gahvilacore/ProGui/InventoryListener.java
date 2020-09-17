package net.gahvila.gahvilacore.ProGui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent  e) {

        Player p = (Player) e.getWhoClicked();

        if (e.getInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Pro menu")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE || !e.getCurrentItem().hasItemMeta()) return;

            switch (e.getSlot()) {
                case 3:
                    p.performCommand("fly");
                    break;
                case 5:
                    p.performCommand("vanish");
                    break;
                case 13:
                    p.closeInventory();
                    OpenGui.openPrefixGUI(p);
                    break;
                case 21:
                    p.performCommand("chatformattoggle");
                    if (PrefixManager.isPrefixEnabled(p)) {
                        e.getInventory().setItem(21, OpenGui.createShortItem(Material.INK_SACK, ChatColor.DARK_PURPLE + "Chatin muoto", Arrays.asList(ChatColor.GRAY + "Vaihda chatin muotoa."), (short) 10));
                    } else {
                        e.getInventory().setItem(21, OpenGui.createShortItem(Material.INK_SACK, ChatColor.DARK_PURPLE + "Chatin muoto", Arrays.asList(ChatColor.GRAY + "Vaihda chatin muotoa."), (short) 8));
                    }
                    p.updateInventory();
                    break;

            }

        } else if (e.getInventory().getTitle().equalsIgnoreCase(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Prefix menu")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) return;
            if (e.getClick() != ClickType.LEFT) return;

            if (e.getSlot() == 39 || e.getSlot() == 40 || e.getSlot() == 41) {

                int slotNumber = e.getSlot();

                //p.sendMessage("Debug position: " + (slotNumber - 38));
                //p.sendMessage("Debug slot: " + slotNumber);

                OpenGui.updatePrefixInventory(p, e.getInventory(), slotNumber, (slotNumber - 38));

                p.updateInventory();
            } else if (e.getSlot() == 13) {

                OpenGui.updatePrefixInventoryName(p, e.getInventory());
                p.updateInventory();
            }

            else if (e.getSlot() == 49) {

                PrefixManager.setPrefix(p, 1, "5");
                PrefixManager.setPrefix(p, 2, "5");
                PrefixManager.setPrefix(p, 3, "5");
                PrefixManager.setPrefix(p, 4, "7");


                OpenGui.createPrefixInventory(p, e.getInventory());

                p.updateInventory();
            }
        }
    }
}
