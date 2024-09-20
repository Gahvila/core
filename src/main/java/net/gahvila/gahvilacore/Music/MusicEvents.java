package net.gahvila.gahvilacore.Music;

import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.gahvila.gahvilacore.Utils.WorldGuardRegionChecker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.gahvila.gahvilacore.GahvilaCore.instance;

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

        musicManager.playSongFromCookies(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        musicManager.clearSongPlayer(player);
        musicManager.saveAutoState(player);
        musicManager.saveVolume(player);

        MusicManager.speakerEnabled.remove(player);
        MusicManager.autoEnabled.remove(player);
        MusicManager.playerVolume.remove(player);
    }

    @EventHandler
    public void songNextEvent(SongNextEvent event) {
        SongPlayer songPlayer = event.getSongPlayer();
        Set<UUID> playerUUIDs = songPlayer.getPlayerUUIDs();
        for (UUID uuid : playerUUIDs) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                musicManager.saveTitleToCookie(player);
                musicManager.saveTickToCookie(player);
                player.sendRichMessage("Nyt soi: <yellow>" + songPlayer.getSong().getTitle());
                musicManager.songPlayerSchedule(player, songPlayer);
            }
        }
    }
}
