package net.gahvila.gahvilacore.nbsminecraft.player.emitter;

import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.EntityReference;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import org.bukkit.Bukkit;

public class EntitySoundEmitter extends SoundEmitter {
    public final EntityReference entityReference;

    public EntitySoundEmitter(EntityReference entityReference) {
        this.entityReference = entityReference;
    }

    @Override
    public void playSound(AbstractPlatform platform, AudioListener listener, String sound, SoundCategory category, float volume, float pitch) {
        platform.playSound(listener, entityReference, sound, category, volume, pitch);
    }
}
