import { NoteEnum, AccidentalEnum } from "../../Common/DataObjects/Pitch";
import { NoteType } from "../../MusicalScore/VoiceData/NoteType";
import { RepetitionInstructionEnum } from "../../MusicalScore/VoiceData/Instructions/RepetitionInstruction";
import { ClefEnum } from "../../MusicalScore/VoiceData/Instructions/ClefInstruction";
import { OctaveEnum } from "../../MusicalScore/VoiceData/Expressions/ContinuousExpressions/OctaveShift";
/**
 * Duration categories in braille music.
 * Braille music uses the same characters for notes differing by a factor of 4.
 * Context (time signature, beat position) determines which actual duration is meant.
 */
export declare enum BrailleDurationGroup {
    /** Whole notes and 16th notes share characters */
    WholeOr16th = 0,
    /** Half notes and 32nd notes share characters */
    HalfOr32nd = 1,
    /** Quarter notes and 64th notes share characters */
    QuarterOr64th = 2,
    /** Eighth notes and 128th notes share characters */
    EighthOr128th = 3
}
export declare const DOT1: number;
export declare const DOT2: number;
export declare const DOT3: number;
export declare const DOT4: number;
export declare const DOT5: number;
export declare const DOT6: number;
/** Base Unicode code point for braille patterns (U+2800) */
export declare const BRAILLE_BASE: number;
/**
 * Convert a dot pattern (bitwise OR of DOT1..DOT6) to a braille Unicode character.
 */
export declare function dotsToChar(dots: number): string;
/** Duration dot patterns indexed by BrailleDurationGroup */
export declare const DURATION_DOTS: number[];
export declare const PITCH_DOTS: Map<NoteEnum, number>;
/**
 * Get the braille character for a note with a given pitch and duration group.
 */
export declare function getNoteChar(pitch: NoteEnum, durationGroup: BrailleDurationGroup): string;
/** Rest characters indexed by BrailleDurationGroup */
export declare const REST_CHARS: Map<BrailleDurationGroup, string>;
/**
 * Get the braille character for a rest with a given duration group.
 */
export declare function getRestChar(durationGroup: BrailleDurationGroup): string;
export declare const OCTAVE_MARKS: Map<number, string>;
export declare const BRAILLE_AUGMENTATION_DOT: string;
/** In braille music, measure separation is represented by a space */
export declare const BRAILLE_MEASURE_SEPARATOR: string;
export declare const BRAILLE_FINAL_DOUBLE_BAR: string;
export declare const BRAILLE_SECTIONAL_DOUBLE_BAR: string;
/** Full-measure in-accord: separates complete voice measures. ⠣⠜ (dots 1,2,6 + dots 3,4,5) */
export declare const BRAILLE_FULL_MEASURE_IN_ACCORD: string;
/** Part-measure in-accord: separates voices within a measure section. ⠐⠂ (dots 5 + dots 2) */
export declare const BRAILLE_PART_MEASURE_IN_ACCORD: string;
/** Measure division: divides a measure into sections for part-measure in-accords. ⠨⠅ (dots 4,6 + dots 1,3) */
export declare const BRAILLE_MEASURE_DIVISION: string;
export declare const BRAILLE_INTERVAL_SIGNS: Map<number, string>;
/**
 * Get the braille character for a diatonic interval sign (2nd through octave).
 * For compound intervals (>8), the interval is reduced: 9th→2nd, 10th→3rd, etc.
 */
export declare function getIntervalChar(interval: number): string;
/**
 * Get a human-readable name for an interval (for debug output).
 */
export declare function getIntervalName(interval: number): string;
export declare const BRAILLE_SHARP: string;
export declare const BRAILLE_FLAT: string;
export declare const BRAILLE_NATURAL: string;
export declare const BRAILLE_DOUBLE_SHARP: string;
export declare const BRAILLE_DOUBLE_FLAT: string;
export declare const BRAILLE_NUMBER_SIGN: string;
/**
 * Lower-cell digits ("dropped numbers", dots 2,3,5,6 region).
 * Braille music uses these for: time signature DENOMINATORS (Table 7),
 * volta ending numbers (Table 17), and other lower-cell number contexts.
 * Not for counts/numerators after the number sign — those use BRAILLE_UPPER_DIGITS.
 */
