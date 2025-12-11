package net.gahvila.gahvilacore.nbsminecraft.player.emitter;


import net.gahvila.gahvilacore.nbsminecraft.platform.AbstractPlatform;
import net.gahvila.gahvilacore.nbsminecraft.utils.AudioListener;
import net.gahvila.gahvilacore.nbsminecraft.utils.SoundCategory;

public abstract class SoundEmitter {

    public abstract void playSound(AbstractPlatform platform, AudioListener listener, String sound, SoundCategory category, float volume, float pitch, float panning);}
