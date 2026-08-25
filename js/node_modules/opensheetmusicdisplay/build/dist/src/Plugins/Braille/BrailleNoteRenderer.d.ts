import { Note } from "../../MusicalScore/VoiceData/Note";
import { Pitch } from "../../Common/DataObjects/Pitch";
import { VoiceEntry } from "../../MusicalScore/VoiceData/VoiceEntry";
import { BrailleOctaveTracker } from "./BrailleOctaveTracker";
/**
 * Debug information for a single rendered braille element.
 */
export interface BrailleNoteDebugInfo {
    /** The braille character(s) produced */
    braille: string;
    /** Human-readable meaning, e.g. "octave 4", "quarter note C", "quarter rest" */
    meaning: string;
}
/**
 * Result of rendering a single note to braille.
 */
export interface BrailleNoteResult {
    /** The complete braille string for this note (octave mark + note char + dots) */
    braille: string;
    /** Debug entries for each braille element in the result */
    debugEntries: BrailleNoteDebugInfo[];
}
/**
 * Converts a single OSMD Note to its braille representation.
 * Handles pitch, duration, octave marks, rests, and augmentation dots.
 */
export declare class BrailleNoteRenderer {
    /**
     * Render a single note to braille.
     *
     * Per Music Braille Code 2015, the ordering around a note is:
     *   [articulations] [ornaments] [accidental] [octave mark] [note char] [aug dots] [fermata]
     *
     * Articulations and ornaments precede accidentals (Par. 22.1, 16.3).
     * Fermata follows the note (Par. 22.2).
     *
     * @param note The OSMD Note to convert
     * @param octaveTracker Tracks octave context for octave mark decisions
     * @param voiceEntry Optional VoiceEntry for articulation/ornament data
     * @param pitchOverride Optional pitch override for facsimile ottava (staff pitch instead of sounding)
     * @returns BrailleNoteResult with braille string and debug info
     */
    render(note: Note, octaveTracker: BrailleOctaveTracker, voiceEntry?: VoiceEntry, pitchOverride?: Pitch): BrailleNoteResult;
    /**
     * Determine the BrailleDurationGroup for a note.
     * Uses NoteTypeXml if available, falls back to computing from the length Fraction.
     */
    private getDurationGroup;
    /**
     * Get a human-readable duration name for debug output.
     */
    private getDurationName;
}
