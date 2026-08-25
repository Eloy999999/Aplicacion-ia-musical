import { Pitch, NoteEnum } from "../../Common/DataObjects/Pitch";
/**
 * Tracks the current octave context and determines when octave marks
 * are needed in braille music output.
 *
 * Rules from Music Braille Code 2015, Par. 3.2:
 * - 3.2.1: Octave mark REQUIRED for first note of a braille line, and after numeric indicators
 * - 3.2.2(a): NOT marked if interval from previous note is less than a 4th (unison, 2nd, 3rd)
 * - 3.2.2(b): ALWAYS marked if interval is greater than a 5th (6th or more)
 * - 3.2.2(c): For interval of a 4th or 5th: ONLY marked if the note is in a different octave
 */
export declare class BrailleOctaveTracker {
    private previousPitch;
    private isFirstNote;
    /**
     * Determine if an octave mark is needed for the given pitch,
     * and return the appropriate braille octave mark string.
     * Returns empty string if no mark is needed.
     *
     * Also updates internal state to track the current pitch for future comparisons.
     *
     * @param pitch The pitch of the current note
     * @returns The braille octave mark string, or empty string if not needed
     */
    getOctaveMark(pitch: Pitch): string;
    /**
     * Reset state (e.g., at start of new line, new section, or new piece).
     * After reset, the next note will receive an octave mark.
     */
    reset(): void;
    /**
     * Calculate the diatonic interval (in scale steps) between two pitches.
     * A unison = 1, a second = 2, a third = 3, a fourth = 4, etc.
     * This counts letter names, not semitones.
     *
     * @param from The starting pitch
     * @param to The target pitch
     * @returns The diatonic interval (1 = unison, 2 = second, etc.)
     */
    static calculateDiatonicInterval(from: Pitch, to: Pitch): number;
    /**
     * Convert a NoteEnum to a diatonic index (0-6).
     * C=0, D=1, E=2, F=3, G=4, A=5, B=6
     */
    static noteEnumToDiatonicIndex(note: NoteEnum): number;
}
