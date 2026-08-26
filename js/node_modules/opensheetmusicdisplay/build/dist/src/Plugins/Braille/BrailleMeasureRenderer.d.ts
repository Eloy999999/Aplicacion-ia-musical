import { Slur } from "../../MusicalScore/VoiceData/Expressions/ContinuousExpressions/Slur";
import { OctaveShift } from "../../MusicalScore/VoiceData/Expressions/ContinuousExpressions/OctaveShift";
import { SourceMeasure } from "../../MusicalScore/VoiceData/SourceMeasure";
import { VoiceEntry } from "../../MusicalScore/VoiceData/VoiceEntry";
import { KeyInstruction } from "../../MusicalScore/VoiceData/Instructions/KeyInstruction";
import { ClefEnum } from "../../MusicalScore/VoiceData/Instructions/ClefInstruction";
import { Fraction } from "../../Common/DataObjects/Fraction";
import { BrailleNoteDebugInfo } from "./BrailleNoteRenderer";
import { BrailleOctaveTracker } from "./BrailleOctaveTracker";
/**
 * State passed between measures during braille conversion.
 * Persists across measure boundaries to maintain context.
 */
export interface BrailleState {
    /** Tracks octave context for octave mark decisions */
    octaveTracker: BrailleOctaveTracker;
    /** Current key signature (undefined = not yet encountered) */
    currentKey?: KeyInstruction;
    /** Current time signature (undefined = not yet encountered) */
    currentRhythm?: Fraction;
    /** Whether output is facsimile (mirrors print layout) or nonfacsimile (standard braille). */
    facsimile: boolean;
    /** Current clef type for chord interval direction. Default: G (treble). */
    currentClef: ClefEnum;
    /** Whether the previous measure used in-accord (forces octave mark on next measure). */
    hadInAccord: boolean;
    /** Active slurs being tracked across voice entries (for emitting slur signs on intermediate notes). */
    activeSlurs: Set<Slur>;
    /** Pre-computed slur lengths (number of notes in each slur). Used to choose short vs bracket slurs. */
    slurLengths: Map<Slur, number>;
    /**
     * Active ottava bracket (8va/8vb/15ma/15mb) affecting the current passage.
     * When set in facsimile mode, note pitches are adjusted to staff (visual) pitch.
     * Set when an OctaveShift starts, cleared when it ends.
     */
    activeOctaveShift?: OctaveShift;
    /**
     * When true, chord intervals are always read upward regardless of clef.
     * Used in ensemble mode where all parts read intervals upward (Par. 33.4.2).
     */
    ensembleMode?: boolean;
    /**
     * Set of VoiceEntries that are continuation notes in a melisma (lyrics mode).
     * A melisma is when a single syllable is sung across multiple notes (Par. 35.2).
     * When a VoiceEntry is in this set, a syllabic slur sign (⠉) is emitted
     * after the note in the music line. Indicated by `<extend/>` in MusicXML.
     */
    melismaSlurNotes?: Set<VoiceEntry>;
}
/**
 * Result of rendering a single measure.
 */
export interface BrailleMeasureResult {
    /** The braille string for this measure (without surrounding barlines/spaces) */
    braille: string;
    /** Debug entries for all elements in this measure */
    debugEntries: BrailleNoteDebugInfo[];
    /** The measure number (1-based, from MusicXML) */
    measureNumber: number;
}
/**
 * Iterate VoiceEntries in a measure for a given staff, in beat order.
 * Handles all null-guard boilerplate for the OSMD container/staff/voice traversal.
 *
 * @param measure The SourceMeasure to traverse
 * @param staffIndex Which staff to read (0-based global index)
 * @param callback Called for each VoiceEntry array (all voices at one beat position)
 */
export declare function forEachVoiceEntryInMeasure(measure: SourceMeasure, staffIndex: number, callback: (voiceEntries: VoiceEntry[]) => void): void;
/**
 * Converts a single SourceMeasure to its braille representation.
 *
 * Handles single voice (M1-M3) and multiple voices (M4) on a single staff.
 * Multiple voices are rendered using full-measure in-accord signs.
 */
