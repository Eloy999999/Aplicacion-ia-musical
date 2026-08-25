import { EngravingRules } from "./EngravingRules";
import { ITextMeasurer } from "../Interfaces/ITextMeasurer";
import { GraphicalMusicSheet } from "./GraphicalMusicSheet";
import { BoundingBox } from "./BoundingBox";
import { DrawingParameters } from "./DrawingParameters";
import { GraphicalLine } from "./GraphicalLine";
import { RectangleF2D } from "../../Common/DataObjects/RectangleF2D";
import { PointF2D } from "../../Common/DataObjects/PointF2D";
import { GraphicalRectangle } from "./GraphicalRectangle";
import { GraphicalLabel } from "./GraphicalLabel";
import { SelectionStartSymbol } from "./SelectionStartSymbol";
import { SelectionEndSymbol } from "./SelectionEndSymbol";
import { MusicSystem } from "./MusicSystem";
import { GraphicalMeasure } from "./GraphicalMeasure";
import { StaffLine } from "./StaffLine";
import { SystemLine } from "./SystemLine";
import { MusicSymbol } from "./MusicSymbol";
import { GraphicalMusicPage } from "./GraphicalMusicPage";
import { MusicSymbolDrawingStyle, PhonicScoreModes } from "./DrawingMode";
import { GraphicalObject } from "./GraphicalObject";
import { GraphicalInstantaneousDynamicExpression } from "./GraphicalInstantaneousDynamicExpression";
import { GraphicalContinuousDynamicExpression } from "./GraphicalContinuousDynamicExpression";
/**
 * Draw a [[GraphicalMusicSheet]] (through the .drawSheet method)
 *
 * The drawing is implemented with a top-down approach, starting from a music sheet, going through pages, systems, staffs...
 * ... and ending in notes, beams, accidentals and other symbols.
 * It's worth to say, that this class just draws the symbols and graphical elements, using the positions that have been computed before.
 * But in any case, some of these previous positioning algorithms need the sizes of the concrete symbols (NoteHeads, sharps, flats, keys...).
 * Therefore, there are some static functions on the 'Bounding Boxes' section used to compute these symbol boxes at the
 * beginning for the later use in positioning algorithms.
 *
 * This class also includes the resizing and positioning of the symbols due to user interaction like zooming or panning.
 */
