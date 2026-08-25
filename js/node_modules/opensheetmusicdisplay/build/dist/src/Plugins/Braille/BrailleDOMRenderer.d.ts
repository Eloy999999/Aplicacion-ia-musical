import { BrailleOutput } from "./BrailleConverter";
/**
 * Creates DOM elements to display braille music output.
 * Renders the braille text into a container element, optionally with
 * a debug/translation view showing what each braille character means.
 */
export declare class BrailleDOMRenderer {
    /**
     * Create a DOM element containing the braille output.
     *
     * @param output The BrailleOutput from BrailleConverter.convert()
     * @param debugMode Whether to show debug/translation annotations
     * @returns An HTMLElement ready to be appended to the page
     */
    render(output: BrailleOutput, debugMode?: boolean): HTMLElement;
    /**
     * Render the debug/translation view as a paragraph showing each braille
     * element with its meaning.
     */
    private renderDebugView;
}
