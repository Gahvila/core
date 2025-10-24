package net.gahvila.gahvilacore.Music;

import net.gahvila.gahvilacore.nbsminecraft.events.SongEndEvent;
import net.gahvila.gahvilacore.nbsminecraft.events.SongNextEvent;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.SoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.StaticSoundEmitter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class MusicEvents implements Listener {

    private final MusicManager musicManager;

    public MusicEvents(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        musicManager.setSpeakerEnabled(player, false);
        musicManager.setAutoEnabled(player, musicManager.getSavedAutoState(player));
        musicManager.setVolume(player, musicManager.getSavedVolume(player));
        musicManager.setRadioEnabled(player, musicManager.getSavedRadio(player));

        musicManager.playSongFromCookies(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        musicManager.clearSongPlayer(player);
        musicManager.saveAutoState(player);
        musicManager.saveVolume(player);
        musicManager.saveRadioEnabled(player);

        MusicManager.speakerEnabled.remove(player);
        MusicManager.autoEnabled.remove(player);
        MusicManager.playerVolume.remove(player);
    }

    @EventHandler
    public void songNextEvent(SongNextEvent event) {
        SongPlayer songPlayer = event.getSongPlayer();
        if (songPlayer.getSoundEmitter() instanceof StaticSoundEmitter) return;
        Set<UUID> playerUUIDs = songPlayer.getListeners().keySet();
        for (UUID uuid : playerUUIDs) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (!musicManager.getRadioPlayer().getListeners().containsKey(uuid)) {
                    musicManager.saveTitleToCookie(player);
                    musicManager.saveTickToCookie(player);
                    musicManager.songPlayerSchedule(player, songPlayer);
                }
                player.sendRichMessage("Nyt soi: <yellow>" + songPlayer.getCurrentSong().getMetadata().getTitle());
            }
        }
    }

    @EventHandler
    public void onSongStop(SongEndEvent event) {
        SongPlayer songPlayer = event.getSongPlayer();
        if (songPlayer.getSoundEmitter() instanceof StaticSoundEmitter) return;
        Set<UUID> playerUUIDs = songPlayer.getListeners().keySet();
        for (UUID uuid : playerUUIDs) {
            if (musicManager.getRadioPlayer().getListeners().containsKey(uuid)) return;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                musicManager.clearSongPlayer(player);
                musicManager.clearCookies(player);
            }
        }
    }
}