export declare const BRAILLE_DIGITS: string[];
/**
 * Upper-cell digits (ordinary literary braille numbers — same dot patterns as
 * letters A–J). Used after the number sign ⠼ for: literary text numbers,
 * key signature counts of 4+ (Table 6: ⠼⠙⠩ = 4 sharps), time signature
 * NUMERATORS (Table 7: 4/4 = ⠼⠙⠲), ottava numerals (Par. 3.3.1), and
 * bar-over-bar/ensemble measure numbers (Par. 29.3(b), Example 29.3-1).
 */
export declare const BRAILLE_UPPER_DIGITS: string[];
/**
 * Convert a non-negative integer to upper-cell braille digits (without number sign).
 * Handles multi-digit numbers (e.g., 12 → ⠁⠃).
 */
export declare function numberToUpperDigits(n: number): string;
/**
 * Convert a non-negative integer to lower-cell braille digits (without number sign).
 * Handles multi-digit numbers (e.g., 12 → ⠂⠆).
 */
export declare function numberToLowerDigits(n: number): string;
/**
 * Get the braille string for an accidental.
 * Returns empty string for NONE or unsupported accidentals.
 */
export declare function getAccidentalChar(accidental: AccidentalEnum): string;
/**
 * Get a human-readable name for an accidental (for debug output).
 */
export declare function getAccidentalName(accidental: AccidentalEnum): string;
/** Staccato: dot above/below note. ⠦ dots 2,3,6 */
export declare const BRAILLE_STACCATO: string;
/** Staccatissimo: pear-shaped dot. ⠠⠦ dots 6 + dots 2,3,6 */
export declare const BRAILLE_STACCATISSIMO: string;
/** Mezzo-staccato (detached legato): dot and short line. ⠐⠦ dots 5 + dots 2,3,6 */
export declare const BRAILLE_MEZZO_STACCATO: string;
/** Tenuto (agogic accent): short line. ⠸⠦ dots 4,5,6 + dots 2,3,6 */
export declare const BRAILLE_TENUTO: string;
/** Accent: thin converging wedge. ⠨⠦ dots 4,6 + dots 2,3,6 */
export declare const BRAILLE_ACCENT: string;
/** Strong accent / marcato: thick inverted V. ⠰⠦ dots 5,6 + dots 2,3,6 */
export declare const BRAILLE_MARCATO: string;
/** Fermata over/under a note. ⠣⠇ dots 1,2,6 + dots 1,2,3 */
export declare const BRAILLE_FERMATA: string;
/** Trill. ⠖ dots 2,3,5 */
export declare const BRAILLE_TRILL: string;
/** Turn between notes. ⠲ dots 2,5,6 */
export declare const BRAILLE_TURN: string;
/** Turn above/below a note. ⠠⠲ dots 6 + dots 2,5,6 */
export declare const BRAILLE_TURN_ON_NOTE: string;
/** Inverted turn between notes. ⠲⠇ dots 2,5,6 + dots 1,2,3 */
export declare const BRAILLE_INVERTED_TURN: string;
/** Inverted turn above/below a note. ⠠⠲⠇ dots 6 + dots 2,5,6 + dots 1,2,3 */
export declare const BRAILLE_INVERTED_TURN_ON_NOTE: string;
/** Upper mordent (short trill). ⠐⠖ dots 5 + dots 2,3,5 */
export declare const BRAILLE_MORDENT: string;
/** Lower mordent (inverted mordent). ⠐⠖⠇ dots 5 + dots 2,3,5 + dots 1,2,3 */
export declare const BRAILLE_INVERTED_MORDENT: string;
/**
 * Word sign: signals that what follows is literary text (letters/words), not music notation.
 * In music braille, this precedes dynamics (e.g. >f, >pp), tempo markings, and other
 * text-based expressions. See New International Manual 10-2, Music Braille Code Par. 22.3.
 * ⠜ dots 3,4,5
 */
