package net.gahvila.gahvilacore.nbsminecraft.player.emitter;

import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GlobalSoundEmitter extends SoundEmitter {

    @Override
    public void playSound(AbstractPlatform platform, AudioListener listener, String sound, SoundCategory category, float volume, float pitch, float panning) {
        Player player = Bukkit.getPlayer(listener.uuid());
        if (player == null) return;

        double radius = 2.0;

        Location loc = player.getEyeLocation();
        double finalYawRad = Math.toRadians(loc.getYaw() + (panning * 0.9));
        loc.add(-Math.sin(finalYawRad) * radius, 0, Math.cos(finalYawRad) * radius);

        player.playSound(loc, sound, org.bukkit.SoundCategory.valueOf(category.name()), volume, pitch);
    }
}