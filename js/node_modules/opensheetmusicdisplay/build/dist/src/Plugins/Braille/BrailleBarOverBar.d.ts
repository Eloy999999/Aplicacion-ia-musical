import { MusicSheet } from "../../MusicalScore/MusicSheet";
import { SourceMeasure } from "../../MusicalScore/VoiceData/SourceMeasure";
import { Slur } from "../../MusicalScore/VoiceData/Expressions/ContinuousExpressions/Slur";
import { KeyInstruction } from "../../MusicalScore/VoiceData/Instructions/KeyInstruction";
import { ClefEnum } from "../../MusicalScore/VoiceData/Instructions/ClefInstruction";
import { Fraction } from "../../Common/DataObjects/Fraction";
import { BrailleMeasureRenderer } from "./BrailleMeasureRenderer";
import { BrailleNoteDebugInfo } from "./BrailleNoteRenderer";
import { BrailleOutput, BrailleDebugEntry } from "./BrailleConverter";
/**
 * A single measure rendered for one staff, with its braille text and metadata.
 */
export interface RenderedMeasure {
    /** The braille string for this measure (notes, chords, rests, etc.) */
    braille: string;
    /** Cell length of the braille string (= braille.length since all chars are BMP) */
    cellLength: number;
    /** 1-based measure number from MusicXML */
    measureNumber: number;
    /** Debug entries for the elements in this measure */
    debugEntries: BrailleNoteDebugInfo[];
}
/**
 * Configuration for the bar-over-bar layout engine.
 */
export interface BarOverBarConfig {
    /** Maximum cells per braille line. Default: 40 (standard braille display width). */
    lineWidth: number;
    /** Number of staves to render (2 for piano RH+LH). */
    staffCount: number;
    /** Minimum gap (cells) before guide dots are used instead of spaces. Default: 6. */
    guideDotThreshold: number;
    /** Minimum number of guide dots to emit. If fewer would fit, use plain spaces. Default: 5. */
    guideDotMinCount: number;
}
/**
 * Bar-over-bar layout engine for keyboard/piano braille music.
 *
 * Implements BANA Music Braille Code 2015, Chapter 28 + Par. 29.3.
 * Produces vertically aligned parallels where RH and LH measures
 * appear on adjacent lines with aligned measure starts.
 *
 * This class handles layout only — note rendering is delegated to BrailleMeasureRenderer.
 */
