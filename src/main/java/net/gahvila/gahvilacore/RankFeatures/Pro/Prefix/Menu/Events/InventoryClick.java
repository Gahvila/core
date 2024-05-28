package net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.Events;

import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.ColorSelectors.ColorSelectorName;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.ColorSelectors.ColorSelectorO;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.ColorSelectors.ColorSelectorP;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.ColorSelectors.ColorSelectorR;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.ProPrefixMainMenu;
import net.gahvila.gahvilacore.RankFeatures.Pro.Prefix.Menu.PrefixManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.Long.MAX_VALUE;

public class InventoryClick implements Listener {
    private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
    @EventHandler
    public void onClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();

        switch (e.getView().getTitle()){
            case "§8» §8Prefixmenu":
                if (p.hasPermission("gahvilacore.rank.pro")){
                    switch (e.getSlot()){
                        case 15:
                            e.setCancelled(true);
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                            ColorSelectorName.openColorMenuName(p);
                            break;
                        case 11:
                            e.setCancelled(true);
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                            ColorSelectorP.openColorMenuP(p);
                            break;
                        case 12:
                            e.setCancelled(true);
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                            ColorSelectorR.openColorMenuR(p);
                            break;
                        case 13:
                            e.setCancelled(true);
                            p.closeInventory();
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                            ColorSelectorO.openColorMenuO(p);
                            break;
                        default:
                            e.setCancelled(true);
                            break;
                    }
                }else{
                    e.setCancelled(true);
                    p.closeInventory();
                }
                break;
            case "§8» §8Värin valinta: §lNimi":
                if (p.hasPermission("gahvilacore.rank.pro")){
                    int position = 4;
                    switch (e.getSlot()){
                        case 0,1,2,3,4,5,6,7,9,10,11,12,13,14,15,16:
                            if (!cooldown.containsKey(p.getUniqueId())) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                Bukkit.getScheduler().runTaskLater(GahvilaCore.instance, () -> cooldown.remove(p.getUniqueId()), 10);
                                String string = String.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().charAt(1));
                                e.setCancelled(true);
                                PrefixManager.setPrefix(p, position, string);
                                ColorSelectorName.updatePrefixMenu(p, e.getInventory());
                                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                                p.updateInventory();
                            }else{
                                e.setCancelled(true);
                            }
                            break;
                        case 17:
                            e.setCancelled(true);
                            p.closeInventory();
                            ProPrefixMainMenu.openPrefixMainMenu(p);
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, MAX_VALUE, 1F);
                            break;
                        default:
                            e.setCancelled(true);
                            break;
                    }
                }
                break;
            case "§8» §8Värin valinta: §lP":
                if (p.hasPermission("gahvilacore.rank.pro")){
                    int position = 1;
                    switch (e.getSlot()){
                        case 0,1,2,3,4,5,6,7,9,10,11,12,13,14,15,16:
                            if (!cooldown.containsKey(p.getUniqueId())) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                Bukkit.getScheduler().runTaskLater(GahvilaCore.instance, () -> cooldown.remove(p.getUniqueId()), 10);
                                String string = String.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().charAt(1));
                                e.setCancelled(true);
                                PrefixManager.setPrefix(p, position, string);
                                ColorSelectorName.updatePrefixMenu(p, e.getInventory());
                                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                                p.updateInventory();
                            }else{
                                e.setCancelled(true);
                            }
                            break;
                        case 17:
                            e.setCancelled(true);
                            p.closeInventory();
                            ProPrefixMainMenu.openPrefixMainMenu(p);
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, MAX_VALUE, 1F);
                            break;
                        default:
                            e.setCancelled(true);
                            break;
                    }
                }
                break;
            case "§8» §8Värin valinta: §lR":
                if (p.hasPermission("gahvilacore.rank.pro")){
                    int position = 2;
                    switch (e.getSlot()){
                        case 0,1,2,3,4,5,6,7,9,10,11,12,13,14,15,16:
                            if (!cooldown.containsKey(p.getUniqueId())) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                Bukkit.getScheduler().runTaskLater(GahvilaCore.instance, () -> cooldown.remove(p.getUniqueId()), 10);
                                String string = String.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().charAt(1));
                                e.setCancelled(true);
                                PrefixManager.setPrefix(p, position, string);
                                ColorSelectorName.updatePrefixMenu(p, e.getInventory());
                                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                                p.updateInventory();
                            }else{
                                e.setCancelled(true);
                            }
                            break;
                        case 17:
                            e.setCancelled(true);
                            p.closeInventory();
                            ProPrefixMainMenu.openPrefixMainMenu(p);
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, MAX_VALUE, 1F);
                            break;
                        default:
                            e.setCancelled(true);
                            break;
                    }
                }
                break;
            case "§8» §8Värin valinta: §lO":
                if (p.hasPermission("gahvilacore.rank.pro")){
                    int position = 3;
                    switch (e.getSlot()){
                        case 0,1,2,3,4,5,6,7,9,10,11,12,13,14,15,16:
                            if (!cooldown.containsKey(p.getUniqueId())) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                Bukkit.getScheduler().runTaskLater(GahvilaCore.instance, () -> cooldown.remove(p.getUniqueId()), 10);
                                String string = String.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().charAt(1));
                                e.setCancelled(true);
                                PrefixManager.setPrefix(p, position, string);
                                ColorSelectorName.updatePrefixMenu(p, e.getInventory());
                                p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, MAX_VALUE, 1F);
                                p.updateInventory();
                            }else{
                                e.setCancelled(true);
                            }
                            break;
                        case 17:
                            e.setCancelled(true);
                            p.closeInventory();
                            ProPrefixMainMenu.openPrefixMainMenu(p);
                            p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, MAX_VALUE, 1F);
                            break;
                        default:
                            e.setCancelled(true);
                            break;
                    }
                }
                break;



        }
    }
}
