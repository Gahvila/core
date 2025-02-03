package net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.player;


import net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.BukkitPlatform;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;

public class BukkitSongPlayer {

    private BukkitSongPlayer() {}

    public static class Builder extends SongPlayer.Builder {

        public Builder() {
            super(BukkitPlatform.INSTANCE);
        }
    }
}
