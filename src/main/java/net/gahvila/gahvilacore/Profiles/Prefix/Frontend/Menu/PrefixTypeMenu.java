package net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.Prefix;
import net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;
import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class PrefixTypeMenu {

    private final PrefixManager prefixManager;

    public static ArrayList<Player> cooldown = new ArrayList<>();


    public PrefixTypeMenu(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(4, ComponentHolder.of(toUndecoratedMM("<dark_purple><b>Prefixvalitsin")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        Pattern pattern = new Pattern(
                "111111111",
                "1AAAAAAA1",
                "1AAAAAAA1",
                "111111111"
        );
        PatternPane border = new PatternPane(0, 0, 9, 4, Pane.Priority.LOWEST, pattern);
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.displayName(toUndecoratedMM(""));
        backgroundMeta.setHideTooltip(true);
        background.setItemMeta(backgroundMeta);
        border.bindItem('1', new GuiItem(background));
        gui.addPane(border);

        PaginatedPane pages = new PaginatedPane(1, 1, 7, 2);
        List<ItemStack> items = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(instance, "gahvilacore");
        for (Prefix prefix : Prefix.values()) {
            if (player.hasPermission(prefix.getPermissionNode())) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, prefix.toString());
                meta.displayName(toUndecoratedMM("<b>" + prefix.getDisplayName()));
                item.setItemMeta(meta);
                items.add(item);
            }
        }
        pages.populateWithItemStacks(items);
        gui.addPane(pages);

        pages.setOnClick(event -> {
            if (event.getCurrentItem() == null) return;

            if (cooldown.contains(player)) return;
            cooldown.add(player);
            Bukkit.getScheduler().runTaskLater(instance, () -> cooldown.remove(player), 10);

            String data = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (data != null) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1F);
                prefixManager.setPrefix(player, Prefix.valueOf(data));
                player.sendRichMessage("<white>prefix: " + data + "</white>");
                player.closeInventory();
            }else {
                player.closeInventory();
                Bukkit.getLogger().severe("player selected nonexistent prefix");
            }
        });

        StaticPane navigationPane = new StaticPane(0, 3, 9, 1);

        ItemStack mainmenu = new ItemStack(Material.BARRIER);
        ItemMeta mainmenuMeta = mainmenu.getItemMeta();
        mainmenuMeta.displayName(toUndecoratedMM("<b>Päävalikko"));
        mainmenu.setItemMeta(mainmenuMeta);
        navigationPane.addItem(new GuiItem(mainmenu, event -> {
            player.closeInventory();
            player.performCommand("prefix");
        }), 0, 0);

        ItemStack previous = new ItemStack(Material.MANGROVE_BUTTON);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(toUndecoratedMM("<b>Takaisin"));
        previous.setItemMeta(previousMeta);
        navigationPane.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.7F);

                gui.update();
            }
        }), 6, 0);
        ItemStack next = new ItemStack(Material.WARPED_BUTTON);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(toUndecoratedMM("<b>Seuraava"));
        next.setItemMeta(nextMeta);
        navigationPane.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 0.8F);

                gui.update();
            }
        }), 7, 0);
        gui.addPane(navigationPane);

        gui.update();
    }
}
