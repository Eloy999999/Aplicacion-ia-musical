/**
 * A virtual VexFlow rendering context that computes the vertical extents (min and max y)
 * of everything drawn into it, per (pixel) column, instead of rasterizing to a canvas.
 *
 * This is used by SkyBottomLineCalculator to calculate the skyline and bottom-line of a measure
 * geometrically from the same VexFlowMeasure.draw() call that was previously made on a hidden canvas,
 * without the very expensive CanvasRenderingContext2D.getImageData() call (GPU->CPU transfer)
 * and without rasterizing/allocating a canvas at all. (see #937)
 *
 * It implements the subset of the CanvasRenderingContext2D interface that VexFlow 1.2.93 uses,
 * plus the extra methods VexFlow adds in Renderer.bolsterCanvasContext() (setFont, setFillStyle,
 * openGroup, etc., see VexFlow's canvascontext.js).
 *
 * Accuracy notes compared to the pixel-based (raster) method:
 * - Values are exact geometry instead of pixel indices, i.e. not quantized to integers and without
 *   the up-to-1px anti-aliasing halo of the raster method. Differences are well below 1px (0.1 units).
 * - Where a shape's edge lies (almost) exactly on a pixel column boundary (e.g. the tangent of a notehead
 *   ellipse, or a stem edge), the rasterizer can round the sliver of coverage down to nothing, while this
 *   context includes the column. Conservative, at most one extra pixel column per edge.
 * - Text is merged from per-character ink extents (probed once per font + character from a tiny canvas,
 *   see probeCharacterInk()). Font hinting can shift rasterized glyph edges by up to a pixel depending on
 *   the fractional pen position, which is not predictable; the unrounded extents used here are conservative.
 * - Completely transparent fills/strokes (e.g. "#00000000" used for invisible elements) are skipped,
 *   like in the raster method, where the alpha > 0 pixel test ignored them.
 * - clearRect() (only used by VexFlow's TabNote to erase stave lines behind fret numbers) is a no-op,
 *   so this context keeps the bottom stave line of tablatures solid, while the raster method saw holes in it,
 *   which even shifted its relative anchoring of the whole bottom line by ~0.2 units on tab staves.
 *   The geometric (solid) result is the more correct one.
 * - The raster method clipped contents above/below its 300px canvas (~10 units above the stave).
 *   This context has no such limit, which is more correct for extreme cases.
 * - Drawing is clipped horizontally to [0, width) like on the per-measure canvas of the raster method.
 * - Dashed/dotted strokes are treated as solid lines (conservative). Miter join spikes of stroked
 *   multi-segment paths are not modeled (only relevant for sharp angles, which VexFlow doesn't stroke).
 */
