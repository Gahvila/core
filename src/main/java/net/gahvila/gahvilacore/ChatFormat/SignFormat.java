package net.gahvila.gahvilacore.ChatFormat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignFormat implements Listener {


    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String[] lines = e.getLines();
        for(int i = 0; i < 4; i++) {
            String line = lines[i];
            line = ChatColor.translateAlternateColorCodes('&', line);
            e.setLine(i, line);
        }
    }
}
