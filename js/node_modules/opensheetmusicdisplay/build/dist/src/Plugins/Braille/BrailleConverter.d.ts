import { MusicSheet } from "../../MusicalScore/MusicSheet";
import { SourceMeasure } from "../../MusicalScore/VoiceData/SourceMeasure";
import { Slur } from "../../MusicalScore/VoiceData/Expressions/ContinuousExpressions/Slur";
import { GraphicalMusicSheet } from "../../MusicalScore/Graphical/GraphicalMusicSheet";
/**
 * Debug information for a braille character or element.
 * Used in translation/debug mode to show what each braille symbol means.
 */
export interface BrailleDebugEntry {
    /** The braille character(s) */
    braille: string;
    /** Human-readable description, e.g. "octave 4", "quarter note C4" */
    meaning: string;
    /** Source measure number (1-based) */
    measureNumber: number;
}
/**
 * Result of a complete braille conversion.
 */
export interface BrailleOutput {
    /** The complete braille string for the entire score/part */
    text: string;
    /** Debug/translation information, one entry per braille element */
    debugEntries: BrailleDebugEntry[];
}
/**
 * Braille output format.
 * - "nonfacsimile": Standard braille music (default). Omits print-layout signs
 *   (clefs, ottavas, page numbers). Output is a continuous stream of measures.
 * - "facsimile": Mirrors visual score layout. Includes clef signs, ottava brackets,
 *   and other print-presentation signs. Line breaks match print systems.
 *   Requires GraphicalMusicSheet (after render()). See Music Braille Code 2015 §1.1.
 */
export type BrailleFormat = "nonfacsimile" | "facsimile";
/**
 * Options for braille conversion.
 */
export interface BrailleOptions {
    /** Which staff to render (0-based). Default: 0. Ignored when multiStaff is true. */
    staffIndex?: number;
    /** Whether to generate debug/translation info. Default: true */
    debugMode?: boolean;
    /** Output format. Default: "nonfacsimile" */
    format?: BrailleFormat;
    /**
     * The rendered graphical sheet. Required for facsimile mode.
     * Available from `osmd.GraphicSheet` after calling `render()`.
     * Provides system layout information for line-break placement.
     */
    graphicSheet?: GraphicalMusicSheet;
    /**
     * When true, render all staves (e.g. both hands for piano) with hand sign prefixes.
     * Each staff is rendered on its own line, separated by newlines.
     * RH (staff 0) gets right-hand sign, LH (staff 1) gets left-hand sign.
     * Key/time signatures are rendered only on the first (RH) line.
     *
     * Default: auto-detected. If staffIndex is not explicitly set and the score has
     * multiple staves, multi-staff mode is enabled automatically. Set to false to
     * force single-staff output (renders staffIndex only).
     */
    multiStaff?: boolean;
    /**
     * When true (and multiStaff is active), use bar-over-bar format instead of
     * sequential multi-staff layout. Bar-over-bar vertically aligns measures
     * between staves, with measure numbers at the left margin and a music heading
     * (key/time) centered above the first parallel. BANA standard format for
     * keyboard music (Music Braille Code 2015, Chapter 28 + Par. 29.3).
     *
     * Default: false (uses sequential M8a format).
     */
    barOverBar?: boolean;
    /**
     * Maximum cells per braille line in bar-over-bar mode.
     * Default: 40 (standard braille display width).
     */
    lineWidth?: number;
    /**
     * When true, render as an ensemble score with instrument abbreviations
     * and all chord intervals reading upward (Music Braille Code 2015, Chapter 33).
     * Each instrument gets its own line in each parallel, with abbreviation prefix.
     * Measure numbers appear in a free line above each parallel.
     *
     * Must be set explicitly — not auto-detected, since MusicXML often splits
     * keyboard instruments into separate parts which would be falsely detected.
     *
     * Default: false.
     */
    ensemble?: boolean;
    /**
     * When true, render with lyrics in word-line + music-line parallel format
     * (Music Braille Code 2015, Section 35). Word line (literary braille) at
     * cell 1, music line indented 2 cells.
     *
     * Must be set explicitly — not auto-detected, since many instrumental
     * scores carry stray lyric metadata that would falsely trigger it.
     *
     * Default: false.
     */
    lyrics?: boolean;
}
/**
 * Main orchestrator that converts a MusicSheet to braille music notation.
 * This is the primary public API for the braille module.
 *
 * Usage:
 * ```typescript
 * const converter = new BrailleConverter();
 * const output = converter.convert(osmd.Sheet);
 * console.log(output.text); // braille string
 * ```
 */
