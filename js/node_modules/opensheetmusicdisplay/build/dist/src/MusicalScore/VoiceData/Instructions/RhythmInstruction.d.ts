import { AbstractNotationInstruction } from "./AbstractNotationInstruction";
import { Fraction } from "../../../Common/DataObjects/Fraction";
/**
 * A [[RhythmInstruction]] is the time signature which specifies the number of beats in each bar, and the value of one beat.
 */
export declare class RhythmInstruction extends AbstractNotationInstruction {
    constructor(rhythm: Fraction, rhythmSymbolEnum: RhythmSymbolEnum);
    private numerator;
    private denominator;
    private rhythm;
    private symbolEnum;
    /** Whether this time signature was not given in the source (e.g. MusicXML), but synthesized by default,
     * e.g. a default 4/4 for a sample without a time signature like Satie's Gnossiennes.
     * Such a time signature is not rendered by default (see EngravingRules.RenderTimeSignaturesForSamplesWithoutTimeSignature). */
    SynthesizedFromNoTimeSignature: boolean;
    get Rhythm(): Fraction;
    set Rhythm(value: Fraction);
    get SymbolEnum(): RhythmSymbolEnum;
    set SymbolEnum(value: RhythmSymbolEnum);
    clone(): RhythmInstruction;
    OperatorEquals(rhythm2: RhythmInstruction): boolean;
    OperatorNotEqual(rhythm2: RhythmInstruction): boolean;
    ToString(): string;
}
export declare enum RhythmSymbolEnum {
    NONE = 0,
    COMMON = 1,
    CUT = 2
}
