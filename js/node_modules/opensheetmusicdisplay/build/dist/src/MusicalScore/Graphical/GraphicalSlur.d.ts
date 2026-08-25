import { GraphicalCurve } from "./GraphicalCurve";
import { Slur } from "../VoiceData/Expressions/ContinuousExpressions/Slur";
import { PlacementEnum } from "../VoiceData/Expressions/AbstractExpression";
import { EngravingRules } from "./EngravingRules";
import { GraphicalStaffEntry } from "./GraphicalStaffEntry";
export declare class GraphicalSlur extends GraphicalCurve {
    constructor(slur: Slur, rules: EngravingRules);
    slur: Slur;
    staffEntries: GraphicalStaffEntry[];
    placement: PlacementEnum;
    graceStart: boolean;
    graceEnd: boolean;
    private rules;
    SVGElement: Node;
    /**
     * Compares the timespan of two Graphical Slurs
     * @param x
     * @param y
     */
    static Compare(x: GraphicalSlur, y: GraphicalSlur): number;
    /**
     *
     * @param rules
     */
    calculateCurve(rules: EngravingRules): void;
    /**
     * Calculates the bezier curve for a slur that crosses between two staves (e.g. left hand to right hand),
     * where the start and end notes lie on different stafflines that are stacked vertically within the same
     * MusicSystem. Unlike [[calculateCurve]], this runs at draw time, because it needs the final vertical
     * positions of both stafflines, which aren't fixed until the system Y-layout (after calculateSlurs()).
     *
     * The resulting bezier points are stored relative to the start note's staffline, so the regular drawSlur()
     * (which adds that staffline's absolute position) renders them at the correct absolute location.
     * @returns true if the curve was calculated and can be drawn, false otherwise (e.g. missing notes, or the
     * two staves are not in the same MusicSystem - a cross-staff plus cross-system slur is not supported).
     */
    calculateCurveCrossStaff(rules: EngravingRules): boolean;
    /**
     * Sums the relative positions from box up to (but not including) the given ancestor box, giving box's
     * position in the ancestor's coordinate system.
     */
    private positionRelativeToBox;
    /**
     * This method calculates the Start and End Positions of the Slur Curve.
     * @param slurStartNote
     * @param slurEndNote
     * @param staffLine
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param rules
     * @param skyBottomLineCalculator
     */
    private calculateStartAndEnd;
    /**
     * This method calculates the placement of the Curve.
     * @param skyBottomLineCalculator
     * @param staffLine
     */
    private calculatePlacement;
    /**
     * This method calculates the Points between Start- and EndPoint (case above).
     * @param start
     * @param end
     * @param staffLine
     * @param skyBottomLineCalculator
     */
    private calculateTopPoints;
    /**
     * This method calculates the Points between Start- and EndPoint (case below).
     * @param start
     * @param end
     * @param staffLine
     * @param skyBottomLineCalculator
     */
    private calculateBottomPoints;
    /**
     * This method calculates the maximum slope between StartPoint and BetweenPoints.
     * @param points
     * @param start
     * @param end
     */
    private calculateMaxLeftSlope;
    /**
     * This method calculates the maximum slope between EndPoint and BetweenPoints.
     * @param points
     * @param start
     * @param end
     */
    private calculateMaxRightSlope;
    /**
     * This method returns the maximum (meaningful) points.Y.
     * @param points
     */
    private getPointListMaxY;
    /**
     * This method calculates the translated and rotated PointsList (case above).
     * @param points
     * @param startX
     * @param startY
     * @param rotationMatrix
     */
    private calculateTranslatedAndRotatedPointListAbove;
    /**
     * This method calculates the translated and rotated PointsList (case below).
     * @param points
     * @param startX
     * @param startY
     * @param rotationMatrix
     */
    private calculateTranslatedAndRotatedPointListBelow;
    /**
     * This method calculates the HeightWidthRatio between the MaxYpoint (from the points between StartPoint and EndPoint)
     * and the X-distance from StartPoint to EndPoint.
     * @param endX
     * @param points
     */
    private calculateHeightWidthRatio;
    /**
     * This method calculates the 2 ControlPoints of the SlurCurve.
     * @param endX
     * @param startAngle
     * @param endAngle
     * @param points
     */
    private calculateControlPoints;
    /**
     * This method calculates the angles for the Curve's Tangent Lines.
     * @param leftAngle
     * @param rightAngle
     * @param startLineSlope
     * @param endLineSlope
     * @param maxAngle
     */
    private calculateAngles;
    private static degreesToRadiansFactor;
}
