package net.gahvila.gahvilacore.Profiles.Marriage;

import net.gahvila.inventoryframework.gui.GuiItem;
import net.gahvila.inventoryframework.gui.type.ChestGui;
import net.gahvila.inventoryframework.pane.OutlinePane;
import net.gahvila.inventoryframework.pane.Pane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class MarriageMenu {

    private final MarriageManager marriageManager;

    public static HashMap<Player, Player> marry = new HashMap<>();

    public MarriageMenu(MarriageManager marriageManager) {
        this.marriageManager = marriageManager;

    }

    public void showGUI(Player player) {
        ChestGui gui = new ChestGui(3, "§5§lKumppanisi");
        gui.show(player);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);

        ItemStack backgroundItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.displayName(toMiniMessage(""));
        backgroundItem.setItemMeta(backgroundItemMeta);

        background.addItem(new GuiItem(backgroundItem));

        background.setRepeat(true);

        gui.addPane(background);

        OutlinePane navigationPane = new OutlinePane(2, 1, 5, 1);

        ItemStack teleport = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportMeta = teleport.getItemMeta();
        teleportMeta.displayName(toMiniMessage("<white><b>Teleport</b>"));
        teleportMeta.lore(List.of(toMiniMessage("<white>Teleporttaa kumppaniisi")));
        teleport.setItemMeta(teleportMeta);

        navigationPane.addItem(new GuiItem(teleport, event -> {
            player.performCommand("marry tp");
        }));


        gui.addPane(navigationPane);

        gui.update();
    }

    public @NotNull Component toMiniMessage(@NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
