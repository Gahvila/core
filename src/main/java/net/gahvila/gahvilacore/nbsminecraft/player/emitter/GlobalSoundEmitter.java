package net.gahvila.gahvilacore.nbsminecraft.player.emitter;

import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GlobalSoundEmitter extends SoundEmitter {

    @Override
    public void playSound(AbstractPlatform platform, AudioListener listener, String sound, SoundCategory category, float volume, float pitch, float panning) {
        Player player = Bukkit.getPlayer(listener.uuid());
        if (player == null) return;

        double radius = 2.0;
        double angleRad = (panning / 100.0) * (Math.PI / 2.0);

        Location headLoc = player.getEyeLocation();
        Vector direction = headLoc.getDirection();
        direction.setY(0).normalize();

        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector forwardComponent = direction.multiply(Math.cos(angleRad));
        Vector sideComponent = right.multiply(Math.sin(angleRad));
        Vector soundOffset = forwardComponent.add(sideComponent).normalize().multiply(radius);

        Location soundLoc = headLoc.clone().add(soundOffset);

        player.playSound(soundLoc, sound, org.bukkit.SoundCategory.valueOf(category.name()), volume, pitch);
    }
}