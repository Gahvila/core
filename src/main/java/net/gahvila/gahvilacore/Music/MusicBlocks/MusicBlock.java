package net.gahvila.gahvilacore.Music.MusicBlocks;

import cz.koca2000.nbs4j.Song;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Music.MusicManager;
import net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.player.BukkitSongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.SongPlayer;
import net.gahvila.gahvilacore.nbsminecraft.player.emitter.StaticSoundEmitter;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MusicBlock {

    private final GahvilaCore plugin;
    private final MusicManager musicManager;
    private final String name;
    private final Location location;
    private final List<String> songTitles;
    private final int maxRange;
    private final int maxVolume;

    private final List<Integer> triggerTicks;

    private long lastTriggeredTotalTime = -1;
    private long lastCheckedTime = -1;
    private boolean isPlaying = false;

    private SongPlayer masterPlayer;
    private final Map<UUID, SongPlayer> playerSongPlayers = new ConcurrentHashMap<>();
    private Song lastMasterSong = null;

    private BukkitTask particleTask;
    private BukkitTask updateTask;
    private BukkitTask schedulerTask;
    private SoundLocation soundLocation;

    public MusicBlock(GahvilaCore plugin, MusicManager musicManager, String name, Location location,
                      List<String> songTitles, int range, int volume, List<Integer> triggerTicks) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.name = name;
        this.location = location;
        this.songTitles = songTitles;
        this.maxRange = range;
        this.maxVolume = volume;
        this.triggerTicks = triggerTicks;
    }

    public void start() {
        if (songTitles.isEmpty()) {
            Bukkit.getLogger().warning("Music block '" + name + "' has no songs and will not be started.");
            return;
        }

        this.soundLocation = new SoundLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );

        // Check if list contains -1 (Infinite Loop Mode)
        if (triggerTicks.contains(-1)) {
            setupMasterPlayer(true); // Loop = true
            setupTasks();
        } else {
            startSchedulerTask();
        }
    }

    private void setupMasterPlayer(boolean loop) {
        if (this.masterPlayer != null) return;

        this.masterPlayer = new BukkitSongPlayer.Builder()
                .soundEmitter(new StaticSoundEmitter(this.soundLocation))
                .transposeNotes(false)
                .volume(0)
                .build();

        int validSongCount = 0;
        for (String title : songTitles) {
            Song song = musicManager.getUnlistedSong(title);
            if (song != null) {
                masterPlayer.queueSong(song);
                validSongCount++;
            }
        }

        if (validSongCount == 0) {
            Bukkit.getLogger().warning("Music block '" + name + "' found no valid songs.");
            masterPlayer = null;
            return;
        }

        this.masterPlayer.loopQueue(loop);
        this.masterPlayer.shuffleQueue();
        this.masterPlayer.play();

        this.isPlaying = true;
        this.lastMasterSong = masterPlayer.getCurrentSong();
    }

    private void triggerPlayback() {
        if (this.isPlaying) return;
        setupMasterPlayer(false);
        setupTasks();
    }

    private void startSchedulerTask() {
        this.schedulerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            World world = location.getWorld();
            if (world == null) return;

            long currentFullTime = world.getFullTime();
            long currentDayTime = world.getTime();

            if (lastCheckedTime != -1 && currentFullTime < lastCheckedTime) {
                this.lastTriggeredTotalTime = -1;
            }
            this.lastCheckedTime = currentFullTime;

            if (shouldTrigger(currentDayTime, currentFullTime)) {
                triggerPlayback();
                this.lastTriggeredTotalTime = currentFullTime;
            }
        }, 0L, 20L);
    }

    private boolean shouldTrigger(long currentDayTime, long currentFullTime) {
        if (lastTriggeredTotalTime != -1 && Math.abs(currentFullTime - lastTriggeredTotalTime) < 100) {
            return false;
        }

        for (int tick : triggerTicks) {
            if (Math.abs(currentDayTime - tick) <= 30) {
                return true;
            }
        }
        return false;
    }

    public void stop() {
        if (schedulerTask != null) {
            schedulerTask.cancel();
            schedulerTask = null;
        }
        stopActivePlaybackTasks();
    }

    private void stopActivePlaybackTasks() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }

        playerSongPlayers.values().forEach(SongPlayer::stop);
        playerSongPlayers.clear();

        if (masterPlayer != null) {
            masterPlayer.stop();
            masterPlayer = null;
        }
        this.isPlaying = false;
    }

    private void setupTasks() {
        startParticleTask();
        startUpdateTask();
    }

    private void startParticleTask() {
        Location particleLoc = location.clone().add(0.5, 1.2, 0.5);
        this.particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (masterPlayer != null && masterPlayer.isPlaying() && !playerSongPlayers.isEmpty()) {
                location.getWorld().spawnParticle(Particle.NOTE, particleLoc, 1, 0, 0, 0, 0);
            }
        }, 0L, 20L);
    }

    private void startUpdateTask() {
        final double maxRangeSquared = (double) this.maxRange * this.maxRange;
        final World blockWorld = location.getWorld();

        if (blockWorld == null) return;

        this.updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (masterPlayer == null) return;

            if (!masterPlayer.isPlaying()) {
                stopActivePlaybackTasks();
                return;
            }

            Song currentMasterSong = masterPlayer.getCurrentSong();
            long currentMasterTick = masterPlayer.getTick();

            if (currentMasterSong != null && !currentMasterSong.equals(lastMasterSong)) {
                for (SongPlayer playerSP : playerSongPlayers.values()) {
                    playerSP.playSong(currentMasterSong);
                    playerSP.setTick(currentMasterTick);
                }
                lastMasterSong = currentMasterSong;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().equals(blockWorld)) {
                    if (playerSongPlayers.containsKey(player.getUniqueId())) {
                        removePlayer(player);
                    }
                    continue;
                }

                double distanceSq = player.getLocation().distanceSquared(location);
                boolean inRange = distanceSq <= maxRangeSquared;
                boolean isListening = playerSongPlayers.containsKey(player.getUniqueId());

                if (inRange) {
                    SongPlayer sp = playerSongPlayers.get(player.getUniqueId());
                    if (sp == null) {
                        sp = addPlayer(player);
                    }
                    if (sp == null) continue;

                    sp.setVolume(getNewVolume(distanceSq));

                } else if (isListening) {
                    removePlayer(player);
                }
            }
        }, 0L, 5L);
    }

    private int getNewVolume(double distanceSq) {
        double distance = Math.sqrt(distanceSq);
        double closeness = Math.max(0.0, 1.0 - (distance / this.maxRange));
        double curvedCloseness = closeness * closeness;

        return (int) (this.maxVolume * curvedCloseness);
    }

    public SongPlayer addPlayer(Player player) {
        if (playerSongPlayers.containsKey(player.getUniqueId()) || masterPlayer == null) {
            return null;
        }

        SongPlayer newPlayerSP = new BukkitSongPlayer.Builder()
                .soundEmitter(new StaticSoundEmitter(this.soundLocation))
                .transposeNotes(false)
                .volume(0)
                .build();

        newPlayerSP.addListener(new AudioListener(player.getEntityId(), player.getUniqueId()));

        for (String title : songTitles) {
            Song song = musicManager.getUnlistedSong(title);
            if (song != null) {
                newPlayerSP.queueSong(song);
            }
        }
        newPlayerSP.loopQueue(masterPlayer.getQueue().isLooping());

        Song masterSong = masterPlayer.getCurrentSong();
        if (masterSong != null) {
            newPlayerSP.playSong(masterSong);
            newPlayerSP.setTick(masterPlayer.getTick());
        }
        newPlayerSP.play();

        playerSongPlayers.put(player.getUniqueId(), newPlayerSP);
        return newPlayerSP;
    }

    public void removePlayer(Player player) {
        SongPlayer sp = playerSongPlayers.remove(player.getUniqueId());
        if (sp != null) {
            sp.stop();
        }
    }

    public String getName() { return name; }
    public Location getLocation() { return location; }
    public List<String> getSongTitles() { return songTitles; }
    public int getRange() { return maxRange; }
    public int getVolume() { return maxVolume; }
    public List<Integer> getTriggerTicks() { return triggerTicks; }
}