export declare class GeometricSkyBottomLineContext {
    /** Conversion factor pt -> px for font sizes, as used by canvas. */
    private readonly PT_TO_PX;
    /** Fallback font ascent as a fraction of the font size in px, if TextMetrics.actualBoundingBoxAscent is unavailable. */
    private readonly FALLBACK_FONT_ASCENT;
    /** Fallback font descent as a fraction of the font size in px, if TextMetrics.actualBoundingBoxDescent is unavailable. */
    private readonly FALLBACK_FONT_DESCENT;
    /** Fallback per-character width as a fraction of the font size in px, if no 2D context is available for measureText. */
    private readonly FALLBACK_CHAR_WIDTH;
    /** Maximum number of line segments a Bézier curve or arc is flattened into. */
    private readonly MAX_FLATTENING_SEGMENTS;
    /** Target length (in px) of the line segments a Bézier curve is flattened into. */
    private readonly FLATTENING_SEGMENT_LENGTH;
    /** Caches for text measurements and glyph outline flattening, typically owned by EngravingRules
     *  (one per OSMD instance) so they persist across measures, stafflines and renders. */
    private readonly caches;
    /** Minimum drawn y per pixel column (the skyline, in device pixels). +Infinity = column untouched. */
    private minY;
    /** Maximum drawn y per pixel column (the bottom-line, in device pixels). -Infinity = column untouched. */
    private maxY;
    private width;
    private translateX;
    private translateY;
    private scaleX;
    private scaleY;
    private currentLineWidth;
    private currentFillStyle;
    private fillTransparent;
    private currentStrokeStyle;
    private strokeTransparent;
    private currentFont;
    private stateStack;
    private pathSegments;
    private hasCurrentPoint;
    private currentX;
    private currentY;
    private subpathStartX;
    private subpathStartY;
    /** Set by VexFlow's Renderer.bolsterCanvasContext() pattern, some code accesses ctx.vexFlowCanvasContext. */
    vexFlowCanvasContext: GeometricSkyBottomLineContext;
    /** Mimics CanvasRenderingContext2D.canvas (width/height only). Some code checks for its existence. */
    canvas: {
        width: number;
        height: number;
    };
    constructor(width?: number, height?: number, caches?: GeometricSkyBottomLineCaches);
    /** Resets the context for a new measure of the given pixel width. */
    initialize(width: number, height?: number): void;
    /**
     * Writes the computed extents into the given skyline/bottomline arrays (in device pixels,
     * indexed by pixel column), in the same format the raster method produced them:
     * columns where nothing was drawn are left undefined.
     */
    copyExtentsInto(skyLine: number[], bottomLine: number[]): void;
    beginPath(): void;
    moveTo(x: number, y: number): void;
    lineTo(x: number, y: number): void;
    quadraticCurveTo(cpx: number, cpy: number, x: number, y: number): void;
    bezierCurveTo(cp1x: number, cp1y: number, cp2x: number, cp2y: number, x: number, y: number): void;
    arc(x: number, y: number, radius: number, startAngle: number, endAngle: number, antiClockwise?: boolean): void;
    rect(x: number, y: number, width: number, height: number): void;
    closePath(): void;
    /**
     * For min/max per column, the fill of a closed path has the same extents as its outline:
     * the topmost/bottommost filled point of a column always lies on the path boundary
     * (holes like in half note noteheads never contain a column's extremes).
     */
    fill(): void;
    stroke(): void;
    fillRect(x: number, y: number, width: number, height: number): void;
    /** Only used by VexFlow's TabNote to erase the stave lines behind fret numbers,
     *  which never affects the extents (skyline/bottom-line). Erasing is not supported by this context. */
    clearRect(x: number, y: number, width: number, height: number): void;
    /**
     * Merges the ink extents of the text, character by character (a single box for the whole string
     * would e.g. claim ascender height above lowercase letters, unlike the rasterized text the
     * raster method saw, which has per-character contours).
     */
    fillText(text: string, x: number, y: number): void;
    measureText(text: string): TextMetrics;
    /**
     * Fast path for glyphs, called by VexFlow's (patched) Glyph.renderOutline() instead of issuing
     * the outline's dozens of path commands: merges the glyph outline's flattened line segments,
     * cached once per outline + scale (the curve flattening and command parsing are the expensive
     * part of glyph drawing, and the same glyphs repeat constantly: noteheads, accidentals, ...).
     * The merged segments are identical to the ones the normal path commands would produce,
     * so the result is exactly the same.
     * Returns true if handled (VexFlow then skips the normal path commands).
     */
    drawCachedGlyphOutline(outline: object, scale: number, xPos: number, yPos: number): boolean;
    save(): void;
    restore(): void;
    scale(x: number, y: number): void;
    translate(x: number, y: number): void;
    setFillStyle(style: string): GeometricSkyBottomLineContext;
    get fillStyle(): string;
    set fillStyle(style: string);
    setStrokeStyle(style: string): GeometricSkyBottomLineContext;
    get strokeStyle(): string;
    set strokeStyle(style: string);
    setLineWidth(width: number): GeometricSkyBottomLineContext;
    get lineWidth(): number;
    set lineWidth(width: number);
    setFont(family: string, size: number, weight: string | number): GeometricSkyBottomLineContext;
    setRawFont(font: string): GeometricSkyBottomLineContext;
    get font(): string;
    set font(font: string);
    clear(): void;
    openGroup(cls?: string, id?: string, attrs?: object): undefined;
    closeGroup(): void;
    getGroup(): undefined;
    add(): void;
    setBackgroundFillStyle(style: string): GeometricSkyBottomLineContext;
    setShadowColor(style: string): GeometricSkyBottomLineContext;
    setShadowBlur(blur: number): GeometricSkyBottomLineContext;
    setLineCap(capType: string): GeometricSkyBottomLineContext;
    setLineDash(dash: number[]): GeometricSkyBottomLineContext;
    set lineCap(capType: string);
    set lineDash(dash: number[]);
    glow(): GeometricSkyBottomLineContext;
    private deviceX;
    private deviceY;
    private flatteningSegments;
    /** Merges a line segment (in device coordinates) into the extent arrays. */
    private mergeSegment;
    /** Merges a stroked line segment by merging the outline of its (butt-capped) stroke rectangle. */
    private mergeStrokedSegment;
    /** Merges the vertical range [top, bottom] into all columns intersecting [left, right) (device coordinates). */
    private mergeColumns;
    /** Font size in px, parsed from the current font string (e.g. "italic 10pt Arial" or "12px Times"). */
    private fontSizeInPixels;
    /** Returns the cached flattened segments for a glyph outline at the given scale,
     *  computing them on first use (see drawCachedGlyphOutline()). */
    private getGlyphSegments;
    /** Flattens a glyph outline at the given scale into line segments relative to the glyph origin,
     *  by replaying the outline (the same way VexFlow's Glyph.processOutline does, with y inverted)
     *  into a scratch context's path, so the segments are identical to the normal path commands'. */
    private computeGlyphSegments;
    /** Measures the ink extents of a single character in the current font, cached.
     *  Uses TextMetrics.actualBoundingBox* (exact ink extents) where available,
     *  with estimates from the font size as a fallback. */
    private measureCharacter;
    /** Renders a single character to a small hidden canvas and scans its exact ink extents.
     *  Only happens once per font + character (see characterExtentsCache); the canvas is tiny
     *  (a few hundred pixels), so this has none of the performance issues of the per-measure
     *  getImageData calls of the raster method. Returns undefined if no canvas is available. */
    private probeCharacterInk;
    private measureTextInternal;
}
/**
 * Caches and shared helper objects for GeometricSkyBottomLineContext. All cached values are pure
 * functions of their keys (font + character, glyph outline + scale), so they could in principle be
 * shared globally - but they are kept as an instanced object (owned by EngravingRules, like e.g.
 * NoteToGraphicalNoteMap) to avoid mutable static state, so that multiple OSMD instances on one
 * page stay fully independent (and the caches are freed with the instance).
 */
