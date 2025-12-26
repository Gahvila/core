package net.gahvila.gahvilacore.nbsminecraft.platform.bukkit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.platform.bukkit.utils.BukkitConverter;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.EntityReference;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BukkitPlatform extends AbstractPlatform {
    public static final BukkitPlatform INSTANCE = new BukkitPlatform();
    private final Cache<UUID, Entity> ENTITY_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.SECONDS)
        .build();

    @Override
    public void playSound(AudioListener listener, String sound, SoundCategory category, float volume, float pitch) {
        Player player = findPlayer(listener.uuid());
        if (player == null || !player.isValid()) {
            return;
        }

        playSound(player, player, sound, category, volume, pitch);
    }

    private static class RateLimit {
        long windowStartMs;
        int count;
    }

    private final Map<UUID, RateLimit> rateLimits = new ConcurrentHashMap<>();

    @Override
    public void playSound(AudioListener listener, EntityReference entityReference,
                          String sound, SoundCategory category,
                          float volume, float pitch) {
        Player player = findPlayer(listener.uuid());
        if (player == null || !player.isValid()) return;

        // Rate limiting: max 10 full-probability particles per second
        RateLimit rl = rateLimits.computeIfAbsent(listener.uuid(), k -> new RateLimit());
        long now = System.currentTimeMillis();

        if (now - rl.windowStartMs >= 1000) {
            rl.windowStartMs = now;
            rl.count = 0;
        }

        boolean shouldSpawn;
        if (rl.count < 25) {
            rl.count++;
            shouldSpawn = true;
        } else {
            // Over the limit: 1 in 5 chance
            shouldSpawn = ThreadLocalRandom.current().nextInt(5) == 0;
        }

        if (shouldSpawn) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Location loc = findPlayer(entityReference.uuid()).getLocation();

            double spread = 0.5 + volume * 0.5;  // Louder = more horizontal spread
            double height = 1.5 + pitch * 1.0;   // Higher pitch = higher vertical offset

            double x = loc.getX() + (random.nextDouble() - 0.5) * spread;
            double y = loc.getY() + height;
            double z = loc.getZ() + (random.nextDouble() - 0.5) * spread;

            player.spawnParticle(Particle.NOTE, x, y, z, 1);
        }

        playSound(player, findEntity(entityReference.uuid()), sound, category, volume, pitch);
    }

    private void playSound(Player player, Entity entity, String sound, SoundCategory category, float volume, float pitch) {
        org.bukkit.SoundCategory bukkitCategory = BukkitConverter.convert(category);

        player.playSound(entity, sound, bukkitCategory, volume, pitch);
    }

    @Override
    public void playSound(AudioListener listener, SoundLocation location, String sound, SoundCategory category, float volume, float pitch) {
        World world = Bukkit.getWorld(location.world());
        if (world == null) {
            return;
        }

        Player player = findPlayer(listener.uuid());
        if (player == null || !player.isValid()) {
            return;
        }

        Location bukkitLocation = BukkitConverter.convert(location);
        org.bukkit.SoundCategory bukkitCategory = BukkitConverter.convert(category);

        player.playSound(bukkitLocation, sound, bukkitCategory, volume, pitch);
    }

    private @Nullable Player findPlayer(UUID uuid) {
        Entity entity = ENTITY_CACHE.getIfPresent(uuid);
        if (!(entity instanceof Player player) || !player.isValid()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                ENTITY_CACHE.put(uuid, player);
            }

            return player;
        } else {
            return player;
        }
    }

    private @Nullable Entity findEntity(UUID uuid) {
        Entity entity = ENTITY_CACHE.getIfPresent(uuid);
        if (entity == null || !entity.isValid()) {
            entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                ENTITY_CACHE.put(uuid, entity);
            }
        }

        return entity;
    }

    @Override
    public void invalidateIfCached(AudioListener listener) {
        ENTITY_CACHE.invalidate(listener.uuid());
    }
}
