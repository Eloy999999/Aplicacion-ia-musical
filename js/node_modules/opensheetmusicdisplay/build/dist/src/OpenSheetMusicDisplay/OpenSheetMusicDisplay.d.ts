import { VexFlowBackend } from "./../MusicalScore/Graphical/VexFlow/VexFlowBackend";
import { GraphicalMusicSheet } from "./../MusicalScore/Graphical/GraphicalMusicSheet";
import { VexFlowMusicSheetDrawer } from "./../MusicalScore/Graphical/VexFlow/VexFlowMusicSheetDrawer";
import { MusicSheet } from "./../MusicalScore/MusicSheet";
import { Cursor } from "./Cursor";
import { DrawingParameters } from "../MusicalScore/Graphical/DrawingParameters";
import { IOSMDOptions, BackendType, CursorOptions } from "./OSMDOptions";
import { EngravingRules, PageFormat } from "../MusicalScore/Graphical/EngravingRules";
import { GraphicalMusicPage } from "../MusicalScore/Graphical/GraphicalMusicPage";
import { GraphicalMeasure } from "../MusicalScore/Graphical/GraphicalMeasure";
import { ITransposeCalculator } from "../MusicalScore/Interfaces/ITransposeCalculator";
/**
 * The main class and control point of OpenSheetMusicDisplay.<br>
 * It can display MusicXML sheet music files in an HTML element container.<br>
 * After the constructor, use load() and render() to load and render a MusicXML file.
 */