export declare class GeometricSkyBottomLineCaches {
    /** Shared hidden canvas context used only for measureText() (cheap, no pixel readback). */
    textMeasureContext: CanvasRenderingContext2D;
    textMeasureContextCreationFailed: boolean;
    measureTextWarningLogged: boolean;
    /** Character ink extents, cached by font + character (see measureCharacter()). */
    characterExtents: Map<string, ICharacterExtents>;
    /** Flattened line segments of glyph outlines (quadruples x0,y0,x1,y1, relative to the glyph
     *  origin, already scaled and y-inverted), cached per outline (by reference) and scale, see
     *  drawCachedGlyphOutline(). VexFlow caches the outline arrays on its font glyph entries,
     *  so they are stable keys that live as long as the font. */
    glyphSegments: WeakMap<object, Map<number, Float64Array>>;
    /** Scratch context used to flatten glyph outlines (reused, see computeGlyphSegments()). */
    glyphSegmentsScratch: GeometricSkyBottomLineContext;
    /** Tiny hidden canvas used to probe the exact rasterized ink extents of single characters
     *  (once per font + character, then cached in characterExtents). */
    characterProbeCanvas: HTMLCanvasElement;
    characterProbeContext: CanvasRenderingContext2D;
    characterProbeCreationFailed: boolean;
}
/** Ink extents of a single character, in px for the font it was measured with.
 *  inkLeft/inkRight are relative to the pen position (inkLeft can be slightly negative,
 *  e.g. italic overhang, and inkRight can exceed the advance width). */
interface ICharacterExtents {
    advance: number;
    ascent: number;
    descent: number;
    inkLeft: number;
    inkRight: number;
}
export {};