export declare abstract class MusicSheetDrawer {
    drawingParameters: DrawingParameters;
    splitScreenLineColor: number;
    midiPlaybackAvailable: boolean;
    drawableBoundingBoxElement: string;
    skyLineVisible: boolean;
    bottomLineVisible: boolean;
    /** Lazy rendering: when >= 0, drawPage() draws only the systems of (the first) page whose
     *  index is within [LazyDrawSystemsFromIndex, LazyDrawSystemsToIndexExcl), leaving the
     *  already-drawn systems above untouched in the shared backend. -1 (default) draws every system.
     *  Set by OpenSheetMusicDisplay.renderAppend() before each appended batch; reset to -1 after. */
    LazyDrawSystemsFromIndex: number;
    LazyDrawSystemsToIndexExcl: number;
    /** Lazy horizontal rendering (RenderSingleHorizontalStaffline): draw only graphical objects whose right
     *  edge x (in OSMD units) lies in (LazyDrawFromXUnits, LazyDrawToXUnits] -- the measures and spanning
     *  elements that first entered the drawn frontier this batch. ±Infinity (default) draws everything.
     *  Set by OpenSheetMusicDisplay.renderAppendGrowingHorizontal() per batch; reset after. */
    LazyDrawFromXUnits: number;
    LazyDrawToXUnits: number;
    /** Lazy horizontal rendering: when true, drawPage() skips the page-level labels (title/credits). They are
     *  drawn once, on the final batch, when the page has reached its full width and they sit at their final
     *  (re-centered) positions -- drawing them earlier would place them under a still-growing page. */
    LazySkipPageLabels: boolean;
    /** Lazy horizontal rendering: when true, drawLabel() ignores the x-window gate. Scoped (set/restored) to
     *  the page-label loop in drawPage(), since those labels span the full page width and must all be drawn
     *  even though their left edges lie behind the final batch's frontier. */
    LazyForcePageLabels: boolean;
    protected rules: EngravingRules;
    protected graphicalMusicSheet: GraphicalMusicSheet;
    protected textMeasurer: ITextMeasurer;
    private phonicScoreMode;
    constructor(textMeasurer: ITextMeasurer, drawingParameters: DrawingParameters);
    set Mode(value: PhonicScoreModes);
    drawSheet(graphicalMusicSheet: GraphicalMusicSheet): void;
    drawLineAsHorizontalRectangle(line: GraphicalLine, layer: number): void;
    drawLineAsVerticalRectangle(line: GraphicalLine, layer: number): void;
    drawLineAsHorizontalRectangleWithOffset(line: GraphicalLine, offset: PointF2D, layer: number): void;
    drawLineAsVerticalRectangleWithOffset(line: GraphicalLine, offset: PointF2D, layer: number): void;
    drawRectangle(rect: GraphicalRectangle, layer: number): void;
    calculatePixelDistance(unitDistance: number): number;
    /** Lazy horizontal rendering: whether an object with this bounding box falls in the current draw
     *  x-window (its right edge first entered the drawn frontier this batch). True when not lazy-horizontal. */
    protected lazyDrawsAtX(rightXUnits: number): boolean;
    protected lazyDrawsObject(psh: BoundingBox): boolean;
    /** Lazy horizontal rendering: whether to draw the once-only left-edge system elements (instrument braces
     *  and group brackets). True for non-lazy and for the first lazy-horizontal batch, which owns the left edge
     *  (LazyDrawFromXUnits is -Infinity); false for continuation batches, so a single-system score's brace
     *  isn't redrawn on top of itself every batch. (Vertical lazy keeps the x-window at ±Infinity and draws
     *  each system's brace once via the per-system gate, so this stays true there.) */
    protected lazyDrawsLeftEdgeOnce(): boolean;
    drawLabel(graphicalLabel: GraphicalLabel, layer: number): Node;
    protected applyScreenTransformation(point: PointF2D): PointF2D;
    protected applyScreenTransformations(points: PointF2D[]): PointF2D[];
    protected applyScreenTransformationForRect(rectangle: RectangleF2D): RectangleF2D;
    protected drawSplitScreenLine(): void;
    protected renderRectangle(rectangle: RectangleF2D, layer: number, styleId: number, colorHex?: string, alpha?: number): Node;
    protected drawScrollIndicator(): void;
    protected drawSelectionStartSymbol(symbol: SelectionStartSymbol): void;
    protected drawSelectionEndSymbol(symbol: SelectionEndSymbol): void;
    protected renderLabel(graphicalLabel: GraphicalLabel, layer: number, bitmapWidth: number, bitmapHeight: number, heightInPixel: number, screenPosition: PointF2D): Node;
    protected renderSystemToScreen(system: MusicSystem, systemBoundingBoxInPixels: RectangleF2D, absBoundingRectWithMargin: RectangleF2D): void;
    protected drawMeasure(measure: GraphicalMeasure): void;
    protected drawSkyLine(staffLine: StaffLine): void;
    protected drawBottomLine(staffLine: StaffLine): void;
    protected drawInstrumentBrace(brace: GraphicalObject, system: MusicSystem): void;
    protected drawGroupBracket(bracket: GraphicalObject, system: MusicSystem): void;
    protected isVisible(psi: BoundingBox): boolean;
    protected drawMusicSystem(system: MusicSystem): void;
    protected getSytemBoundingBoxInPixels(absBoundingRectWithMargin: RectangleF2D): RectangleF2D;
    protected getSystemAbsBoundingRect(system: MusicSystem): RectangleF2D;
    protected drawMusicSystemComponents(musicSystem: MusicSystem, systemBoundingBoxInPixels: RectangleF2D, absBoundingRectWithMargin: RectangleF2D): void;
    protected activateSystemRendering(systemId: number, absBoundingRect: RectangleF2D, systemBoundingBoxInPixels: RectangleF2D, createNewImage: boolean): boolean;
    protected drawSystemLineObject(systemLine: SystemLine): void;
    protected drawStaffLine(staffLine: StaffLine): void;
    protected drawLyricLines(lyricLines: GraphicalLine[], staffLine: StaffLine): void;
    protected drawExpressions(staffline: StaffLine): void;
    protected drawGraphicalLine(graphicalLine: GraphicalLine, lineWidth: number, colorOrStyle?: string): Node;
    protected drawLine(start: PointF2D, stop: PointF2D, color: string, lineWidth: number): Node;
    /**
     * Draw all dashes to the canvas
     * @param lyricsDashes Array of lyric dashes to be drawn
     * @param layer Number of the layer that the lyrics should be drawn in
     */
    protected drawDashes(lyricsDashes: GraphicalLabel[]): void;
    protected drawOctaveShifts(staffLine: StaffLine): void;
    protected abstract drawPedals(staffLine: StaffLine): void;
    protected abstract drawWavyLines(staffLine: StaffLine): void;
    protected drawStaffLines(staffLine: StaffLine): void;
    /**
     * Draws an instantaneous dynamic expression (p, pp, f, ff, ...) to the canvas
     * @param instantaneousDynamic GraphicalInstantaneousDynamicExpression to be drawn
     */
    protected drawInstantaneousDynamic(instantaneousDynamic: GraphicalInstantaneousDynamicExpression): void;
    /**
     * Draws a continuous dynamic expression (wedges) to the canvas
     * @param expression GraphicalContinuousDynamicExpression to be drawn
     */
    protected drawContinuousDynamic(expression: GraphicalContinuousDynamicExpression): void;
    protected drawSymbol(symbol: MusicSymbol, symbolStyle: MusicSymbolDrawingStyle, position: PointF2D, scalingFactor?: number, layer?: number): void;
    protected get leadSheet(): boolean;
    protected set leadSheet(value: boolean);
    protected drawPage(page: GraphicalMusicPage): void;
    /**
     * Draw bounding boxes aroung GraphicalObjects
     * @param startBox Bounding Box that is used as a staring point to recursively go through all child elements
     * @param layer Layer to draw to
     * @param type Type of element to show bounding boxes for as string.
     */
    private drawBoundingBoxes;
    drawBoundingBox(bbox: BoundingBox, color?: string, drawCross?: boolean, labelText?: string, layer?: number): Node;
    private drawMarkedAreas;
    private drawComment;
    private drawStaffLineSymbols;
}
