package net.gahvila.gahvilacore.Profiles.Prefix.Frontend.Menu;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toUndecoratedMM;

public class PrefixMainMenu {

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, ComponentHolder.of(toMM("<dark_purple><b>Prefix")));
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);

        ItemStack backgroundItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.setHideTooltip(true);
        backgroundItemMeta.displayName(toMM(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        background.addItem(new GuiItem(backgroundItem));

        background.setRepeat(true);

        gui.addPane(background);

        StaticPane navigationPane = new StaticPane(3, 1, 3, 1);

        NamespacedKey key = new NamespacedKey(instance, "aula");


        ItemStack prefix = new ItemStack(Material.PAPER);
        ItemMeta prefixMeta = prefix.getItemMeta();
        prefixMeta.displayName(toUndecoratedMM("<green><b>Prefixin valinta</b></green>"));
        prefixMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "setprefix");
        prefix.setItemMeta(prefixMeta);
        navigationPane.addItem(new GuiItem(prefix), 0, 0);

        ItemStack color = new ItemStack(Material.PAPER);
        ItemMeta colorMeta = color.getItemMeta();
        colorMeta.displayName(toUndecoratedMM("<green><b>Värin valinta</b></green>"));
        colorMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "colorpicker");
        color.setItemMeta(colorMeta);
        navigationPane.addItem(new GuiItem(color), 2, 0);

        navigationPane.setOnClick(event -> {
            if (event.getCurrentItem().getItemMeta().isHideTooltip()) return;
            String command = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1F);
            player.performCommand("prefix " + command);
        });

        gui.addPane(navigationPane);

        gui.update();
    }
}
