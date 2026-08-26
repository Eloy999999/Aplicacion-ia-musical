import { Fraction } from "../../Common/DataObjects/Fraction";
import { BrailleNoteDebugInfo } from "./BrailleNoteRenderer";
/**
 * Result of rendering a time signature to braille.
 */
export interface BrailleTimeSignatureResult {
    /** The braille string for the time signature */
    braille: string;
    /** Debug entries for each braille element */
    debugEntries: BrailleNoteDebugInfo[];
}
/**
 * Render a time signature to braille music notation.
 *
 * Per Music Braille Code 2015, Table 7 / Par. 7.1:
 * number sign + numerator in UPPER-cell digits + denominator in LOWER-cell digits.
 * - 4/4 → ⠼⠙⠲ (number sign + upper 4 + lower 4)
 * - 3/4 → ⠼⠉⠲ (number sign + upper 3 + lower 4)
 * - 6/8 → ⠼⠋⠦ (number sign + upper 6 + lower 8)
 * - 12/8 → ⠼⠁⠃⠦ (number sign + upper 1,2 + lower 8)
 *
 * @param rhythm The time signature Fraction (Numerator/Denominator)
 * @returns BrailleTimeSignatureResult with braille string and debug info
 */
export declare function renderTimeSignature(rhythm: Fraction): BrailleTimeSignatureResult;
