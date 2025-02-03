package net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.utils;

import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitAudioListener extends AudioListener {

    public BukkitAudioListener(Player player, float volume) {
        super(player.getEntityId(), player.getUniqueId(), volume);
    }

    public BukkitAudioListener(@NotNull Player player) {
        super(player.getEntityId(), player.getUniqueId());
    }
}
