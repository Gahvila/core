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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static net.gahvila.gahvilacore.Config.ConfigManager.*;
import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class MusicManager {

    public MusicManager() {
    }

    public static ArrayList<Song> songs = new ArrayList<>();
    public static HashMap<String, Song> namedSong = new HashMap<>();
    public static HashMap<Player, SongPlayer> songPlayers = new HashMap<>();
    public static HashMap<Player, Boolean> speakerEnabled = new HashMap<>();
    public static HashMap<Player, Boolean> autoEnabled = new HashMap<>();
    public static HashMap<Player, Byte> playerVolume = new HashMap<>();

    public static NamespacedKey titleKey = new NamespacedKey(instance, "song.title");
    public static NamespacedKey tickKey = new NamespacedKey(instance, "song.tick");
    public static NamespacedKey pauseKey = new NamespacedKey(instance, "song.pause");
    public static NamespacedKey volumeKey = new NamespacedKey(instance, "song.volume");



    //
    //Song loading
    //
    public static Boolean isLoading = false;
    public static Boolean isLoaded = false;

    public Boolean getLoadState() {
        return isLoaded;
    }

    private static String username;
    private static String password;
    private static String url;

    public void loadSongs(Consumer<Long> onComplete) {
        if (isLoading) return;
        isLoading = true;
        isLoaded = false;
        long startTime = System.currentTimeMillis();

        username = getDownloadUsername();
        password = getDownloadPassword();
        url = getDownloadUrl();

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            List<Song> concurrentSongs = new CopyOnWriteArrayList<>();
            Map<String, Song> concurrentNamedSong = new ConcurrentHashMap<>();

            if (songs != null) songs.clear();
            if (namedSong != null) namedSong.clear();

            try {
                List<String> remoteFiles = getRemoteFileList();

                //create a executor for virtual threads
                try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    for (String fileName : remoteFiles) {
                        executor.submit(() -> {
                            try {
                                URL fileUrl = new URL(url + fileName);
                                Song song = downloadAndParseSong(fileUrl);
                                if (song != null) {
                                    concurrentSongs.add(song);
                                    concurrentNamedSong.put(song.getTitle(), song);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }


                //song sorting
                concurrentSongs.parallelStream()
                        .sorted((song1, song2) -> song1.getTitle().compareToIgnoreCase(song2.getTitle()))
                        .forEachOrdered(songs::add);

                namedSong.putAll(concurrentNamedSong);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTask(instance, () -> {
                isLoaded = true;
                isLoading = false;
                long executionTime = System.currentTimeMillis() - startTime;
                onComplete.accept(executionTime);
            });
        });
    }

    private List<String> getRemoteFileList() throws Exception {
        URL directoryUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) directoryUrl.openConnection();

        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

        //create reader for the response from server
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        List<String> fileList = new ArrayList<>();

        //read response line by line
        while ((inputLine = in.readLine()) != null) {
            //extract lines with .nbs in them
            if (inputLine.contains(".nbs")) {
                String fileName = inputLine.substring(inputLine.indexOf("href=\"") + 6, inputLine.indexOf(".nbs") + 4);
                fileList.add(fileName);
            }
        }
        in.close();
        return fileList;
    }

    private Song downloadAndParseSong(URL fileUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();

        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new IOException("Failed to authenticate: " + responseCode);
        }

        try (InputStream in = connection.getInputStream()) {
            return NBSDecoder.parse(in);
        }
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public Song getSong(String name){
        return namedSong.get(name);
    }

    //
    //Song player
    //

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

    public void createSP(Player player, Song song, Short tick, Boolean playing) {
        clearSongPlayer(player);
        Playlist playlist = new Playlist(song);
        RadioSongPlayer rsp = new RadioSongPlayer(playlist);
        rsp.setChannelMode(new MonoStereoMode());
        rsp.setVolume(volumeConverter(getVolume(player)));
        rsp.addPlayer(player);
        if (tick != null){
            rsp.setTick(tick);
        }
        rsp.setPlaying(playing);
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
        Bukkit.getScheduler().runTaskLater(instance, () -> songPlayerSchedule(player, rsp), 3);

        saveTitleToCookie(player);
        saveTickToCookie(player);
        savePauseToCookie(player);
        saveVolumeToCookie(player);
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
        Bukkit.getScheduler().runTaskLater(instance, () -> songPlayerSchedule(player, esp), 3);

        saveTitleToCookie(player);
        saveTickToCookie(player);
        savePauseToCookie(player);
        saveVolumeToCookie(player);
    }

    //
    //SPEAKER MODE
    //
    public boolean getSpeakerEnabled(Player player) {
        return speakerEnabled.get(player) != null && speakerEnabled.get(player);
    }

    public void setSpeakerEnabled(Player player, boolean option) {
        speakerEnabled.put(player, option);
    }

    //
    //Automatic playback, whether to shuffle
    //
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

    //
    //Volume selection
    //
    public byte getVolume(Player player) {
        return playerVolume.getOrDefault(player, (byte) 5);
    }

    public void setVolume(Player player, byte volume) {
        System.err.println("volume set to " + volume);
        if (volume >= (byte) 10) {
            playerVolume.put(player, (byte) 10);
        } else playerVolume.put(player, (byte) Math.max(volume, 1));
        saveVolumeToCookie(player);
    }

    public void increaseVolume(Player player) {
        setVolume(player, (byte) (getVolume(player) + 1));
    }

    public void reduceVolume(Player player) {
        setVolume(player, (byte) (getVolume(player) - 1));
    }

    public void saveVolume(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "volume", getVolume(player));
    }
    public byte getSavedVolume(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        if (!playerData.contains(uuid + "." + "volume")) {
            return (byte) 5;
        }

        int volume = playerData.getInt(uuid + "." + "volume");
        return (byte) volume;
    }

    //
    // Miscallaneous
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

    //
    // Utilities
    //
    public byte volumeConverter(byte volume) {
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

    //
    // cookies
    //
    private SongPlayer getPlayingSongPlayer(Player player) {
        SongPlayer songPlayer = getSongPlayer(player);
        return (songPlayer != null && songPlayer.isPlaying()) ? songPlayer : null;
    }

    public void saveTitleToCookie(Player player) {
        SongPlayer songPlayer = getPlayingSongPlayer(player);
        if (songPlayer != null) {
            player.storeCookie(titleKey, songPlayer.getSong().getTitle().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void saveTickToCookie(Player player) {
        SongPlayer songPlayer = getPlayingSongPlayer(player);
        if (songPlayer != null) {
            ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(songPlayer.getTick());
            player.storeCookie(tickKey, buffer.array());
        }
    }

    public void savePauseToCookie(Player player) {
        SongPlayer songPlayer = getSongPlayer(player);
        if (songPlayer != null) {
            byte[] byteArray = {(byte) (songPlayer.isPlaying() ? 1 : 0)};
            player.storeCookie(pauseKey, byteArray);
        }
    }

    public void saveVolumeToCookie(Player player) {
        SongPlayer songPlayer = getPlayingSongPlayer(player);
        if (songPlayer != null) {
            byte[] volumeArray = new byte[]{getVolume(player)};
            player.storeCookie(volumeKey, volumeArray);
        }
    }

    private CompletableFuture<String> retrieveTitleCookie(Player player) {
        return player.retrieveCookie(titleKey)
                .thenApply(bytes -> bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private CompletableFuture<Short> retrieveTickCookie(Player player) {
        return player.retrieveCookie(tickKey)
                .thenApply(bytes -> bytes != null ? ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort() : null)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private CompletableFuture<Boolean> retrievePauseCookie(Player player) {
        return player.retrieveCookie(pauseKey)
                .thenApply(bytes -> bytes != null ? bytes[0] == 1 : null)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private CompletableFuture<Byte> retrieveVolumeCookie(Player player) {
        return player.retrieveCookie(volumeKey)
                .thenApply(bytes -> bytes != null ? ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).get() : null)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    public void clearCookies(Player player) {
        player.storeCookie(titleKey, new byte[]{});
        player.storeCookie(tickKey, new byte[]{});
    }

    public void playSongFromCookies(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {
            CompletableFuture<String> titleFuture = retrieveTitleCookie(player);
            CompletableFuture<Short> tickFuture = retrieveTickCookie(player);
            CompletableFuture<Boolean> pauseFuture = retrievePauseCookie(player);
            CompletableFuture<Byte> volumeFuture = retrieveVolumeCookie(player);

            CompletableFuture.allOf(titleFuture, tickFuture, pauseFuture, volumeFuture)
                    .thenRun(() -> {
                        String title = titleFuture.join();
                        Short tick = tickFuture.join();
                        Boolean pause = pauseFuture.join();
                        Byte volume = volumeFuture.join();

                        if (title == null || tick == null || pause == null) {
                            return;
                        }

                        if (volume != null) {
                            setVolume(player, volume);
                        }

                        for (Song song : getSongs()) {
                            if (song.getTitle().equals(title)) {
                                clearSongPlayer(player);
                                createSP(player, song, tick, pause);
                            }
                        }
                    });
        });
    }

    //
    //Favorited songs
    //
    public static HashMap<Player, ArrayList<Song>> favorited = new HashMap<>();


    public void addFavorited(Player player, Song song) {
        favorited.computeIfAbsent(player, k -> new ArrayList<>());

        favorited.get(player).add(song);
    }

    public void removeFavorited(Player player, Song song) {
        if (favorited.containsKey(player)) {
            ArrayList<Song> playerSongs = favorited.get(player);
            playerSongs.remove(song);
        }
    }

    public void toggleFavorited(Player player, Song song) {
        favorited.computeIfAbsent(player, k -> new ArrayList<>());

        if (favorited.get(player).contains(song)) {
            removeFavorited(player, song);
        } else {
            addFavorited(player, song);
        }
    }

    public ArrayList<Song> getFavoritedSongs(Player player) {
        return favorited.getOrDefault(player, new ArrayList<>());
    }

    public boolean isFavorited(Player player, Song song) {
        return favorited.getOrDefault(player, new ArrayList<>()).contains(song);
    }
}
