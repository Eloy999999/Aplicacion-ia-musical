const fs = require('fs');
const path = require('path');
const puppeteer = require('puppeteer-core');

const CHROME_PATH = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const EDGE_PATH = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';

function getBrowserPath() {
    if (fs.existsSync(CHROME_PATH)) return CHROME_PATH;
    if (fs.existsSync(EDGE_PATH)) return EDGE_PATH;
    throw new Error('No se encontró ni Chrome ni Edge instalado en el PC.');
}

async function convertMusicXmlToPdf(xmlPath, outputPath) {
    const absoluteXmlPath = path.resolve(__dirname, xmlPath);
    const absoluteOutputPath = path.resolve(__dirname, outputPath);

    if (!fs.existsSync(absoluteXmlPath)) {
        throw new Error(`El archivo MusicXML no existe en: ${absoluteXmlPath}`);
    }

    const xmlContent = fs.readFileSync(absoluteXmlPath, 'utf-8');

    const osmdScriptPath = require.resolve('opensheetmusicdisplay/build/opensheetmusicdisplay.min.js');
    const osmdScriptContent = fs.readFileSync(osmdScriptPath, 'utf-8');

    const browser = await puppeteer.launch({
        executablePath: getBrowserPath(),
        headless: true
    });

    const page = await browser.newPage();

    const htmlContent = `
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
            body { margin: 0; padding: 20px; background: #ffffff; }
            #osmd-container { width: 100%; }
            </style>
        </head>
        <body>
            <div id="osmd-container"></div>
            <script>
            ${osmdScriptContent}
            </script>
            <script>
                async function renderXML(xmlString) {
                const osmd = new opensheetmusicdisplay.OpenSheetMusicDisplay("osmd-container", {
                    autoResize: false,
                    drawTitle: false,
                    drawComposer: false,
                    drawFingerings: true,
                    fingeringPosition: "left", // OSMD acepta "left", "right", "above", "below" como string
                    drawStringNumbers: true,
                    fingeringInsideStaff: false
                });

                await osmd.load(xmlString);
                osmd.render();
                }
            </script>
        </body>
        </html>
    `;

    await page.setContent(htmlContent, { waitUntil: 'domcontentloaded' });

    await page.evaluate(async (xml) => {
        await window.renderXML(xml);
    }, xmlContent);

    const outputDir = path.dirname(absoluteOutputPath);
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
    }

    await page.pdf({
        path: absoluteOutputPath,
        format: 'A4',
        printBackground: true,
        margin: { top: '15mm', bottom: '15mm', left: '10mm', right: '10mm' }
    });

    await browser.close();
    console.log(`PDF generado correctamente en: ${absoluteOutputPath}`);
    }

    const inputXml = '../Python/acordes_digitado.xml';
    const outputPdf = '../Biblioteca/acordes_digitado.pdf';

    convertMusicXmlToPdf(inputXml, outputPdf).catch((err) => {
    console.error('sError durante la conversión:', err.message);
    });