export declare class BrailleConverter {
    private measureRenderer;
    constructor();
    /**
     * Convert a MusicSheet to braille music notation.
     *
     * @param musicSheet The parsed MusicSheet from OSMD (after load())
     * @param options Conversion options (staff index, debug mode)
     * @returns BrailleOutput with text and optional debug info
     */
    convert(musicSheet: MusicSheet, options?: BrailleOptions): BrailleOutput;
    /**
     * Nonfacsimile conversion: linear iteration over all SourceMeasures.
     * Measures are joined with barline separators (space characters).
     */
    private convertNonfacsimile;
    /**
     * Multi-staff conversion: renders all staves with hand sign prefixes (M8a).
     *
     * For keyboard music (piano), this produces two lines:
     *   .>[RH measures...]\n
     *   _>[LH measures...]
     *
     * Each staff is rendered independently with its own BrailleState.
     * Key/time signatures are rendered only on the first staff (RH);
     * subsequent staves have their state pre-initialized to suppress
     * duplicate signatures.
     *
     * Hand signs (Par. 29.2):
     * - Staff 0 = right hand sign (⠨⠜)
     * - Staff 1 = left hand sign (⠸⠜)
     * - Additional staves (rare) omit hand sign but still get separate lines
     * - Octave mark is mandatory on the first note after a hand sign
     * - Dot-3 separator is added if the next sign contains dot 1, 2, or 3
     */
    private convertMultiStaff;
    /**
     * Single-staff conversion with hand sign prefix.
     * Used when multiStaff is true but only one staff exists.
     */
    private convertSingleStaffWithHandSign;
    /**
     * Bar-over-bar conversion: vertically aligned parallels (M8b).
     *
     * Produces BANA-standard bar-over-bar format where RH and LH measures
     * appear on adjacent lines with aligned measure starts. Each "parallel"
     * is a group of measures that fit within the line width.
     *
     * Format:
     *   [heading: key+time centered]
     *   [measure#] [RH hand sign][RH measures...]
     *   [measure#] [LH hand sign][LH measures...]
     */
    private convertBarOverBar;
    /**
     * Ensemble conversion: multi-instrument bar-over-bar (M8c).
     *
     * Produces BANA-standard ensemble format where each instrument gets its own
     * line in each parallel, prefixed by its abbreviation. Measure numbers appear
     * in a free line above each parallel. All chord intervals read upward.
     */
    private convertEnsemble;
    /**
     * Lyrics conversion: word-line + music-line parallel format (M9).
     *
     * Produces parallels where each group consists of:
     *   [word line — literary braille at cell 1]
     *   [music line — indented 2 cells]
     *
     * Syllables are paired 1:1 with notes. Print hyphens are omitted;
     * syllables of the same word are concatenated. Melisma notes
     * get syllabic slur signs in the music line.
     */
    private convertWithLyrics;
    /**
     * Facsimile conversion: iterate by visual system layout.
     *
     * Traverses GraphicalMusicSheet pages → systems → staff lines to render
     * measures in the same grouping as the visual score. Each system becomes
     * one braille line, separated by BRAILLE_LINE_BREAK.
     *
     * Within each system, measures are joined with barline separators just
     * like nonfacsimile. The octave tracker resets at each system start
     * (the first note of a new line always needs an octave mark).
     */
    private convertFacsimile;
    /**
     * Pre-compute the number of notes in each slur across the entire score.
     * Used to decide between short slurs (≤4 notes → per-note signs) and
     * bracket slurs (>4 notes → open/close brackets). Par. 13.3.
     */
    /**
     * Render clef sign for facsimile mode using GraphicalMeasure.InitiallyActiveClef.
     * This is the authoritative clef for each measure in the graphical layout.
     * Renders on the first measure or when the clef changes from the previous measure.
     * Also updates state.currentClef for chord interval direction.
     */
    private renderFacsimileClef;
    /**
     * Pre-compute the number of notes in each slur across the entire score.
     * Used to decide between short slurs (≤4 notes → per-note signs) and
     * bracket slurs (>4 notes → open/close brackets). Par. 13.3.
     */
    static computeSlurLengths(measures: SourceMeasure[], staffIndex: number): Map<Slur, number>;
}
