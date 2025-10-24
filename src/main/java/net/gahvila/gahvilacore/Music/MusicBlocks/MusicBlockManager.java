package net.gahvila.gahvilacore.Music.MusicBlocks;

import de.leonhard.storage.Json;
import net.gahvila.gahvilacore.GahvilaCore;
import net.gahvila.gahvilacore.Music.MusicManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MusicBlockManager implements Listener {

    private final GahvilaCore plugin;
    private final MusicManager musicManager;
    private final Json musicBlockData;
    private final Map<String, MusicBlock> activeMusicBlocks = new ConcurrentHashMap<>();

    private static final int DEFAULT_RANGE = 16;
    private static final int DEFAULT_VOLUME = 100;

    public MusicBlockManager(GahvilaCore plugin, MusicManager musicManager) {
        this.plugin = plugin;
        this.musicManager = musicManager;
        this.musicBlockData = new Json("musicblocks.json", plugin.getDataFolder() + "/data/");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean createMusicBlock(String name, Location location, List<String> songTitles, int range, int volume) {
        if (activeMusicBlocks.containsKey(name)) {
            return false;
        }

        MusicBlock block = new MusicBlock(plugin, musicManager, name, location, songTitles, range, volume);
        block.start();
        activeMusicBlocks.put(name, block);
        saveMusicBlock(block);
        return true;
    }

    public boolean removeMusicBlock(String name) {
        MusicBlock block = activeMusicBlocks.remove(name);
        if (block != null) {
            block.stop();
            deleteMusicBlockFromConfig(name);
            return true;
        }
        return false;
    }

    private void saveMusicBlock(MusicBlock block) {
        String path = block.getName();
        musicBlockData.set(path + ".world", block.getLocation().getWorld().getName());
        musicBlockData.set(path + ".x", block.getLocation().getX());
        musicBlockData.set(path + ".y", block.getLocation().getY());
        musicBlockData.set(path + ".z", block.getLocation().getZ());
        musicBlockData.set(path + ".songs", block.getSongTitles());
        musicBlockData.set(path + ".range", block.getRange());
        musicBlockData.set(path + ".volume", block.getVolume());
    }

    private void deleteMusicBlockFromConfig(String name) {
        musicBlockData.remove(name);
    }

    public void loadMusicBlocks() {
        stopAll();
        Set<String> blockNames = musicBlockData.getFileData().toJsonObject().keySet();
        Bukkit.getLogger().info("Loading " + blockNames.size() + " music blocks...");
        for (String name : blockNames) {
            String path = name;
            String worldName = musicBlockData.getString(path + ".world");
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                Bukkit.getLogger().warning("Failed to load music block '" + name + "': World '" + worldName + "' not found.");
                continue;
            }

            double x = musicBlockData.getDouble(path + ".x");
            double y = musicBlockData.getDouble(path + ".y");
            double z = musicBlockData.getDouble(path + ".z");
            List<String> songTitles = musicBlockData.getStringList(path + ".songs");

            int range = musicBlockData.getOrDefault(path + ".range", DEFAULT_RANGE);
            int volume = musicBlockData.getOrDefault(path + ".volume", DEFAULT_VOLUME);

            if (songTitles.isEmpty()) {
                Bukkit.getLogger().warning("Music block '" + name + "' has no songs.");
            }

            Location location = new Location(world, x, y, z);
            MusicBlock block = new MusicBlock(plugin, musicManager, name, location, songTitles, range, volume);
            block.start();
            activeMusicBlocks.put(name, block);
        }
    }

    public void stopAll() {
        activeMusicBlocks.values().forEach(MusicBlock::stop);
        activeMusicBlocks.clear();
    }

    public Set<String> getActiveBlockNames() {
        return activeMusicBlocks.keySet();
    }

    public MusicBlock getMusicBlock(String name) {
        return activeMusicBlocks.get(name);
    }

    private void updateBlock(MusicBlock newBlock) {
        MusicBlock oldBlock = activeMusicBlocks.remove(newBlock.getName());
        if (oldBlock != null) {
            oldBlock.stop();
        }

        newBlock.start();
        activeMusicBlocks.put(newBlock.getName(), newBlock);

        saveMusicBlock(newBlock);
    }

    public boolean setBlockVolume(String name, int newVolume) {
        MusicBlock oldBlock = getMusicBlock(name);
        if (oldBlock == null) {
            return false;
        }
        MusicBlock newBlock = new MusicBlock(plugin, musicManager, name, oldBlock.getLocation(), oldBlock.getSongTitles(), oldBlock.getRange(), newVolume);
        updateBlock(newBlock);
        return true;
    }

    public boolean setBlockRange(String name, int newRange) {
        MusicBlock oldBlock = getMusicBlock(name);
        if (oldBlock == null) {
            return false;
        }
        MusicBlock newBlock = new MusicBlock(plugin, musicManager, name, oldBlock.getLocation(), oldBlock.getSongTitles(), newRange, oldBlock.getVolume());
        updateBlock(newBlock);
        return true;
    }

    public boolean addBlockSongs(String name, List<String> songsToAdd) {
        MusicBlock oldBlock = getMusicBlock(name);
        if (oldBlock == null) {
            return false;
        }
        List<String> newSongList = new ArrayList<>(oldBlock.getSongTitles());
        newSongList.addAll(songsToAdd);

        MusicBlock newBlock = new MusicBlock(plugin, musicManager, name, oldBlock.getLocation(), newSongList, oldBlock.getRange(), oldBlock.getVolume());
        updateBlock(newBlock);
        return true;
    }

    public boolean removeBlockSongs(String name, List<String> songsToRemove) {
        MusicBlock oldBlock = getMusicBlock(name);
        if (oldBlock == null) {
            return false;
        }
        List<String> newSongList = new ArrayList<>(oldBlock.getSongTitles());
        newSongList.removeAll(songsToRemove);

        MusicBlock newBlock = new MusicBlock(plugin, musicManager, name, oldBlock.getLocation(), newSongList, oldBlock.getRange(), oldBlock.getVolume());
        updateBlock(newBlock);
        return true;
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (MusicBlock block : activeMusicBlocks.values()) {
            block.removePlayer(player);
        }
    }
}