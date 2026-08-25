import { KeyInstruction } from "../../MusicalScore/VoiceData/Instructions/KeyInstruction";
import { BrailleNoteDebugInfo } from "./BrailleNoteRenderer";
/**
 * Result of rendering a key signature to braille.
 */
export interface BrailleKeySignatureResult {
    /** The braille string for the key signature (empty if C major / A minor) */
    braille: string;
    /** Debug entries for each braille element */
    debugEntries: BrailleNoteDebugInfo[];
}
/**
 * Render a key signature to braille music notation.
 *
 * Per Music Braille Code 2015, Table 6:
 * - 1–3 accidentals are written out as that many accidental signs:
 *   2 sharps → ⠩⠩, 3 flats → ⠣⠣⠣
 * - 4 or more use numeric form: number sign + upper-cell digit + accidental:
 *   4 sharps → ⠼⠙⠩, 5 flats → ⠼⠑⠣
 * - C major / A minor (key=0) → empty (no key signature)
 *
 * @param keyInstruction The KeyInstruction from the SourceMeasure
 * @returns BrailleKeySignatureResult with braille string and debug info
 */
export declare function renderKeySignature(keyInstruction: KeyInstruction): BrailleKeySignatureResult;
