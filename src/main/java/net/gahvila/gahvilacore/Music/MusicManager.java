package net.gahvila.gahvilacore.Music;

import cz.koca2000.nbs4j.Song;
import de.leonhard.storage.Json;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.gahvila.gahvilacore.Music.MusicBlocks.MusicBlockManager;
import net.gahvila.gahvilacore.Utils.WorldGuardRegionChecker;
import net.gahvila.gahvilacore.nbsminecraft.NBSAPI;
import net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.player.BukkitSongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.EntitySoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.GlobalSoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.StaticSoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.EntityReference;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.gahvila.gahvilacore.Config.ConfigManager.*;
import static net.gahvila.gahvilacore.GahvilaCore.instance;
import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class MusicManager {
    private MusicBlockManager musicBlockManager;
    public MusicManager() {
    }

    public static ArrayList<Song> songs = new ArrayList<>();
    public static HashMap<String, Song> namedSong = new HashMap<>();
    public static HashMap<Player, SongPlayer> songPlayers = new HashMap<>();
    public static HashMap<Player, Boolean> speakerEnabled = new HashMap<>();
    public static HashMap<Player, Boolean> autoEnabled = new HashMap<>();
    public static HashMap<Player, Byte> playerVolume = new HashMap<>();
    public static HashMap<Player, Byte> playerSpeed = new HashMap<>();
    public static HashMap<Player, Integer> lastMenuPage = new HashMap<>();

    public static NamespacedKey titleKey = new NamespacedKey(instance, "song.title");
    public static NamespacedKey tickKey = new NamespacedKey(instance, "song.tick");
    public static NamespacedKey pauseKey = new NamespacedKey(instance, "song.pause");
    public static NamespacedKey volumeKey = new NamespacedKey(instance, "song.volume");

    public void setMusicBlockManager(MusicBlockManager musicBlockManager) {
        this.musicBlockManager = musicBlockManager;
    }

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
                Month currentMonth = ZonedDateTime.now().getMonth();

                try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    for (String fileName : remoteFiles) {
                        executor.submit(() -> {
                            try {
                                URL fileUrl = new URL(url + fileName);
                                Song song = downloadAndParseSong(fileUrl);
                                if (song != null) {
                                    song.getMetadata().getDescription();
                                    boolean isChristmas = song.getMetadata().getDescription().toLowerCase().contains("christmas");
                                    if (currentMonth == Month.DECEMBER || !isChristmas) {
                                        concurrentSongs.add(song);
                                        concurrentNamedSong.put(song.getMetadata().getTitle(), song);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                songs.addAll(concurrentSongs);
                namedSong.putAll(concurrentNamedSong);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTask(instance, () -> {
                clearRadioPlayer();

                if (musicBlockManager != null) {
                    musicBlockManager.stopAll();
                    musicBlockManager.loadMusicBlocks();
                }

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
            return NBSAPI.INSTANCE.readSongInputStream(in);
        }
    }

    public ArrayList<Song> getSongs() {
        return songs.stream()
                .filter(this::isNotUnlisted) // Apply the filter
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Song> getSongsSorted(Player player) {
        MusicSorting sorting = getSorting(player);

        Comparator<Song> comparator = switch (sorting) {
            case ALPHABETICAL -> Comparator.comparing(song -> song.getMetadata().getTitle(), String.CASE_INSENSITIVE_ORDER);
            case ARTIST -> Comparator.comparing(song -> song.getMetadata().getOriginalAuthor(), String.CASE_INSENSITIVE_ORDER);
            case LENGTH -> Comparator.comparingDouble(song -> (double) song.getSongLengthInSeconds());
        };

        return songs.stream()
                .filter(this::isNotUnlisted)
                .sorted(comparator)
                .toList();
    }

    public Song getSong(String name) {
        Song song = namedSong.get(name);

        if (song != null && isNotUnlisted(song)) {
            return song;
        }

        return null;
    }

    public Song getUnlistedSong(String name){
        return namedSong.get(name);
    }

    private boolean isNotUnlisted(Song song) {
        return !song.getMetadata().getDescription().toLowerCase().contains("unlisted");
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
        songPlayer.stop();
        songPlayers.remove(player);
        clearCookies(player);
    }

    public void createSongPlayer(Player player, Song song, long tick, Boolean playing) {
        setRadioEnabled(player,false);
        clearSongPlayer(player);
        SongPlayer songPlayer;
        if (getSpeakerEnabled(player)) {
            EntityReference entityReference = new EntityReference(player.getEntityId(), player.getUniqueId());
            songPlayer = new BukkitSongPlayer.Builder()
                    .soundEmitter(new EntitySoundEmitter(entityReference))
                    .volume(60)
                    .transposeNotes(false)
                    .build();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!WorldGuardRegionChecker.isInRegion(onlinePlayer, "spawn")) {
                    if (Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null) {
                        CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(onlinePlayer.getUniqueId()).getNow(null);
                        if (!carbonPlayer.ignoring(entityReference.uuid())) {
                            songPlayer.addListener(new AudioListener(onlinePlayer.getEntityId(), onlinePlayer.getUniqueId()));
                        }
                    }
                }
            }
        } else {
            songPlayer = new BukkitSongPlayer.Builder()
                    .soundEmitter(new GlobalSoundEmitter())
                    .transposeNotes(false)
                    .build();
        }
        songPlayer.playSong(song);
        if (playing) {
            playSong(player, songPlayer);
        } else {
            pauseSong(player, songPlayer);
        }
        if (!getSpeakerEnabled(player) && getAutoEnabled(player)) {
            songs.forEach(songPlayer::queueSong);
            songPlayer.loopQueue(true);
            songPlayer.shuffleQueue();
        }
        songPlayer.setTick(tick);

        saveSongPlayer(player, songPlayer);
        songPlayerSchedule(player, songPlayer);

        saveTitleToCookie(player);
        saveTickToCookie(player);
        savePauseToCookie(player);
        saveVolumeToCookie(player);
        songPlayer.addListener(new AudioListener(player.getEntityId(), player.getUniqueId()));
        if (songPlayer.getSoundEmitter() instanceof GlobalSoundEmitter) {
            songPlayer.setVolume(volumeConverter(getVolume(player)));
        }
    }

    //
    //RADIO
    //
    public static HashMap<Player, Boolean> radioEnabled = new HashMap<>();
    public boolean getRadioEnabled(Player player) {
        return radioEnabled.get(player) != null && radioEnabled.get(player);
    }

    public void setRadioEnabled(Player player, boolean option) {
        if (option) {
            addRadioListener(player);
        } else {
            removeRadioListener(player);
        }
        radioEnabled.put(player, option);
    }


    public void saveRadioEnabled(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();
        playerData.set(uuid + "." + "radio", getRadioEnabled(player));
    }
    public boolean getSavedRadio(Player player) {
        Json playerData = new Json("playerdata.json", instance.getDataFolder() + "/data/");
        String uuid = player.getUniqueId().toString();

        if (!playerData.contains(uuid + "." + "radio")) {
            return false;
        }
        return playerData.getBoolean(uuid + "." + "radio");
    }

    private SongPlayer radioPlayer;
    public void createRadioPlayer() {
        SongPlayer songPlayer;

        songPlayer = new BukkitSongPlayer.Builder()
                .soundEmitter(new GlobalSoundEmitter())
                .transposeNotes(false)
                .build();

        if (songs != null && !songs.isEmpty()) {
            Song randomSong = songs.get(new Random().nextInt(songs.size()));
            songPlayer.playSong(randomSong);
        }

        songs.forEach(songPlayer::queueSong);

        songPlayer.loopQueue(true);
        songPlayer.shuffleQueue();
        songPlayer.play();

        radioPlayer = songPlayer;
    }

    public void clearRadioPlayer() {
        radioPlayer.getQueue().clearQueue();
        if (songs != null && !songs.isEmpty()) {
            Song randomSong = songs.get(new Random().nextInt(songs.size()));
            radioPlayer.playSong(randomSong);
        }

        songs.forEach(radioPlayer::queueSong);
    }

    public SongPlayer getRadioPlayer() {
        return radioPlayer;
    }

    public void addRadioListener(Player player) {
        getRadioPlayer().addListener(new AudioListener(player.getEntityId(), player.getUniqueId()));
        if (getRadioPlayer().isPlaying()) {
            player.sendRichMessage("Nyt soi: <yellow>" + getRadioPlayer().getCurrentSong().getMetadata().getTitle());
        }
    }

    public void removeRadioListener(Player player) {
        getRadioPlayer().removeListener(player.getUniqueId());
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
        if (volume >= (byte) 10) {
            playerVolume.put(player, (byte) 10);
        } else playerVolume.put(player, (byte) Math.max(volume, 1));
        saveVolumeToCookie(player);
        if (getSongPlayer(player) != null) {
            SongPlayer sp = getSongPlayer(player);
            if (sp.getSoundEmitter() instanceof GlobalSoundEmitter) {
                sp.setVolume(volumeConverter(getVolume(player)));
            }
        }
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
    // Song speed
    //
    public byte getSpeed(Player player) {
        return playerSpeed.getOrDefault(player, (byte) 1);
    }

    public void setSpeed(Player player, Byte speed) {
        if (speed == 1 || speed == 2 || speed == 4 || speed == 8) {
            playerSpeed.put(player, speed);
        }
    }

    //
    //play & pause
    //
    public void playSong(Player player, SongPlayer songPlayer) {
        switch (getSpeed(player)) {
            case 8:
                songPlayer.play();
            case 4:
                songPlayer.play();
            case 2:
                songPlayer.play();
            case 1:
                songPlayer.play();
                break;
        }
    }

    public void pauseSong(Player player, SongPlayer songPlayer) {
        songPlayer.pause();
    }

    //
    // Miscallaneous
    //
    public static WeakHashMap<Player, BossBar> progressBars = new WeakHashMap<>();
    public void songPlayerSchedule(Player player, SongPlayer songPlayer) {
        if (progressBars.containsKey(player)) {
            progressBars.get(player).removeViewer(player);
            progressBars.remove(player);
        }
        BossBar progressBar = BossBar.bossBar(toMM("<aqua>" + songPlayer.getCurrentSong().getMetadata().getOriginalAuthor() + " - " +
                songPlayer.getCurrentSong().getMetadata().getTitle() + "</aqua>"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        player.showBossBar(progressBar);
        progressBars.put(player, progressBar);
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, task -> {
            Song currentSong = songPlayer.getCurrentSong();
            if (currentSong == null) {
                progressBar.removeViewer(player);
                task.cancel();
                return;
            }
            saveTickToCookie(player);
            double progress = (double) songPlayer.getTick() / songPlayer.getCurrentSong().getSongLength();
            if (progress >= 1.0 || progress < 0){
                progressBar.removeViewer(player);
                task.cancel();
                return;
            }
            progressBar.progress((float) progress);
            if (!songPlayer.isPlaying()){
                progressBar.name(toMM("<red>" + songPlayer.getCurrentSong().getMetadata().getOriginalAuthor() + " - " +
                        songPlayer.getCurrentSong().getMetadata().getTitle() + "</red>"));
                progressBar.color(BossBar.Color.RED);
            } else {
                progressBar.name(toMM("<aqua>" + songPlayer.getCurrentSong().getMetadata().getOriginalAuthor() + " - " +
                        songPlayer.getCurrentSong().getMetadata().getTitle() + "</aqua>"));
                progressBar.color(BossBar.Color.BLUE);
            }
        }, 0, 20);


        if (songPlayer.getSoundEmitter() instanceof EntitySoundEmitter entitySoundEmitter) {
            boolean carbonEnabled = Bukkit.getServer().getPluginManager().getPlugin("CarbonChat") != null;
            boolean wgEnabled = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null;

            Bukkit.getScheduler().runTaskTimer(instance, task2 -> {
                Song currentSong = songPlayer.getCurrentSong();
                if (currentSong == null) {
                    task2.cancel();
                    return;
                }
                double progress = (double) songPlayer.getTick() / songPlayer.getCurrentSong().getSongLength();
                if (progress >= 1.0 || progress < 0){
                    task2.cancel();
                    return;
                }

                if (carbonEnabled || wgEnabled) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (wgEnabled) {
                            if (!WorldGuardRegionChecker.isInRegion(onlinePlayer, "spawn")) {
                                songPlayer.addListener(new AudioListener(onlinePlayer.getEntityId(), onlinePlayer.getUniqueId()));
                            } else {
                                songPlayer.removeListener(onlinePlayer.getUniqueId());
                            }
                        }

                        if (carbonEnabled) {
                            CarbonPlayer carbonPlayer = CarbonChatProvider.carbonChat().userManager().user(onlinePlayer.getUniqueId()).getNow(null);
                            if (carbonPlayer.ignoring((entitySoundEmitter.entityReference.uuid()))) {
                                songPlayer.removeListener(onlinePlayer.getUniqueId());
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
        float lengthInSeconds = (float) song.getSongLengthInSeconds();

        int minutes = (int) (lengthInSeconds / 60);
        int seconds = (int) (lengthInSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public Integer getPage(Player player) {
        return lastMenuPage.getOrDefault(player, 0);
    }

    public void setPage(Player player, Integer page) {
        lastMenuPage.put(player, page);
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
            player.storeCookie(titleKey, songPlayer.getCurrentSong().getMetadata().getTitle().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void saveTickToCookie(Player player) {
        SongPlayer songPlayer = getPlayingSongPlayer(player);
        if (songPlayer != null) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putLong(songPlayer.getTick());
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

    private CompletableFuture<Long> retrieveTickCookie(Player player) {
        return player.retrieveCookie(tickKey)
                .thenApply(bytes -> bytes != null ? ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong() : null)
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
            CompletableFuture<Long> tickFuture = retrieveTickCookie(player);
            CompletableFuture<Boolean> pauseFuture = retrievePauseCookie(player);
            CompletableFuture<Byte> volumeFuture = retrieveVolumeCookie(player);

            CompletableFuture.allOf(titleFuture, tickFuture, pauseFuture, volumeFuture)
                    .thenRun(() -> {
                        String title = titleFuture.join();
                        Long tick = tickFuture.join();
                        Boolean pause = pauseFuture.join();
                        Byte volume = volumeFuture.join();

                        if (title == null || tick == null || pause == null) {
                            return;
                        }

                        if (volume != null) {
                            setVolume(player, volume);
                        }
                        for (Song song : getSongs()) {
                            if (song.getMetadata().getTitle().equals(title)) {
                                clearSongPlayer(player);
                                createSongPlayer(player, song, tick, pause);
                                player.sendRichMessage("<hover:show_text:'Klikkaa avataksesi asetukset'><click:run_command:'/music'>Musiikki soi edelleen. Asetukset löydät komennolla <yellow>/music</yellow>.</click></hover>");
                            }
                        }
                    });
        });
    }

    /**
     * Music sorting
     */
    public void setSorting(Player player, MusicSorting sorting) {
        setData(player, "musicsorting", sorting.name());
    }

    public void changeSorting(Player player) {
        MusicSorting currentSorting = getSorting(player);

        MusicSorting[] values = MusicSorting.values();

        int nextOrdinal = (currentSorting.ordinal() + 1) % values.length;
        MusicSorting nextSorting = values[nextOrdinal];

        setData(player, "musicsorting", nextSorting.name());
    }

    public MusicSorting getSorting(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        CachedMetaData metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
        String sortingValue = metaData.getMetaValue("musicsorting");

        if (sortingValue == null) return MusicSorting.ALPHABETICAL;

        try {
            return MusicSorting.valueOf(sortingValue);
        } catch (IllegalArgumentException e) {
            return MusicSorting.ALPHABETICAL;
        }
    }


    /**
     * LuckPerms data saving
     */
    private void setData(Player player, String key, String value) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getPlayerAdapter(Player.class).getUser(player);
        MetaNode node = MetaNode.builder(key, value).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(key)));
        user.data().add(node);
        api.getUserManager().saveUser(user);
    }
}