export declare class BrailleMeasureRenderer {
    private noteRenderer;
    private chordRenderer;
    constructor();
    /**
     * Render a single measure to braille.
     *
     * @param measure The SourceMeasure to convert
     * @param staffIndex Which staff to render (0-based)
     * @param state Persistent state across measures (octave tracking, etc.)
     * @returns BrailleMeasureResult with braille string and debug info
     */
    render(measure: SourceMeasure, staffIndex: number, state: BrailleState): BrailleMeasureResult;
    /**
     * Group all VoiceEntries in a measure by their parent Voice.
     * Returns an array of VoiceGroups, one per voice, with entries in beat order.
     */
    private groupVoiceEntries;
    /**
     * Order voice groups for braille output.
     * Treble/C clef: highest voice first. Bass clef: lowest voice first.
     * "Highest/lowest" is determined by the first note's pitch in each voice.
     */
    private orderVoices;
    /**
     * Get the half-tone value of the first pitched note in a voice group.
     * Used for voice ordering. Returns 0 if no pitched notes found.
     */
    private getFirstPitchHalfTone;
    /**
     * Render a sequence of VoiceEntries (from a single voice) to braille.
     * Each VoiceEntry is rendered as a single note or chord.
     * Dynamics and ottava events are interleaved by matching timestamps.
     *
     * Per Par. 22.3(e), a note following a word-sign expression (dynamic/ottava)
     * requires an octave mark, so we reset the octave tracker after emitting one.
     *
     * In facsimile mode, ottava events adjust note pitches to staff (visual) pitch
     * during the ottava passage. The start marker goes before the first affected note,
     * and the end marker goes after the last affected note (Par. 3.3.1).
     */
    private renderVoiceEntries;
    /**
     * Handle slur sign emission for the current voice entry's notes.
     * Tracks active slurs in state, emits slur sign after each note except the last
     * in a slurred phrase (Par. 13.2). Slurs concurrent with ties are omitted in
     * nonfacsimile (Par. 13.5).
     */
    private handleSlurs;
    /**
     * Check if any active slur is a short slur (≤4 notes).
     * Used to decide whether to emit per-note short slur signs.
     */
    private hasActiveShortSlur;
    /**
     * Check if a measure starts with a forward repeat barline (StartLine instruction).
     */
    private hasForwardRepeat;
    /**
     * Check if a measure ends with a backward repeat barline (BackJumpLine instruction).
     */
    private hasBackwardRepeat;
    /**
     * Render volta (alternate ending) signs for a measure.
     * Returns empty result if no volta begins at this measure.
     * Par. 17.1.1: volta sign = number sign + ending number.
     * If the following sign contains dot 1, 2, or 3, a dot 3 separator is added.
     */
    private renderVoltas;
    /**
     * Render navigation signs (Segno, Coda) that appear at the START of a measure.
     * Segno goes to FirstRepetitionInstructions. Coda can appear in either.
     */
    private renderNavigationStart;
    /**
     * Render navigation signs (D.C., D.S., Fine, ToCoda, compound forms) at the END of a measure.
     * These go to LastRepetitionInstructions.
     */
    private renderNavigationEnd;
    /**
     * Check if a bracket slur should open before the current note.
     * Bracket slurs are used for slurred phrases of more than 4 notes (Par. 13.3).
     * The open sign (⠰⠃) is placed BEFORE the first note of the phrase.
     */
    private handleBracketSlurOpen;
    /**
     * Get an adjusted pitch for a note when an ottava is active in facsimile mode.
     * Returns the staff (visual) pitch if ottava is active, or undefined if no adjustment needed.
     * Uses OctaveShift.getPitchFromOctaveShift() to compute the visual pitch.
     */
    private getOttavaPitchOverride;
    /**
     * Collect instantaneous dynamics and continuous dynamics (hairpins) for a staff
     * from SourceMeasure.StaffLinkedExpressions. Returns them sorted by timestamp.
     */
    private collectDynamicEvents;
    /**
     * Collect OctaveShift start/end events for a staff from StaffLinkedExpressions.
     * Only relevant in facsimile mode (nonfacsimile ignores ottava markings).
     * Returns events sorted by timestamp, with "end" events before "start" events
     * at the same timestamp (so loco is emitted before a new ottava begins).
     */
    private collectOctaveShiftEvents;
}
