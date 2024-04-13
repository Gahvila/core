package net.gahvila.gahvilacore.Paper.Marriage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class MarriageEvents implements Listener {
    private final MarriageManager marriageManager;

    public MarriageEvents(MarriageManager marriageManager) {
        this.marriageManager = marriageManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (marriageManager.isPlayerMarried(player)){
            UUID marriedTo = UUID.fromString(marriageManager.getMarriageInfo(player, "uuid"));
            marriageManager.updateCurrentName(marriedTo, player.getUniqueId());
        }
    }
}