export declare const BRAILLE_WORD_SIGN: string;
/** Abbreviation period (dot 3). Used in cresc., dim., etc. */
export declare const BRAILLE_ABBREV_PERIOD: string;
/** Literary opening parenthesis. ⠷ dots 1,2,3,5,6. Used in verse numbering (Par. 35.7). */
export declare const BRAILLE_OPEN_PAREN: string;
/** Literary closing parenthesis. ⠾ dots 2,3,4,5,6. Used in verse numbering (Par. 35.7). */
export declare const BRAILLE_CLOSE_PAREN: string;
/** Crescendo hairpin start. ⠜⠉ word sign + c */
export declare const BRAILLE_CRESC_HAIRPIN: string;
/** Diminuendo hairpin start. ⠜⠙ word sign + d */
export declare const BRAILLE_DIM_HAIRPIN: string;
/**
 * Get the braille string for a dynamic marking.
 * Returns word sign followed by the appropriate letter sequence.
 */
export declare function getDynamicBraille(dynamicName: string): string;
/** Tie between single notes. ⠈⠉ dots 4 + dots 1,4 */
export declare const BRAILLE_SINGLE_TIE: string;
/** Tie between chords (two or more ties). ⠨⠉ dots 4,6 + dots 1,4 */
export declare const BRAILLE_CHORD_TIE: string;
/** Short slur sign. ⠉ dots 1,4 */
export declare const BRAILLE_SLUR: string;
/** Syllabic slur sign for lyrics melisma. ⠉ dots 1,4. Same glyph as BRAILLE_SLUR (Par. 35.2). */
export declare const BRAILLE_SYLLABIC_SLUR: string;
/** Bracket slur opening sign. ⠰⠃ dots 5,6 + dots 1,2 */
export declare const BRAILLE_BRACKET_SLUR_OPEN: string;
/** Bracket slur closing sign. ⠘⠆ dots 4,5 + dots 2,3 */
export declare const BRAILLE_BRACKET_SLUR_CLOSE: string;
/** Forward repeat (double bar followed by dots). ⠣⠶ dots 1,2,6 + dots 2,3,5,6 */
export declare const BRAILLE_FORWARD_REPEAT: string;
/** Backward repeat (double bar preceded by dots). ⠣⠆ dots 1,2,6 + dots 2,3 */
export declare const BRAILLE_BACKWARD_REPEAT: string;
/**
 * Get the braille string for a volta (alternate ending) sign.
 * Uses number sign + digit for the ending number.
 * @param endingNumber The ending number (1, 2, 3, etc.)
 * @returns Braille string for the volta sign
 */
export declare function getVoltaBraille(endingNumber: number): string;
/** Segno sign. ⠬ dots 3,4,6 */
export declare const BRAILLE_SEGNO: string;
/** Coda sign. ⠬⠇ dots 3,4,6 + dots 1,2,3 */
export declare const BRAILLE_CODA: string;
/** Blank braille cell (U+2800). Used as word separator and for bar-over-bar alignment padding. */
export declare const BRAILLE_BLANK_CELL: string;
/**
 * Get the braille string for a navigation/repeat direction instruction.
 * Returns empty string for unsupported types.
 */
export declare function getNavigationBraille(type: RepetitionInstructionEnum): string;
/**
 * Get a human-readable name for a navigation instruction (for debug output).
 */
export declare function getNavigationName(type: RepetitionInstructionEnum): string;
/** G clef (treble). ⠜⠌⠇ word sign + G-id + suffix */
export declare const BRAILLE_CLEF_G: string;
/** F clef (bass). ⠜⠼⠇ word sign + F-id + suffix */
export declare const BRAILLE_CLEF_F: string;
/** C clef (alto, 3rd line). ⠜⠬⠇ word sign + C-id + suffix */
export declare const BRAILLE_CLEF_C: string;
/** C clef on 4th line (tenor). ⠜⠬⠐⠇ word sign + C-id + line-4 indicator + suffix */
export declare const BRAILLE_CLEF_TENOR: string;
/**
 * Get the braille string for a clef type.
 * Returns the standard clef sign for facsimile mode.
 * @param clefType The ClefEnum value
 * @param line Optional clef line (for distinguishing alto vs tenor C clef)
 */
