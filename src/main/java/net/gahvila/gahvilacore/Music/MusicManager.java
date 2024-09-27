package net.gahvila.gahvilacore.Music;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.leonhard.storage.Json;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.gahvila.gahvilacore.Utils.WorldGuardRegionChecker;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;
import static net.gahvila.gahvilacore.GahvilaCore.instance;

public class MusicManager {

    public MusicManager() {
    }

    public static ArrayList<Song> songs = new ArrayList<>();
    public static HashMap<String, Song> namedSong = new HashMap<>();
    public static HashMap<Player, SongPlayer> songPlayers = new HashMap<>();
    public static HashMap<Player, Boolean> speakerEnabled = new HashMap<>();
    public static HashMap<Player, Boolean> autoEnabled = new HashMap<>();
    public static HashMap<Player, Integer> playerVolume = new HashMap<>();

    public static NamespacedKey titleKey = new NamespacedKey(instance, "musicplayer.song.title");
    public static NamespacedKey tickKey = new NamespacedKey(instance, "musicplayer.song.tick");

    public static Boolean isLoaded = false;


    public Boolean getLoadState() {
        return isLoaded;
    }

    public void loadSongs() {
        isLoaded = false;
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            List<Song> concurrentSongs = new CopyOnWriteArrayList<>();
            Map<String, Song> concurrentNamedSong = new ConcurrentHashMap<>();

            if (songs != null) songs.clear();
            if (namedSong != null) namedSong.clear();

            File folder = new File(instance.getDataFolder(), "songs");
            File[] songFiles = folder.listFiles();
            if (songFiles == null || songFiles.length == 0) return;

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (File file : songFiles) {
                    executor.submit(() -> {
                        Song song = NBSDecoder.parse(file);
                        if (song != null) {
                            concurrentSongs.add(song);
                            concurrentNamedSong.put(song.getTitle(), song);
                        }
                    });
                }
            }

            concurrentSongs.parallelStream()
                    .sorted((song1, song2) -> song1.getTitle().compareToIgnoreCase(song2.getTitle()))
                    .forEachOrdered(songs::add);

            namedSong.putAll(concurrentNamedSong);

