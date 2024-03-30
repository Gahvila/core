package net.gahvila.gahvilacore.Paper.Marriage;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;

public class SexUpdate implements Listener {
    private Map<Player, Long> lastSneakTime = new HashMap<>();

    private final MarriageManager marriageManager;

    public static HashMap<Player, Player> marry = new HashMap<>();

    public SexUpdate(MarriageManager marriageManager) {
        this.marriageManager = marriageManager;

    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player sneaker = event.getPlayer();
        if (!marriageManager.isPlayerMarried(sneaker)) return;
        if (event.isSneaking()) {
            lastSneakTime.put(sneaker, System.currentTimeMillis());
            checkForSneakSpam();
        }
    }

    private void checkForSneakSpam() {
        for (Player player1 : lastSneakTime.keySet()) {
            for (Player player2 : lastSneakTime.keySet()) {
                if (!player1.equals(player2)) {
                    if (!marriageManager.isPlayerMarriedToPlayer(player1, player2)) return;
                    if (player1.getLocation().distance(player2.getLocation()) <= 1) {
                        long currentTime = System.currentTimeMillis();
                        long lastSneakTime1 = lastSneakTime.get(player1);
                        long lastSneakTime2 = lastSneakTime.get(player2);
                        if (currentTime - lastSneakTime1 <= 1000 && currentTime - lastSneakTime2 <= 1000) {
                            player1.getWorld().spawnParticle(Particle.HEART, player2.getX(), player2.getY()+1.5, player2.getZ(), 1);
                        }
                    }
                }
            }
        }
    }
}
