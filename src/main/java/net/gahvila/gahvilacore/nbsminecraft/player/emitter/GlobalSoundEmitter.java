package net.gahvila.gahvilacore.nbsminecraft.player.emitter;

import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;

public class GlobalSoundEmitter extends SoundEmitter {

    @Override
    public void playSound(AbstractPlatform platform, AudioListener listener, String sound, SoundCategory category, float volume, float pitch) {
        platform.playSound(listener, sound, category, volume, pitch);
    }
}
