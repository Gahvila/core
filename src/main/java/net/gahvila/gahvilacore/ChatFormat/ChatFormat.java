package net.gahvila.gahvilacore.ChatFormat;

import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.ProGui.PrefixManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatFormat implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (PrefixManager.isPrefixEnabled(p)) {

            String prefix = ChatColor.AQUA + "" + ChatColor.BOLD + "Pelaaja";


            if (p.hasPermission("gahvilacore.prefix.owner")) {
                prefix = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Omistaja";

            } else if (p.hasPermission("gahvilacore.prefix.admin")) {
                prefix = ChatColor.RED + "" + ChatColor.BOLD + "Ylläpitäjä";

            } else if (p.hasPermission("gahvilacore.prefix.builder")) {
                prefix = ChatColor.GREEN + "" + ChatColor.BOLD + "Rakentaja";

            } else if (p.hasPermission("gahvilacore.prefix.moderator")) {
                prefix = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Valvoja";

            } else if (p.hasPermission("gahvilacore.prefix.trainee")) {
                prefix = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Harjoittelija";

            } else if (p.hasPermission("gahvilacore.prefix.pro")) {
                prefix = "§" + PrefixManager.getPrefix(p, 1) + "" + ChatColor.BOLD + "P" + "§" + PrefixManager.getPrefix(p, 2) + "" + ChatColor.BOLD + "r" + "§" + PrefixManager.getPrefix(p, 3) + "" + ChatColor.BOLD + "o";

            } else if (p.hasPermission("gahvilacore.prefix.mvp")) {
                prefix = ChatColor.GOLD + "" + ChatColor.BOLD + "MVP";

            } else if (p.hasPermission("gahvilacore.prefix.vip")) {
                prefix = ChatColor.YELLOW + "" + ChatColor.BOLD + "VIP";
            }

            if (p.hasPermission("gahvilacore.chatcolor")) {
                e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
            }

            String nameColor = "§" + PrefixManager.getPrefix(p, 4);
            e.setFormat(prefix + " " + ChatColor.GRAY + nameColor + "%s" + ChatColor.YELLOW + "" + ChatColor.BOLD + " > " + ChatColor.RESET + "%s");
        }


        //CAPS BLOCK
        float upCaseLetters = 0f;
        float letters = 0;

        for (char c : e.getMessage().toCharArray()) {
            if (Character.isUpperCase(c)) {
                upCaseLetters++;
            }
            if (c != ' ') {
                letters++;
            }
        }

        if (upCaseLetters / letters >= 0.6 && letters > 7) {
            e.setMessage(e.getMessage().toLowerCase());
        }
    }



    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPlayedBefore()) return;

        if (GahvilaCore.instance.getConfig().getBoolean("chatformatenabled")) {
            PrefixManager.enablePrefix(e.getPlayer(), true);
        }
    }
}