export declare function getClefBraille(clefType: ClefEnum, line?: number): string;
/**
 * Get a human-readable name for a clef type (for debug output).
 */
export declare function getClefName(clefType: ClefEnum, line?: number): string;
/** Music hyphen: dot 5. Used when a measure continues across a braille line break. Par. 1.11. */
export declare const BRAILLE_MUSIC_HYPHEN: string;
/** Print page indicator: dots 5 + dots 2,5. Precedes page number in facsimile. Par. 1.5. */
export declare const BRAILLE_PAGE_INDICATOR: string;
/** Braille line break character for facsimile output. */
export declare const BRAILLE_LINE_BREAK: string;
/** Right hand sign. ⠨⠜ dots 4,6 + dots 3,4,5. Par. 29.2. */
export declare const BRAILLE_HAND_RIGHT: string;
/** Left hand sign. ⠸⠜ dots 4,5,6 + dots 3,4,5. Par. 29.2. */
export declare const BRAILLE_HAND_LEFT: string;
/**
 * Check whether a braille string's first character contains dot 1, 2, or 3.
 * Used to determine if a dot-3 separator is needed after hand signs and clef signs (Par. 29.2, 4.2).
 */
export declare function firstCharHasLowerDots(braille: string): boolean;
/** 8va start: ⠜⠼⠓⠧⠁⠄⠄ word_sign + #8 + va + period + period. Par. 3.3.1 */
export declare const BRAILLE_OTTAVA_8VA: string;
/** 8vb start: ⠜⠼⠓⠧⠃⠄⠄ word_sign + #8 + vb + period + period. Par. 3.3.1 */
export declare const BRAILLE_OTTAVA_8VB: string;
/** 15ma start: ⠜⠼⠁⠑⠍⠁⠄⠄ word_sign + #15 + ma + period + period. Par. 3.3.1 */
export declare const BRAILLE_OTTAVA_15MA: string;
/** 15mb start: ⠜⠼⠁⠑⠍⠃⠄⠄ word_sign + #15 + mb + period + period. Par. 3.3.1 */
export declare const BRAILLE_OTTAVA_15MB: string;
/** Ottava end marker: ⠜⠄ word_sign + period. Placed after the last note of the ottava passage. */
export declare const BRAILLE_OTTAVA_END: string;
/**
 * Get the braille start marker for an ottava type.
 * Returns empty string for OctaveEnum.NONE.
 */
export declare function getOttavaBraille(type: OctaveEnum): string;
/**
 * Get a human-readable name for an ottava type (for debug output).
 */
export declare function getOttavaName(type: OctaveEnum): string;
/**
 * Maps a NoteType enum value to its BrailleDurationGroup.
 * Returns undefined for types that don't map to a standard braille duration.
 */
export declare function noteTypeToDurationGroup(noteType: NoteType): BrailleDurationGroup | undefined;
/**
 * Infer a BrailleDurationGroup from a Fraction duration (fallback when NoteType is UNDEFINED).
 * Uses the fraction's real value (fraction of a whole note).
 */
export declare function fractionToDurationGroup(realValue: number): BrailleDurationGroup;
/**
 * Convert a text string to braille literary characters (Grade 1 alphabet).
 * Used for instrument abbreviations in ensemble scores.
 *
 * - Letters (a-z, A-Z) are converted to braille lowercase (case-insensitive)
 * - Digits (0-9) are converted to braille digits with number sign prefix
 * - Spaces become braille blank cells
 * - Periods (.) become dot 3 (abbreviation period)
 * - Other characters are skipped
 *
 * @param text The text string to convert
 * @returns Braille literary string
 */
export declare function textToBraille(text: string): string;
