import { VoiceEntry, StemDirectionType } from "./VoiceEntry";
import { SourceStaffEntry } from "./SourceStaffEntry";
import { Fraction } from "../../Common/DataObjects/Fraction";
import { NoteEnum, Pitch } from "../../Common/DataObjects/Pitch";
import { Beam } from "./Beam";
import { Tuplet } from "./Tuplet";
import { Tie } from "./Tie";
import { Staff } from "./Staff";
import { Slur } from "./Expressions/ContinuousExpressions/Slur";
import { NoteState } from "../Graphical/DrawingEnums";
import { Notehead } from "./Notehead";
import { Arpeggio } from "./Arpeggio";
import { NoteType } from "./NoteType";
import { SourceMeasure } from "./SourceMeasure";
import { TechnicalInstruction } from "./Instructions";
import { Glissando } from "../../MusicalScore/VoiceData/Glissando";
/**
 * Represents a single pitch with a duration (length)
 */
export declare class Note {
    constructor(voiceEntry: VoiceEntry, parentStaffEntry: SourceStaffEntry, length: Fraction, pitch: Pitch, sourceMeasure: SourceMeasure, isRest?: boolean);
    /**
     * The transposed (!!!) HalfTone of this note.
     */
    halfTone: number;
    state: NoteState;
    private voiceEntry;
    private parentStaffEntry;
    private length;
    private sourceMeasure;
    /** The length/duration given in the <type> tag. different from length for tuplets/tremolos. */
    private typeLength;
    /** The NoteType given in the XML, e.g. quarter, which can be a normal quarter or tuplet quarter -> can have different length/fraction */
    private noteTypeXml;
    DotsXml: number;
    /** The amount of notes the tuplet of this note (if there is one) replaces. */
    private normalNotes;
    private isRestFlag;
    IsWholeMeasureRest: boolean;
    /**
     * The untransposed (!!!) source data.
     */
    private pitch;
    /** The transposed pitch, if the score is transposed, otherwise undefined. */
    TransposedPitch: Pitch;
    displayStepUnpitched: NoteEnum;
    displayOctaveUnpitched: number;
    get NoteAsString(): string;
    private beam;
    private tuplet;
    private tie;
    private glissando;
    private slurs;
    private playbackInstrumentId;
    private notehead;
    /** Custom notehead vexflow code. E.g. "vb" = quarter, "v1d" = whole, "v53" = half, etc. - see tables.js
     * Set this before render() (e.g. after load, before first render).
     */
    CustomNoteheadVFCode: string;
    /** States whether the note should be displayed. False if xmlNode.attribute("print-object").value = "no". */
    private printObject;
    /** The Arpeggio this note is part of. */
    private arpeggio;
    /** States whether this is a cue note (Stichnote) (smaller size). */
    private isCueNote;
    IsGraceNote: boolean;
    /** The stem direction asked for in XML. Not necessarily final or wanted stem direction. */
    private stemDirectionXml;
    /** Tremolo information for this note, e.g. the number of tremolo strokes (16th tremolo = 2 strokes),
     * or the TremoloBetweenNotes object for a tremolo between two notes.
     */
    TremoloInfo: TremoloInfo;
    /** Color of the stem given in the XML Stem tag. RGB Hexadecimal, like #00FF00.
     * This is not used for rendering, which takes VoiceEntry.StemColor.
     * It is merely given in the note's stem element in XML and stored here for reference.
     * So, to read or change the stem color of a note, modify note.ParentVoiceEntry.StemColor.
     */
    private stemColorXml;
    /** Color of the notehead given in the XML Notehead tag. RGB Hexadecimal, like #00FF00.
     * This should not be changed, instead noteheadColor is used and modifiable for Rendering.
     * Needs to be stored here and not in Note.Notehead,
     * because Note.Notehead is undefined for normal Noteheads to save space and time.
     */
    private noteheadColorXml;
    /** Color of the notehead currently set/desired for next render. RGB Hexadecimal, like #00FF00.
     * Needs to be stored here and not in Note.Notehead,
     * because Note.Notehead is undefined for normal Noteheads to save space and time.
     */
    private noteheadColor;
    private noteheadColorCurrentlyRendered;
    Fingering: TechnicalInstruction;
    StringInstruction: TechnicalInstruction;
    /** Used by GraphicalNote.FromNote(note) and osmd.rules.GNote(note) to get a GraphicalNote from a Note.
     *  Note that we don't want the data model (Note) to be dependent on the graphical implementation (GraphicalNote),
     *    and have (potentially circular) import dependencies of graphical parts, which also applies to other non-graphical classes.
     *    That's why we don't save a GraphicalNote reference directly in Note.
     */
    NoteToGraphicalNoteObjectId: number;
    ToStringShort(octaveOffset?: number): string;
    get ToStringShortGet(): string;
    get ParentVoiceEntry(): VoiceEntry;
    set ParentVoiceEntry(value: VoiceEntry);
    get ParentStaffEntry(): SourceStaffEntry;
    get ParentStaff(): Staff;
    get Length(): Fraction;
    set Length(value: Fraction);
    get SourceMeasure(): SourceMeasure;
    get TypeLength(): Fraction;
    set TypeLength(value: Fraction);
    get NoteTypeXml(): NoteType;
    set NoteTypeXml(value: NoteType);
    get NormalNotes(): number;
    set NormalNotes(value: number);
    get Pitch(): Pitch;
    get NoteBeam(): Beam;
    set NoteBeam(value: Beam);
    set Notehead(value: Notehead);
    get Notehead(): Notehead;
    get NoteTuplet(): Tuplet;
    set NoteTuplet(value: Tuplet);
    /** All tuplets this note is part of, from outermost to innermost (for nested tuplets). Usually a single tuplet.
     *  NoteTuplet stays the innermost one for backwards compatibility; this list adds the enclosing tuplet(s). */
    NoteTuplets: Tuplet[];
    get NoteGlissando(): Glissando;
    set NoteGlissando(value: Glissando);
    get NoteTie(): Tie;
    set NoteTie(value: Tie);
    get NoteSlurs(): Slur[];
    set NoteSlurs(value: Slur[]);
    get PlaybackInstrumentId(): string;
    set PlaybackInstrumentId(value: string);
    get PrintObject(): boolean;
    set PrintObject(value: boolean);
    /** Whether this note's own notehead is hidden (e.g. print-object="no" or notehead "none") but there is a
     * visible note on the same staff line in another voice at the same staff entry - i.e. a unison whose visible
     * notehead this note shares. Used to keep such a note's beam and stem rendered (the stem still emanates from
     * the shared notehead and joins the beam) instead of dropping it. E.g. an eighth note sharing a notehead with
     * a dotted quarter in Beethoven's Moonlight Sonata 1st mvt. m.37 (test_unison_notehead_moonlight_sonata_measure37). */
    sharesNoteheadWithVisibleUnisonNote(): boolean;
    get Arpeggio(): Arpeggio;
    set Arpeggio(value: Arpeggio);
    get IsCueNote(): boolean;
    set IsCueNote(value: boolean);
    get StemDirectionXml(): StemDirectionType;
    set StemDirectionXml(value: StemDirectionType);
    get TremoloStrokes(): number;
    get StemColorXml(): string;
    set StemColorXml(value: string);
    get NoteheadColorXml(): string;
    set NoteheadColorXml(value: string);
    /** The desired notehead color for the next render. */
    get NoteheadColor(): string;
    set NoteheadColor(value: string);
    get NoteheadColorCurrentlyRendered(): string;
    set NoteheadColorCurrentlyRendered(value: string);
    isRest(): boolean;
    /** Note: May be dangerous to use if ParentStaffEntry.VerticalContainerParent etc is not set.
     * better calculate this directly when you have access to the note's measure.
     * whole rest: length = measure length. (4/4 in a 4/4 time signature, 3/4 in a 3/4 time signature, 1/4 in a 1/4 time signature, etc.)
     */
    isWholeRest(): boolean;
    /** Whether the note fills the whole measure. */
    isWholeMeasureNote(): boolean;
    ToString(): string;
    getAbsoluteTimestamp(): Fraction;
    isDuplicateSlur(slur: Slur): boolean;
    hasTabEffects(): boolean;
}
export declare enum Appearance {
    Normal = 0,
    Grace = 1,
    Cue = 2
}
export interface TremoloInfo {
    /** Number of tremolo strokes (e.g. 16th tremolo = 2 strokes).
     * For a tremolo between notes, the number of strokes ("tremolo beams") drawn between the two notes. */
    tremoloStrokes: number;
    /** Buzz roll (type="unmeasured" in XML) */
    tremoloUnmeasured: boolean;
    /** Whether this note starts a tremolo between (two) notes (type="start" in XML). */
    tremoloBetweenNotesStart?: boolean;
    /** Whether this note stops/ends a tremolo between (two) notes (type="stop" in XML). */
    tremoloBetweenNotesStop?: boolean;
    /** The tremolo between (two) notes this note is part of, linking start and stop note.
     * This object is shared between the start note and the stop note,
     * set in VoiceGenerator.handleTremoloBetweenNotes(). */
    tremoloBetweenNotes?: TremoloBetweenNotes;
}
/** A tremolo between two notes, e.g. two alternating half notes with 3 strokes ("tremolo beams") between them,
 * often seen in orchestral string parts. (<tremolo type="start"> and type="stop" in MusicXML)
 * Note that for these tremolos, each note is notated with the full duration of the tremolo,
 * but only played for half of it (e.g. notated two half notes = tremolo over one half note duration),
 * so Note.TypeLength is twice the Note.Length here.
 * The strokes are drawn in VexFlowMusicSheetDrawer.drawTremolosBetweenNotes(). */
export interface TremoloBetweenNotes {
    /** Number of strokes ("tremolo beams") drawn between the two notes. */
    strokes: number;
    /** The first/left note of the tremolo. */
    startNote: Note;
    /** The second/right note of the tremolo. Undefined until the stop note is read. */
    stopNote: Note;
}