export declare class OpenSheetMusicDisplay {
    protected version: string;
    /**
     * Creates and attaches an OpenSheetMusicDisplay object to an HTML element container.<br>
     * After the constructor, use load() and render() to load and render a MusicXML file.
     * @param container The container element OSMD will be rendered into.<br>
     *                  Either a string specifying the ID of an HTML container element,<br>
     *                  or a reference to the HTML element itself (e.g. div)
     * @param options An object for rendering options like the backend (svg/canvas) or autoResize.<br>
     *                For defaults see the OSMDOptionsStandard method in the [[OSMDOptions]] class.
     */
    constructor(container: string | HTMLElement, options?: IOSMDOptions);
    /** Options from which OSMD creates cursors in enableOrDisableCursors(). */
    cursorsOptions: CursorOptions[];
    cursors: Cursor[];
    get cursor(): Cursor;
    get Cursor(): Cursor;
    zoom: number;
    protected zoomUpdated: boolean;
    /** Timeout in milliseconds used in osmd.load(string) when string is a URL. */
    loadUrlTimeout: number;
    protected container: HTMLElement;
    protected backendType: BackendType;
    protected needBackendUpdate: boolean;
    protected sheet: MusicSheet;
    protected drawer: VexFlowMusicSheetDrawer;
    protected drawBoundingBox: string;
    protected drawSkyLine: boolean;
    protected drawBottomLine: boolean;
    protected graphic: GraphicalMusicSheet;
    protected drawingParameters: DrawingParameters;
    protected rules: EngravingRules;
    protected autoResizeEnabled: boolean;
    protected resizeHandlerAttached: boolean;
    protected followCursor: boolean;
    /** A function that is executed when the XML has been read.
     * The return value will be used as the actual XML OSMD parses,
     * so you can make modifications to the xml that OSMD will use.
     * Note that this is (re-)set on osmd.setOptions as `{return xml}`, unless you specify the function in the options. */
    OnXMLRead: (xml: string) => string;
    /**
     * Load a MusicXML file
     * @param content is either the url of a file, or the root node of a MusicXML document,
     *   or the string content of a .xml/.mxl file, or a file blob.
     * @param tempTitle is used as the title for the piece if there is no title in the XML.
     */
    load(content: string | Document | Blob, tempTitle?: string): Promise<{}>;
    /**
     * (Re-)creates the graphic sheet from the music sheet
     */
    updateGraphic(): void;
    /** Lazy rendering (LazyConsistentGraphic): number of systems already drawn into the shared
     *  backend across prior batches. Greedy layout is *usually* forward-stable, so the next batch skips
     *  redrawing these and draws from this index -- but some scores re-position earlier systems as the
     *  prefix grows, so each batch verifies the drawn systems against lazyDrawnSystemY and redraws from
     *  the topmost one that moved (reconciliation). */
    private lazyDrawnSystemCount;
    /** Lazy rendering: the absolute Y (in units) each already-drawn system [index] was drawn at,
     *  used to detect when a later batch's full-prefix layout moves an earlier system (forward-stability
     *  is not universal) so it can be redrawn at its corrected position. */
    private lazyDrawnSystemY;
    /** Lazy HORIZONTAL rendering (RenderSingleHorizontalStaffline): number of graphical measures already
     *  drawn into the shared SVG (left-to-right). The next batch draws from here, deferring the prefix's
     *  last measure (it carries an end-barline until it becomes interior), like the vertical path defers
     *  its last system. */
    private lazyDrawnHMeasureCount;
    /** Lazy HORIZONTAL: for a multi-staff score, lay the whole score out ONCE on the first batch and reuse that
     *  final layout for every batch (only the drawn x-window grows). A growing partial re-layout would route
     *  slurs/ties along each batch's boundary skyline and size the inter-staff gap to the prefix's max
     *  clearance -- so lower stafflines would drift down batch to batch. A single horizontal staffline always
     *  fits one SVG, so the full layout is safe; only the (expensive) DRAW stays lazy. Single-staff scores keep
     *  the growing layout (lazy layout + draw; nothing below the top line to drift). */
    private lazyHReuseLayout;
    /** Incremental rendering ({@link renderNext}): whether a session is in progress (started, not yet reset). */
    private lazyIncrementalActive;
    /** Incremental rendering: source-measure index where the next batch continues (the drawn frontier). */
    private lazyNextSourceIndex;
    /** Incremental rendering: the draw-measure range the lazy layout mutates, saved on begin and restored on
     *  reset, so a later normal render() isn't left limited to the last batch's draw range. */
    private lazySavedMinMeasureToDrawIndex;
    private lazySavedMaxMeasureToDrawIndex;
    /** Incremental rendering: the scroll listener + its target, while enableIncrementalRenderingOnScroll() is on. */
    private lazyScrollHandler;
    private lazyScrollTarget;
    /** Render the loaded music sheet to the container. */
    render(): void;
    /** Internal range-based engine behind {@link renderNext} (the public incremental API). Lays out the
     *  whole prefix [0..toMeasureIndex] and APPENDS the newly-stable source measures below previously
     *  rendered batches, without clearing the container, so a large score renders "system by system".
     *  clearFirst=true starts a fresh session (clears prior content, resets the counters). Returns the
     *  source-measure index at which the next batch should continue. fromMeasureIndex is informational --
     *  the drawn frontier is tracked internally. Targets the endless vertical-scroll format and
     *  RenderSingleHorizontalStaffline. `targetNewSystems`, if set, draws that many whole systems this batch
     *  (vertical path only; ignored for the single horizontal staffline, which is one system). */
    private renderAppend;
    /**
     * Incrementally render the loaded sheet one batch at a time, appending each batch so a large score
     * paints progressively ("system by system") instead of blocking on a full render(). The first call --
     * or the first after load(), render() or resetIncrementalRendering() -- starts a fresh session: it
     * clears the container and lays the score out from the first measure. Each later call appends the next
     * batch. Returns progress; once `done` is true the whole sheet is rendered and further calls are no-ops.
     *
     * Pair with {@link enableIncrementalRenderingOnScroll} for scroll-to-load, or {@link renderRemaining}
     * to finish synchronously (e.g. before PDF/image export). Works for the endless vertical-scroll page
     * format and for RenderSingleHorizontalStaffline (one staffline scrolling right).
     *
     * @param options batch options; defaults to 8 visual measures (a multi-rest counts as one). Pass
     *   `systems` instead to advance by whole music systems (vertical only); see {@link IRenderNextOptions}.
     */
    renderNext(options?: IRenderNextOptions): IRenderNextResult;
    /**
     * Finish an in-progress incremental render synchronously: render all remaining measures at once. Useful
     * before PDF/image export or printing, which need every system, not just the ones scrolled into view.
     * No-op if no incremental render is in progress, or it is already complete.
     */
    renderRemaining(): void;
    /** Whether an incremental render ({@link renderNext}) is in progress (started and not yet reset). */
    get IncrementalRenderingActive(): boolean;
    /** Whether the in-progress incremental render has rendered the whole sheet (its last measure). */
    get IncrementalRenderingComplete(): boolean;
    /** Current incremental-render progress as a snapshot (same shape {@link renderNext} returns), queryable
     *  at any time -- e.g. for a progress bar, or to re-render the same extent after a resize. Reports zero
     *  rendered measures when no session is active. */
    get IncrementalRenderProgress(): IRenderNextResult;
    /** All GraphicalMeasures (one per staff/instrument) at the measure position for source-measure index
     *  `sourceIndex`, walking BACK over collapsed multi-rest members (which have no graphical measure of their
     *  own) to the nearest real measure. Empty if there is none at/before it. Used to resolve the last- /
     *  next-measure handles for renderNext(). */
    private graphicalMeasuresAtOrBefore;
    /**
     * Abandon any in-progress incremental render and restore the draw-measure range it mutated, so a
     * following normal render() draws the whole sheet again. Called
     * automatically by render(), load() and clear(); call it directly only to cancel a session yourself.
     */
    resetIncrementalRendering(): void;
    /**
     * Drive {@link renderNext} automatically from scrolling: render the next batch whenever the user scrolls
     * within ~1.5 viewports of the not-yet-rendered edge (the page bottom for the endless vertical format,
     * the right edge for RenderSingleHorizontalStaffline). Renders the first batch immediately if none has
     * been rendered yet, then keeps appending as the user scrolls, and detaches itself once the whole sheet
     * is rendered. Re-enabling replaces any previous listener; reset/render/load also detach it.
     *
     * @param options batch size (as {@link renderNext}) plus an optional `scrollElement` -- the element whose
     *  scrolling drives loading. Defaults to the OSMD container for a single horizontal staffline (it scrolls
     *  horizontally) and to `window` otherwise (the page scrolls vertically).
     */
    enableIncrementalRenderingOnScroll(options?: IRenderNextOptions & {
        scrollElement?: HTMLElement | Window;
    }): void;
    /** Stop driving {@link renderNext} from scrolling (see {@link enableIncrementalRenderingOnScroll}). */
    disableIncrementalRenderingOnScroll(): void;
    /** Start a fresh incremental session: save the draw-measure range the lazy layout mutates (restored on
     *  reset). Lazy needs the greedy, forward-stable builder so earlier systems keep their position as the
     *  prefix grows -- public OSMD is greedy-only, so that is already in effect (nothing to force). */
    private beginIncrementalRendering;
    /** Number of VISUAL measures in the source-measure range [from, toExcl): a multi-rest renders as one
     *  GraphicalMeasure when RenderMultipleRestMeasures collapses it, so it counts as one. */
    private visualMeasureCount;
    /** Source-measure index (inclusive) at which a batch of `visualCount` visual measures starting at source
     *  index `from` ends -- i.e. the toMeasureIndex to render this batch (multi-rests collapsed; see above). */
    private visualBatchEndIndex;
    /**
     * Lazy rendering (EngravingRules.LazyConsistentGraphic). Lays out the WHOLE prefix
     * [0..toMeasureIndex] into ONE globally-consistent graphic and draws only the systems that became
     * stable since the previous batch, appending them into the shared backend below what's already drawn.
     *
     * There is no per-batch vertical offset or seam-distance
     * computation: the real Y-layout already stacks and crisp-snaps every system at its final absolute
     * position, and the title space / first-system instrument names fall out of laying the prefix from
     * measure 0 each time. The greedy builder is forward-stable -- every system except the LAST of a
     * prefix is *usually* forward-stable (an interior system keeps its position as the prefix grows), so
     * we skip drawing the already-drawn systems above and DEFER the last system of a non-final batch (it
     * is unstretched and shifts once it becomes an interior, stretched system next batch). But this is NOT
     * universal -- some scores re-position earlier systems by several px as later systems are added -- so
     * each batch first VERIFIES the drawn systems against their drawn Y (lazyDrawnSystemY) and, if any
     * moved, redraws the whole drawn range at the corrected positions (reconciliation). In the common
     * (stable) case nothing is redrawn. See export/inspect_prefix_stability.mjs / inspect_optionb_drawpos.mjs.
     *
     * @returns the source-measure index at which the next batch should continue (past the last drawn system).
     */
    private renderAppendGrowing;
    /**
     * Lazy rendering for RenderSingleHorizontalStaffline (one continuous staffline, horizontal scroll).
     * Lays out the whole prefix [0..toMeasureIndex] as ONE system (greedy builder at SheetMaximumWidth so it
     * never breaks) and draws only the measures (and spanning elements) whose right edge first entered the
     * drawn frontier this batch -- the single SVG grows to the RIGHT (and taller if later measures are tall).
     * Measure X and Y are forward-stable here, so unlike the vertical path there is no reconciliation and no
     * deferred last unit: every batch simply appends. SVG backend only (Canvas keeps its existing width cap).
     * @returns the source-measure index at which the next batch should continue.
     */
    private renderAppendGrowingHorizontal;
    protected createOrRefreshRenderBackend(): void;
    exportSVG(): void;
    /** States whether the render() function can be safely called. */
    IsReadyToRender(): boolean;
    /** Clears what OSMD has drawn on its canvas. */
    clear(): void;
    /** Set OSMD rendering options using an IOSMDOptions object.
     *  Can be called during runtime. Also called by constructor.
     *  For example, setOptions({autoResize: false}) will disable autoResize even during runtime.
     */
    setOptions(options: IOSMDOptions): void;
    setColoringMode(options: IOSMDOptions): void;
    /**
     * Sets the logging level for this OSMD instance. By default, this is set to `warn`.
     *
     * @param: content can be `trace`, `debug`, `info`, `warn` or `error`.
     */
    setLogLevel(level: string): void;
    getLogLevel(): number;
    /**
     * Initialize this object to default values
     * FIXME: Probably unnecessary
     */
    protected reset(): void;
    /**
     * Attach the appropriate handler to the window.onResize event
     */
    protected autoResize(): void;
    /** Re-render and scroll back to previous scroll bar y position in percent.
     * If the document keeps the same height/length, the scroll bar position will basically be unchanged.
     * For example, if you scroll to the bottom of the page, resize by one pixel (or enable dark mode) and call this,
     *   for the human eye there will be no detectable scrolling or change in the scroll position at all.
     * If you just call render() instead of renderAndScrollBack(),
     *   it will scroll you back to the top of the page, even if you were scrolled to the bottom before. */
    renderAndScrollBack(): void;
    /**
     * Helper function for managing window's onResize events
     * @param startCallback is the function called when resizing starts
     * @param endCallback is the function called when resizing (kind-of) ends
     */
    protected handleResize(startCallback: () => void, endCallback: () => void): void;
    /** Enable or disable (hide) the cursor.
     * @param enable whether to enable (true) or disable (false) the cursor
     */
    enableOrDisableCursors(enable: boolean): void;
    createBackend(type: BackendType, page: GraphicalMusicPage, idOverride?: string): VexFlowBackend;
    /** Standard page format options like A4 or Letter, in portrait and landscape. E.g. PageFormatStandards["A4_P"] or PageFormatStandards["Letter_L"]. */
    static PageFormatStandards: {
        [type: string]: PageFormat;
    };
    static StringToPageFormat(pageFormatString: string): PageFormat;
    /** Sets page format by string. Used by setOptions({pageFormat: "A4_P"}) for example. */
    setPageFormat(formatId: string): void;
    setCustomPageFormat(width: number, height: number): void;
    set DrawSkyLine(value: boolean);
    get DrawSkyLine(): boolean;
    set DrawBottomLine(value: boolean);
    get DrawBottomLine(): boolean;
    set DrawBoundingBox(value: string);
    get DrawBoundingBox(): string;
    setDrawBoundingBox(value: string, render?: boolean): void;
    get AutoResizeEnabled(): boolean;
    set AutoResizeEnabled(value: boolean);
    get Zoom(): number;
    set Zoom(value: number);
    set FollowCursor(value: boolean);
    get FollowCursor(): boolean;
    set TransposeCalculator(calculator: ITransposeCalculator);
    get TransposeCalculator(): ITransposeCalculator;
    get Sheet(): MusicSheet;
    get Drawer(): VexFlowMusicSheetDrawer;
    get GraphicSheet(): GraphicalMusicSheet;
    get DrawingParameters(): DrawingParameters;
    get EngravingRules(): EngravingRules;
    /** Returns the version of OSMD this object is built from (the version you are using). */
    get Version(): string;
}
/** Options for {@link OpenSheetMusicDisplay.renderNext} (incremental, "system by system" rendering). */
export interface IRenderNextOptions {
    /** Target number of visual measures to advance per batch (a multi-rest counts as ONE -- it renders as a
     *  single GraphicalMeasure). Defaults to 8. This is a TARGET, not an exact count: a batch always ends on a
     *  whole music-system (line) boundary, so the system the measure count lands inside is deferred to the next
     *  batch (and a batch always renders at least one whole system). The measures actually drawn may therefore
     *  be somewhat fewer or more than this -- e.g. 8 lands part-way into a system, so only the complete systems
     *  before it are drawn; or if 8 doesn't fill one system, the layout extends until one whole system is ready.
     *  To see what was actually rendered, read the returned {@link IRenderNextResult}: `renderedMeasures` (total
     *  visual measures drawn so far, cumulative) and `lastRenderedMeasure` (the GraphicalMeasures at the last
     *  drawn measure position). Ignored when `systems` is set (and applicable). */
    measures?: number;
    /** How many whole music systems (lines) to render in this batch, instead of advancing by `measures`.
     *  Each batch then ends exactly on a system boundary. Takes precedence over `measures` when > 0.
     *  VERTICAL ONLY: a single horizontal staffline (RenderSingleHorizontalStaffline) is one system, so
     *  `systems` is ignored there and rendering falls back to `measures`. */
    systems?: number;
}
/** Progress returned by {@link OpenSheetMusicDisplay.renderNext}. Measure counts are visual (a multi-rest
 *  counts as one). */
export interface IRenderNextResult {
    /** True once the last measure of the sheet has been rendered -- no more batches remain. */
    done: boolean;
    /** Visual measures rendered so far, cumulative across batches. */
    renderedMeasures: number;
    /** Total visual measures in the sheet. */
    totalMeasures: number;
    /** The last measure position rendered so far (highest measure index): all its GraphicalMeasures, one per
     *  staff/instrument (e.g. 3 for a voice + piano score). Empty if nothing has been rendered yet. They
     *  share one source measure -- reach it and its number/index via any element's `.parentSourceMeasure` and
     *  `.parentSourceMeasure.measureListIndex` (0-based). */
    lastRenderedMeasure: GraphicalMeasure[];
    /** The next measure position not yet rendered (the frontier): all its GraphicalMeasures (one per staff),
     *  or empty once `done`. Same `.parentSourceMeasure` / `.parentSourceMeasure.measureListIndex` accessors. */
    nextUnrenderedMeasure: GraphicalMeasure[];
}
