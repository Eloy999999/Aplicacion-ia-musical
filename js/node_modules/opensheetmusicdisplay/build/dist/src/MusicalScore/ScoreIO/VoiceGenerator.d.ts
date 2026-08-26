import { Voice } from "../VoiceData/Voice";
import { StemDirectionType } from "../VoiceData/VoiceEntry";
import { Note, TremoloInfo } from "../VoiceData/Note";
import { SourceMeasure } from "../VoiceData/SourceMeasure";
import { SourceStaffEntry } from "../VoiceData/SourceStaffEntry";
import { Fraction } from "../../Common/DataObjects/Fraction";
import { IXmlElement } from "../../Common/FileIO/Xml";
import { Staff } from "../VoiceData/Staff";
import { SlurReader } from "./MusicSymbolModules/SlurReader";
import { NoteType } from "../VoiceData/NoteType";
import { ReaderPluginManager } from "./ReaderPluginManager";
export declare class VoiceGenerator {
    constructor(pluginManager: ReaderPluginManager, staff: Staff, voiceId: number, slurReader: SlurReader, mainVoice?: Voice);
    pluginManager: ReaderPluginManager;
    private slurReader;
    private lyricsReader;
    private articulationReader;
    private musicSheet;
    private voice;
    private currentVoiceEntry;
    private currentNote;
    private currentMeasure;
    private currentStaffEntry;
    private staff;
    private instrument;
    private openBeams;
    private beamNumberOffset;
    private get openTieDict();
    private currentOctaveShift;
    private tupletDict;
    private openTupletNumber;
    /** The last tremolo between (two) notes started in this voice, awaiting its stop note. */
    private openTremoloBetweenNotes;
    get GetVoice(): Voice;
    get OctaveShift(): number;
    set OctaveShift(value: number);
    /**
     * Create new [[VoiceEntry]], add it to given [[SourceStaffEntry]] and if given so, to [[Voice]].
     * @param musicTimestamp
     * @param parentStaffEntry
     * @param addToVoice
     * @param isGrace States whether the new VoiceEntry (only) has grace notes
     */
    createVoiceEntry(musicTimestamp: Fraction, parentStaffEntry: SourceStaffEntry, addToVoice: boolean, isGrace?: boolean, graceNoteSlash?: boolean, graceSlur?: boolean): void;
    /**
     * Create [[Note]]s and handle Lyrics, Articulations, Beams, Ties, Slurs, Tuplets.
     * @param noteNode
     * @param noteDuration
     * @param divisions
     * @param restNote
     * @param parentStaffEntry
     * @param parentMeasure
     * @param measureStartAbsoluteTimestamp
     * @param maxTieNoteFraction
     * @param chord
     * @param octavePlusOne Software like Guitar Pro gives one octave too low, so we need to add one
     * @param printObject whether the note should be rendered (true) or invisible (false)
     * @returns {Note}
     */
    read(noteNode: IXmlElement, noteDuration: Fraction, typeDuration: Fraction, noteTypeXml: NoteType, normalNotes: number, restNote: boolean, parentStaffEntry: SourceStaffEntry, parentMeasure: SourceMeasure, measureStartAbsoluteTimestamp: Fraction, maxTieNoteFraction: Fraction, chord: boolean, octavePlusOne: boolean, printObject: boolean, isCueNote: boolean, isGraceNote: boolean, stemDirectionXml: StemDirectionType, tremoloInfo: TremoloInfo, stemColorXml: string, noteheadColorXml: string, dotsXml: number): Note;
    /**
     * Create a new [[StaffEntryLink]] and sets the currenstStaffEntry accordingly.
     * @param index
     * @param currentStaff
     * @param currentStaffEntry
     * @param currentMeasure
     * @returns {SourceStaffEntry}
     */
    checkForStaffEntryLink(index: number, currentStaff: Staff, currentStaffEntry: SourceStaffEntry, currentMeasure: SourceMeasure): SourceStaffEntry;
    checkForOpenBeam(): void;
    /** Check/delete open ties that don't exceed measure duration. Currently unused as it's incorrect, see below. */
    checkOpenTies(): void;
    hasVoiceEntry(): boolean;
    private readArticulations;
    /**
     * Create a new [[Note]] and adds it to the currentVoiceEntry
     * @param node
     * @param noteDuration
     * @param divisions
     * @param chord
     * @param octavePlusOne Software like Guitar Pro gives one octave too low, so we need to add one
     * @returns {Note}
     */
    private addSingleNote;
    /**
     * Create a new rest note and add it to the currentVoiceEntry.
     * @param noteDuration
     * @param divisions
     * @returns {Note}
     */
    private addRestNote;
    private addNoteInfo;
    /**
     * Links the start and stop note of a tremolo between (two) notes (XML tremolo type="start"/"stop")
     * via a shared TremoloBetweenNotes object (note.TremoloInfo.tremoloBetweenNotes).
     */
    private handleTremoloBetweenNotes;
    /**
     * Handle the currentVoiceBeam.
     * @param node
     * @param note
     */
    private createBeam;
    private endBeam;
    /**
     * Check for open [[Beam]]s at end of [[SourceMeasure]] and closes them explicity.
     */
    private handleOpenBeam;
    /**
     * Create a [[Tuplet]].
     * @param node
     * @param tupletNodeList
     * @returns {number}
     */
    private addTuplet;
    private readShowNumberNoneGiven;
    /**
     * This method handles the time-modification IXmlElement for the Tuplet case (tupletNotes not at begin/end of Tuplet).
     * @param noteNode
     */
    private handleTimeModificationNode;
    /** Record that the given note is part of the tuplet. Sets NoteTuplet (the innermost/primary tuplet) and adds
     *  the tuplet to the note's NoteTuplets list (which holds all tuplets a note belongs to, for nested tuplets). */
    private linkNoteToTuplet;
    /** Add the current note to the given tuplet's note list (reusing the last sub-list for chords at the same
     *  timestamp, otherwise starting a new one) and add the tuplet to the note's NoteTuplets, without changing
     *  NoteTuplet. Used to add a note to enclosing/continuing tuplets when nesting. */
    private addCurrentNoteToTuplet;
    /** Add the current note to every still-open tuplet it isn't part of yet. For nested tuplets this adds a note
     *  to the enclosing (outer) tuplet(s) that are still open while it also starts/continues an inner tuplet. */
    private addCurrentNoteToOpenTuplets;
    /** Read an explicit tuplet display number from <tuplet-actual><tuplet-number> if given (used for nested tuplets,
     *  where the cumulative time-modification actual-notes - e.g. 9 - differs from the number to show - e.g. 3). */
    private readTupletActualNumber;
    private addTie;
    private getTieDirection;
    /**
     * Find the next free int (starting from 0) to use as key in TieDict.
     * @returns {number}
     */
    private getNextAvailableNumberForTie;
    /**
     * Search the tieDictionary for the corresponding candidateNote to the currentNote.
     * Prefer the existing spelling/string match, then fall back to sounding pitch for enharmonic ties.
     * @param candidateNote
     * @returns {number}
     */
    private findCurrentNoteInTieDict;
    /**
     * Calculate the normal duration of a [[Tuplet]] note.
     * @param xmlNode
     * @returns {any}
     */
    private getTupletNoteDurationFromType;
}
