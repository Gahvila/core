/*
 * This package uses methods adapted from koca2000's NoteBlockAPI
 * Link: https://github.com/koca2000/NoteBlockAPI
 */

package net.gahvila.gahvilacore.nbsminecraft.utils;

import cz.koca2000.nbs4j.Note;

public class PitchUtils {

    /**
     * Get pitch within the note's octave
     * @param note note
     * @return pitch within octave
     */
    public static float getPitchInOctave(Note note) {
        int key = note.getKey();
        float pitch = note.getPitch() / 100F;

        // Apply pitch to key
        key += (int) (pitch / 100);
        pitch %= 100;

        key -= 33;

        return (float) Math.pow(2, ((key * 100 + pitch) - 1200d) / 1200d);
    }

}