export declare class BrailleBarOverBarLayout {
    private measureRenderer;
    constructor(measureRenderer: BrailleMeasureRenderer);
    /**
     * Format a multi-staff score in bar-over-bar layout.
     *
     * @param musicSheet The parsed MusicSheet
     * @param config Layout configuration
     * @param debugMode Whether to generate debug entries
     * @param facsimile Whether facsimile mode is active
     * @param computeSlurLengthsFn Function to compute slur lengths per staff
     * @returns BrailleOutput with formatted bar-over-bar text
     */
    format(musicSheet: MusicSheet, config: BarOverBarConfig, debugMode: boolean, facsimile: boolean, computeSlurLengthsFn: (measures: SourceMeasure[], staffIndex: number) => Map<Slur, number>): BrailleOutput;
    /**
     * Pre-render all measures for a single staff.
     * Resets the octave tracker before each measure (Par. 29.3(a):
     * first note of every measure in bar-over-bar must have an octave mark).
     */
    private preRenderMeasures;
    /**
     * Render the music heading: key + time signature centered on the line.
     * Par. 1.7: heading appears above the first parallel.
     */
    renderMusicHeading(initialKey: KeyInstruction | undefined, initialTime: Fraction | undefined, lineWidth: number): {
        braille: string;
        debugEntries: BrailleDebugEntry[];
    };
    /**
     * Group measures into parallels based on line width.
     * Greedy packing: add measures until the next one won't fit in both lines.
     *
     * Each measure's column width = max(cellLength across staves).
     * Measures within a parallel are separated by one space.
     */
    private groupIntoParallels;
    /**
     * Format a single parallel: measure number + hand sign + aligned measures for each staff.
     *
     * Par. 29.3(f)(g): One space after the longer part's last sign, then the next measure.
     * The shorter part's next measure starts directly above/below.
     */
    private formatParallel;
    /**
     * Convert a measure number to bare braille digits (NO number sign).
     * Par. 29.3(b): measure numbers at the margin use no numeric indicator,
     * in upper-cell digits (Example 29.3-1: measure 15 → ⠁⠑).
     *
     * @param n The measure number (0 for anacrusis, 1+ for regular measures)
     * @returns Braille digit string without number sign prefix
     */
    measureNumberToBareDigits(n: number): string;
    /**
     * Split a formatted staff line into run-over lines when it exceeds lineWidth (Par. 28.1.2).
     *
     * Splits occur only at blank-cell separators (between measures) — never mid-measure,
     * since breaking within a measure would corrupt braille semantics (octave marks,
     * interval signs, etc. are tightly coupled to adjacent notes).
     *
     * Run-over (continuation) lines are indented 2 cells beyond the alignment point
     * (where music starts on the primary line, i.e., right after the prefix).
     *
     * @param line The full formatted line (prefix + music content)
     * @param prefixLen The cell length of the prefix (music alignment point)
     * @param lineWidth Maximum cells allowed per line
     * @returns Array of lines. If input fits, returns [line]. Otherwise, primary line
     *          plus one or more continuation lines with run-over indent.
     */
    splitLineWithRunOver(line: string, prefixLen: number, lineWidth: number): string[];
    /**
     * Generate padding to fill a gap for vertical alignment.
     *
     * If the gap is large enough (≥ guideDotThreshold), fills with guide dots
     * (dot 3, minimum guideDotMinCount). Guide dots are separated from music
     * by one blank cell on each side (Par. 28.1.3).
     *
     * If the gap is too small for guide dots, fills with plain spaces.
     *
     * @param gap Number of cells to fill
     * @param config Layout configuration with guide dot thresholds
     * @returns Padding string of exactly `gap` cells
     */
    padWithGuideDots(gap: number, config: BarOverBarConfig): string;
    /**
     * Compute the maximum measure number digit width across all measures.
     * Used to vertically align measure numbers across parallels.
     */
    private getMaxMeasureNumberWidth;
    /**
     * Format a multi-instrument score in ensemble bar-over-bar layout.
     *
     * Unlike keyboard bar-over-bar (M8b), ensemble format:
     * - Uses instrument abbreviations as line prefixes (not hand signs)
     * - Places measure numbers in a free line above each parallel
     * - Has a variable number of lines per parallel (one per instrument staff)
     * - Reads all chord intervals upward (Par. 33.4.2)
     *
     * Music Braille Code 2015, Chapter 33.
     *
     * @param musicSheet The parsed MusicSheet
     * @param parts Instrument/part info (abbreviation, staff indices, clef defaults)
     * @param config Layout configuration
     * @param debugMode Whether to generate debug entries
     * @param facsimile Whether facsimile mode is active
     * @param computeSlurLengthsFn Function to compute slur lengths per staff
     * @returns BrailleOutput with formatted ensemble bar-over-bar text
     */
    /**
     * @param perPartKeys When provided, parts have different key signatures.
     *   The heading will show only time (no key), and each staff's state is
     *   pre-initialized with its own key to suppress per-measure rendering.
     *   Map: global staff index → KeyInstruction. (Par. 33.4.1)
     */
    formatEnsemble(musicSheet: MusicSheet, parts: EnsemblePartInfo[], config: BarOverBarConfig, debugMode: boolean, facsimile: boolean, computeSlurLengthsFn: (measures: SourceMeasure[], staffIndex: number) => Map<Slur, number>, perPartKeys?: Map<number, KeyInstruction>): BrailleOutput;
    /**
     * Format a single ensemble parallel: abbreviation + aligned measures for each part.
     * Similar to formatParallel() but uses instrument abbreviations instead of hand signs.
     * When activeStaves is provided, only those staves are included (condensed score, Par. 33.1).
     */
    private formatEnsembleParallel;
    /**
     * Check if a staff has only rests across a range of measures.
     * Used for condensed score (Par. 33.1): omit parts that are resting.
     */
    private isStaffRestOnly;
    /**
     * Get the global staff index for a given render index.
     * Maps from the flat allRendered[] index back to the original staff index.
     */
    /**
     * Render an instrument list table (Par. 33.2).
     * Two-column table: full instrument names + abbreviations.
     * Abbreviations start 2 cells beyond the longest name.
     * Guide dots (dot 5) fill gaps of 3+ cells between name and abbreviation column.
     *
     * @param instruments Array of { name, abbreviation } in braille literary characters
     * @returns Lines of the table, one per instrument
     */
    renderInstrumentTable(instruments: Array<{
        name: string;
        abbreviation: string;
    }>): {
        lines: string[];
        debugEntries: BrailleDebugEntry[];
    };
    private getStaffIndexForRenderIndex;
}
/**
 * Information about an ensemble part for bar-over-bar layout.
 */
export interface EnsemblePartInfo {
    /** Braille abbreviation for the instrument (already converted via textToBraille + dot 3 terminator) */
    brailleAbbreviation: string;
    /** Global staff indices for this part's staves (from Staff.idInMusicSheet) */
    staffIndices: number[];
    /** Default clef for this part (for chord interval direction — though ensemble always reads upward) */
    defaultClef: ClefEnum;
}
