import { Instrument } from "../Instrument";
import { SourceMeasure } from "../VoiceData/SourceMeasure";
import { KeyInstruction } from "../VoiceData/Instructions/KeyInstruction";
import { RhythmInstruction } from "../VoiceData/Instructions/RhythmInstruction";
import { Fraction } from "../../Common/DataObjects/Fraction";
import { IXmlElement } from "../../Common/FileIO/Xml";
import { RepetitionInstructionReader } from "./MusicSymbolModules/RepetitionInstructionReader";
import { ReaderPluginManager } from "./ReaderPluginManager";
/**
 * An InstrumentReader is used during the reading phase to keep parsing new measures from the MusicXML file
 * with the readNextXmlMeasure method.
 */
export declare class InstrumentReader {
    constructor(pluginManager: ReaderPluginManager, repetitionInstructionReader: RepetitionInstructionReader, xmlMeasureList: IXmlElement[], instrument: Instrument);
    private repetitionInstructionReader;
    private xmlMeasureList;
    private musicSheet;
    private slurReader;
    pluginManager: ReaderPluginManager;
    private instrument;
    private voiceGeneratorsDict;
    private staffMainVoiceGeneratorDict;
    private inSourceMeasureInstrumentIndex;
    private divisions;
    private currentMeasure;
    private previousMeasure;
    private currentClefNumber;
    private currentXmlMeasureIndex;
    private currentStaff;
    private currentStaffEntry;
    private activeClefs;
    private activeKey;
    private activeRhythm;
    private activeClefsHaveBeenInitialized;
    private activeKeyHasBeenInitialized;
    private abstractInstructions;
    private expressionReaders;
    private currentVoiceGenerator;
    private maxTieNoteFraction;
    private currentMultirestStartMeasure;
    private followingMultirestMeasures;
    get ActiveKey(): KeyInstruction;
    get MaxTieNoteFraction(): Fraction;
    get ActiveRhythm(): RhythmInstruction;
    set ActiveRhythm(value: RhythmInstruction);
    /**
     * Main CreateSheet: read the next XML Measure and save all data to the given [[SourceMeasure]].
     * @param currentMeasure
     * @param measureStartAbsoluteTimestamp - Using this instead of currentMeasure.AbsoluteTimestamp as it isn't set yet
     * @param octavePlusOne Software like Guitar Pro gives one octave too low, so we need to add one
     * @returns {boolean}
     */
    readNextXmlMeasure(currentMeasure: SourceMeasure, measureStartAbsoluteTimestamp: Fraction, octavePlusOne: boolean): boolean;
    private getStemDirectionAndColors;
    /** Parse a color in XML format. Can be #ARGB or #RGB format, colors as byte hex values.
     *  @return color in Vexflow format #[A]RGB or undefined for invalid xmlColorString
     */
    parseXmlColor(xmlColorString: string): string;
    doCalculationsAfterDurationHasBeenSet(): void;
    /**
     * Get or create the passing [[VoiceGenerator]].
     * @param voiceId
     * @param staffId
     * @returns {VoiceGenerator}
     */
    private getOrCreateVoiceGenerator;
    private createExpressionGenerators;
    /**
     * Create the default [[ClefInstruction]] for the given staff index.
     * @param staffIndex
     */
    private createDefaultClefInstruction;
    /**
     * Create the default [[KeyInstruction]] in case no [[KeyInstruction]] is given in the whole [[Instrument]].
     */
    private createDefaultKeyInstruction;
    /**
     * Check if the given attributesNode is at the begin of a XmlMeasure.
     * @param parentNode
     * @param attributesNode
     * @returns {boolean}
     */
    private isAttributesNodeAtBeginOfMeasure;
    /**
     * Check if the given attributesNode is at the end of a XmlMeasure.
     * @param parentNode
     * @param attributesNode
     * @returns {boolean}
     */
    private isAttributesNodeAtEndOfMeasure;
    /**
     * Called only when no noteDuration is given in XML.
     * @param xmlNode
     * @returns {Fraction}
     */
    private getNoteDurationFromTypeNode;
    /**
     * Add (the three basic) Notation Instructions to a list
     * @param attrNode
     * @param guitarPro
     */
    private addAbstractInstruction;
    /**
     * Save the current AbstractInstructions to the corresponding [[StaffEntry]]s.
     * @param numberOfStaves
     * @param beginOfMeasure
     */
    private saveAbstractInstructionList;
    /**
     * Save any ClefInstruction given - exceptionally - at the end of the currentMeasure.
     */
    private saveClefInstructionAtEndOfMeasure;
    /**
     * Compute a tuplet note's real (sounding) duration.
     *
     * Per the MusicXML spec a note's <duration> already reflects the tuplet ratio, so we normally take it
     * verbatim. Doing so also preserves an exporter's rounding when the divisions value can't encode the
     * exact tuplet fraction (e.g. a triplet eighth when divisions isn't divisible by 3).
     *
     * Some exporters (observed: musx2mxl 0.2.9) instead write the *un-reduced* type duration for tuplet
     * notes — a triplet eighth carries the <duration> of a full eighth — which overflows the measure and
     * makes OSMD play and space the notes as if they weren't a tuplet. We detect that (the written
     * duration equals the note's dotted type duration instead of the smaller reduced value) and apply the
     * time-modification ratio (normal-notes / actual-notes) ourselves. When this heuristic would misfire
     * on an already-correct note the ratio is necessarily ~1, so the correction is then a no-op.
     * @param xmlNode
     * @returns {Fraction}
     */
    private getNoteDurationForTuplet;
    /**
     * The note's duration derived from its <type>, including augmentation <dot>s (e.g. a dotted eighth
     * yields 3/16). Returns a zero Fraction when no <type> is given.
     * @param xmlNode
     * @returns {Fraction}
     */
    private getDottedNoteDurationFromTypeNode;
    private readExpressionStaffNumber;
    /**
     * Calculate the divisions value from the type and duration of the first MeasureNote that makes sense
     * (meaning itself hasn't any errors and it doesn't belong to a [[Tuplet]]).
     *
     * If all the MeasureNotes belong to a [[Tuplet]], then we read the next XmlMeasure (and so on...).
     * If we have reached the end of the [[Instrument]] and still the divisions aren't set, we throw an exception
     * @returns {number}
     */
    private readDivisionsFromNotes;
    private getCueNoteAndNoteTypeXml;
    private getStemDirectionType;
    private getNoteHeadColorXml;
    private getNoteColorXml;
    private getTremoloInfo;
    private getWavyLines;
    private getNoteStaff;
}
