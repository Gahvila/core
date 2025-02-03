package net.gahvila.gahvilacore.nbsminecraft.player.emitter;

import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;

public class StaticSoundEmitter extends SoundEmitter {
    private final SoundLocation location;

    public StaticSoundEmitter(SoundLocation location) {
        this.location = location;
    }

    @Override
    public void playSound(AbstractPlatform platform, AudioListener listener, String sound, SoundCategory category, float volume, float pitch) {
        platform.playSound(listener, location, sound, category, volume, pitch);
    }
}
