import { Note } from "../../MusicalScore/VoiceData/Note";
import { ClefEnum } from "../../MusicalScore/VoiceData/Instructions/ClefInstruction";
import { VoiceEntry } from "../../MusicalScore/VoiceData/VoiceEntry";
import { OctaveShift } from "../../MusicalScore/VoiceData/Expressions/ContinuousExpressions/OctaveShift";
import { BrailleNoteResult } from "./BrailleNoteRenderer";
import { BrailleOctaveTracker } from "./BrailleOctaveTracker";
/**
 * Converts a chord (multiple simultaneous notes in a VoiceEntry) to braille.
 *
 * In braille music, chords are encoded as:
 * 1. One "written note" rendered as a full note character (accidental + octave + pitch/duration + dots)
 * 2. Remaining notes rendered as interval signs from the written note
 *
 * The written note depends on the clef:
 * - G clef (treble) / C clef: highest note, intervals read downward
 * - F clef (bass): lowest note, intervals read upward
 *
 * See Music Braille Code 2015, Chapter 5.
 */
export declare class BrailleChordRenderer {
    private noteRenderer;
    constructor();
    /**
     * Render a chord to braille.
     *
     * @param notes All notes in the chord (from VoiceEntry.Notes[])
     * @param clefType The current clef (determines interval direction)
     * @param octaveTracker Tracks octave context for the written note
     * @param voiceEntry Optional VoiceEntry for articulation/ornament data
     * @param activeOctaveShift Optional active ottava for facsimile pitch adjustment
     * @param forceUpward When true, always read intervals upward (ensemble mode, Par. 33.4.2)
     * @returns BrailleNoteResult with braille string and debug info
     */
    render(notes: Note[], clefType: ClefEnum, octaveTracker: BrailleOctaveTracker, voiceEntry?: VoiceEntry, activeOctaveShift?: OctaveShift, forceUpward?: boolean): BrailleNoteResult;
    /**
     * Sort notes by pitch height (lowest to highest).
     * Uses the original (untransposed) pitch for comparison.
     */
    private sortByPitch;
    /**
     * Get the written note (the one rendered as a full note character).
     * G/C clef: highest note. F clef: lowest note.
     */
    private getWrittenNote;
    /**
     * Get the interval notes in reading order (nearest to written note first).
     * G/C clef: next-highest → lowest. F clef: next-lowest → highest.
     */
    private getIntervalNotes;
}