            isLoaded = true;
        });
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public Song getSong(String name){
        return namedSong.get(name);
    }

    public void saveSongPlayer(Player player, SongPlayer songPlayer){
        songPlayers.put(player, songPlayer);
    }

    public SongPlayer getSongPlayer(Player player){
        return songPlayers.get(player);
    }

    public void clearSongPlayer(Player player){
        if (!songPlayers.containsKey(player)) return;
        if (songPlayers.get(player) == null) songPlayers.remove(player);
        SongPlayer songPlayer = songPlayers.get(player);
        songPlayer.destroy();
        songPlayers.remove(player);
        clearCookies(player);
    }

    //SPEAKER MODE
    public boolean getSpeakerEnabled(Player player) {
        return speakerEnabled.get(player) != null && speakerEnabled.get(player);
    }

    public void setSpeakerEnabled(Player player, boolean option) {
        speakerEnabled.put(player, option);
    }

    //AUTO PLAY
    public boolean getAutoEnabled(Player player) {
        return autoEnabled.get(player);
    }

    public void setAutoEnabled(Player player, boolean option) {
        autoEnabled.put(player, option);
    }

    public void saveAutoState(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "radioState", getAutoEnabled(player));
    }
    public boolean getSavedAutoState(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        if (!playerData.contains(uuid + "." + "radioState")) {
            return true;
        }
        return playerData.getBoolean(uuid + "." + "radioState");
    }

    //VOLUME SELECTOR
    public int getVolume(Player player) {
        return playerVolume.getOrDefault(player, 5);
    }

    public void setVolume(Player player, int volume) {
        if (volume >= 10) {
            playerVolume.put(player, 10);
        } else playerVolume.put(player, Math.max(volume, 1));
    }

    public void increaseVolume(Player player) {
        setVolume(player, getVolume(player) + 1);
    }

    public void reduceVolume(Player player) {
        setVolume(player, getVolume(player) - 1);
    }

    public void saveVolume(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "volume", getVolume(player));
    }
    public int getSavedVolume(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        if (!playerData.contains(uuid + "." + "volume")) {
            return 5;
        }
        return playerData.getInt(uuid + "." + "volume");
    }

    //

    public void songPlayerSchedule(Player player, SongPlayer songPlayer) {
        double length = songPlayer.getSong().getLength();
        BossBar progressBar = BossBar.bossBar(toMM("<aqua>" + songPlayer.getSong().getOriginalAuthor() + " - " +
                songPlayer.getSong().getTitle() + "</aqua>"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        player.showBossBar(progressBar);
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, task -> {
            saveTickToCookie(player);
            double progress = (double) songPlayer.getTick() / length;
            if (progress >= 1.0 || progress < 0){
                progressBar.removeViewer(player);
                task.cancel();
                return;
            }
            progressBar.progress((float) progress);

            if (!songPlayer.isPlaying()){
                progressBar.name(toMM("<red>" + songPlayer.getSong().getOriginalAuthor() + " - " +
                        songPlayer.getSong().getTitle() + "</red>"));
                progressBar.color(BossBar.Color.RED);
            } else {
                progressBar.name(toMM("<aqua>" + songPlayer.getSong().getOriginalAuthor() + " - " +
                        songPlayer.getSong().getTitle() + "</aqua>"));
                progressBar.color(BossBar.Color.BLUE);
            }
        }, 0, 1);

        if (songPlayer instanceof EntitySongPlayer){
            boolean carbonEnabled = Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null;
            boolean wgEnabled = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null;

            Bukkit.getScheduler().runTaskTimer(instance, task2 -> {
                double progress = (double) songPlayer.getTick() / length;
                if (progress >= 1.0 || progress < 0){
                    task2.cancel();
                    return;
                }

                if (carbonEnabled || wgEnabled) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (wgEnabled) {
                            if (!WorldGuardRegionChecker.isInRegion(onlinePlayer, "spawn")) {
                                songPlayer.addPlayer(onlinePlayer);
                                if (songPlayer.isPlaying()) {
                                    onlinePlayer.spawnParticle(Particle.NOTE, player.getLocation().add(0, 2, 0), 1);
                                }
                            } else {
                                songPlayer.removePlayer(onlinePlayer);
                            }
                        } else {
                            if (songPlayer.isPlaying()) {
                                onlinePlayer.spawnParticle(Particle.NOTE, player.getLocation().add(0, 2, 0), 1);
                            }
                        }

                        if (carbonEnabled) {
                            CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(onlinePlayer.getUniqueId()).getNow(null);
                            if (carbonPlayer.ignoring(((EntitySongPlayer) songPlayer).getEntity().getUniqueId())) {
                                songPlayer.removePlayer(onlinePlayer);
                            }
                        }
                    }
                }
            }, 10L, 10);
        }
    }

    public byte volumeConverter(int volume) {
        return switch(volume) {
            case 1 -> (byte) 10;
            case 2 -> (byte) 20;
            case 3 -> (byte) 30;
            case 4 -> (byte) 40;
            case 5 -> (byte) 50;
            case 6 -> (byte) 60;
            case 7 -> (byte) 70;
            case 8 -> (byte) 80;
            case 9 -> (byte) 90;
            case 10 -> (byte) 100;
            default -> 50;
        };
    }

    public String songLength(Song song) {
        float lengthInSeconds = song.getLength() / song.getSpeed();

        int minutes = (int) (lengthInSeconds / 60);
        int seconds = (int) (lengthInSeconds % 60);

        return String.format("%d:%02d", minutes, seconds);
    }

    //Song player creation
    public void createSP(Player player, Song song, Short tick) {
        clearSongPlayer(player);
        Playlist playlist = new Playlist(song);
        RadioSongPlayer rsp = new RadioSongPlayer(playlist);
        rsp.setChannelMode(new MonoStereoMode());
        rsp.setVolume(volumeConverter(getVolume(player)));
        rsp.addPlayer(player);
        if (tick != null){
            rsp.setTick(tick);
        }
        rsp.setPlaying(true);
        if (getAutoEnabled(player)) {
            ArrayList<Song> songs = getSongs();
            for (Song playlistSong : songs) {
                playlist.add(playlistSong);
            }
            rsp.setRandom(true);
            rsp.setRepeatMode(RepeatMode.ALL);
        } else {
            rsp.setRandom(false);
            rsp.setRepeatMode(RepeatMode.NO);
        }
        saveSongPlayer(player, rsp);
        Bukkit.getScheduler().runTaskLater(instance, () -> songPlayerSchedule(player, rsp), 5);

        saveTitleToCookie(player);
        saveTickToCookie(player);
    }

    public void createESP(Player player, Song song, Short tick) {
        clearSongPlayer(player);
        EntitySongPlayer esp = new EntitySongPlayer(song);
        esp.setEntity(player);
        esp.setVolume((byte) 45);
        esp.setDistance(24);
        if (tick != null){
            esp.setTick(tick);
        }
        esp.setPlaying(true);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!WorldGuardRegionChecker.isInRegion(onlinePlayer, "spawn")){
                if(Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null) {
                    CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(onlinePlayer.getUniqueId()).getNow(null);
                    if (!carbonPlayer.ignoring(esp.getEntity().getUniqueId())) {
                        esp.addPlayer(onlinePlayer);
                    }
                }
            }
        }
        saveSongPlayer(player, esp);
        Bukkit.getScheduler().runTaskLater(instance, () -> songPlayerSchedule(player, esp), 5);

        saveTitleToCookie(player);
        saveTickToCookie(player);
    }

    public void saveTitleToCookie(Player player) {
        if (getSongPlayer(player) != null && getSongPlayer(player).isPlaying()) {
            SongPlayer songPlayer = getSongPlayer(player);
            player.storeCookie(titleKey, songPlayer.getSong().getTitle().getBytes());
        }
    }

    public void saveTickToCookie(Player player) {
        if (getSongPlayer(player) != null && getSongPlayer(player).isPlaying()) {
            SongPlayer songPlayer = getSongPlayer(player);

            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(songPlayer.getTick());

            player.storeCookie(tickKey, buffer.array());
        }
    }
    private CompletableFuture<String> retrieveTitleCookie(Player player) {
        return player.retrieveCookie(titleKey)
                .thenApply(bytes -> bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null);
    }

    private CompletableFuture<Short> retrieveTickCookie(Player player) {
        return player.retrieveCookie(tickKey)
                .thenApply(bytes -> bytes != null ? ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort() : null);
    }

    public void clearCookies(Player player) {
        player.storeCookie(titleKey, new byte[]{});
        player.storeCookie(tickKey, new byte[]{});
    }

    public void playSongFromCookies(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {
            String title = retrieveTitleCookie(player).join();
            Short tick = retrieveTickCookie(player).join();

            if (title == null || tick == null) {
                return;
            }

            for (Song song : getSongs()) {
                if (song.getTitle().equals(title)) {
                    clearSongPlayer(player);
                    setVolume(player, 5);
                    createSP(player, song, tick);
                }
            }
        });
    }
}
