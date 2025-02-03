package net.gahvila.gahvilacore.nbsminecraft.platform;


import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.EntityReference;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundLocation;

public abstract class AbstractPlatform {

    public abstract void playSound(AudioListener listener, String sound, SoundCategory category, float volume, float pitch);

    public abstract void playSound(AudioListener listener, EntityReference entityReference, String sound, SoundCategory category, float volume, float pitch);

    public abstract void playSound(AudioListener listener, SoundLocation location, String sound, SoundCategory category, float volume, float pitch);

    public void invalidateIfCached(AudioListener listener) {}
}
