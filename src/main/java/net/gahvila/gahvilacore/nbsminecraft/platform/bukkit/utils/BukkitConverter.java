package net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.utils;

import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;

public class BukkitConverter {

    public static Location convert(SoundLocation location) {
        return new Location(Bukkit.getWorld(location.world()), location.x(), location.y(), location.z());
    }

    public static org.bukkit.SoundCategory convert(net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory category) {
        return switch (category) {
            case MASTER -> SoundCategory.MASTER;
            case MUSIC -> SoundCategory.MUSIC;
            case RECORDS -> SoundCategory.RECORDS;
            case WEATHER -> SoundCategory.WEATHER;
            case BLOCKS -> SoundCategory.BLOCKS;
            case HOSTILE -> SoundCategory.HOSTILE;
            case NEUTRAL -> SoundCategory.NEUTRAL;
            case PLAYERS -> SoundCategory.PLAYERS;
            case AMBIENT -> SoundCategory.AMBIENT;
            case VOICE -> SoundCategory.VOICE;
        };
    }
}
