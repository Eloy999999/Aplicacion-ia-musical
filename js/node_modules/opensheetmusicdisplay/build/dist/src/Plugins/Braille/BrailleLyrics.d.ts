import { SourceMeasure } from "../../MusicalScore/VoiceData/SourceMeasure";
import { VoiceEntry } from "../../MusicalScore/VoiceData/VoiceEntry";
import { LyricsEntry } from "../../MusicalScore/VoiceData/Lyrics/LyricsEntry";
import { BrailleNoteDebugInfo } from "./BrailleNoteRenderer";
/**
 * Result of lyrics extraction for a single measure.
 */
export interface LyricsMeasureResult {
    /** Word-line text for this measure (syllables joined per braille rules) */
    wordText: string;
    /** Cell length of wordText */
    cellLength: number;
    /** Debug entries for lyrics elements */
    debugEntries: BrailleNoteDebugInfo[];
    /** Whether a melisma (single syllable spanning multiple notes) is active at end of this measure */
    melismaActive: boolean;
}
/**
 * State tracked across measures during lyrics extraction.
 */
export interface LyricsState {
    /** Whether a melisma is active — a syllable extending across multiple notes (MusicXML `<extend/>`) */
    melismaActive: boolean;
}
/**
 * Check if a LyricsEntry is the last syllable in its word.
 * Returns true for single-syllable words (no Word reference) or
 * when the entry is the final syllable in Word.Syllables[].
 */
export declare function isLastSyllableInWord(entry: LyricsEntry): boolean;
/**
 * Extract lyrics for a single measure, building the word-line text.
 *
 * Iterates VoiceEntries in beat order (first voice only) and collects
 * syllables for the specified verse. Syllables of the same word are
 * concatenated without hyphens (Par. 35.1.1a). Spaces are added
 * between words.
 *
 * @param measure The SourceMeasure to extract lyrics from
 * @param staffIndex Which staff to extract from (0-based)
 * @param verseNumber The verse number to extract (e.g., "1")
 * @param state Persistent state for melisma tracking across measures
 * @returns LyricsMeasureResult with word-line text and debug info
 */
export declare function extractLyricsMeasure(measure: SourceMeasure, staffIndex: number, verseNumber: string, state: LyricsState): LyricsMeasureResult;
/**
 * Build a Set of VoiceEntries that are continuation notes in a melisma
 * (a single syllable sung across multiple notes, indicated by `<extend/>` in MusicXML).
 * Used to inject syllabic slur signs in the music line.
 *
 * @param measures All SourceMeasures in the score
 * @param staffIndex Which staff to analyze
 * @param verseNumber The verse number to check
 * @returns Set of VoiceEntries that need syllabic slur signs
 */
export declare function buildMelismaSlurSet(measures: SourceMeasure[], staffIndex: number, verseNumber: string): Set<VoiceEntry>;
/**
 * Collect all distinct verse numbers present in the score for a given staff.
 * Returns sorted array of verse number strings (e.g., ["1", "2", "3"]).
 *
 * @param measures All SourceMeasures in the score
 * @param staffIndex Which staff to scan
 * @returns Sorted array of verse number strings
 */
export declare function collectVerseNumbers(measures: SourceMeasure[], staffIndex: number): string[];
/**
 * Extract a full verse as continuous literary braille text.
 * Used for verses 2+ which appear after the music (Par. 35.7).
 *
 * Iterates all measures and collects syllables for the given verse,
 * joining them per braille rules (no hyphens within words, spaces between words).
 *
 * @param measures All SourceMeasures in the score
 * @param staffIndex Which staff to extract from
 * @param verseNumber The verse number to extract (e.g., "2")
 * @returns The full verse text as a braille literary string, prefixed with (N)
 */
export declare function extractFullVerse(measures: SourceMeasure[], staffIndex: number, verseNumber: string): {
    braille: string;
    debugEntries: BrailleNoteDebugInfo[];
};
