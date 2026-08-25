// Carga dinámica de html2pdf
async function loadHtml2Pdf() {
  if (window.html2pdf) return;
  return new Promise((resolve) => {
    const script = document.createElement('script');
    script.src = './html2pdf.bundle.min.js';
    script.onload = resolve;
    document.head.appendChild(script);
  });
}

async function convertMusicXmlToPdfAndroid(xmlString, outputFileName = 'partitura.pdf') {
  await loadHtml2Pdf();

  const container = document.createElement('div');
  container.style.width = '800px';
  container.style.background = '#ffffff';
  document.body.appendChild(container);

  try {
    const osmd = new opensheetmusicdisplay.OpenSheetMusicDisplay(container, {
      autoResize: false,
      drawTitle: false,
      drawComposer: false,
      drawFingerings: true,
      fingeringPosition: "left",
      drawStringNumbers: true,
      fingeringInsideStaff: false
    });

    await osmd.load(xmlString);
    osmd.render();

    const options = {
      margin: [15, 10, 15, 10],
      filename: outputFileName,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    // Esperar a que html2pdf procese y ordene la descarga
    await html2pdf().set(options).from(container).save();

    // Notificar éxito a Java
    if (window.AndroidBridge && window.AndroidBridge.onPdfFinalizado) {
      window.AndroidBridge.onPdfFinalizado(true);
    }
  } catch (error) {
    console.error('Error al generar el PDF:', error);
    if (window.AndroidBridge && window.AndroidBridge.onPdfFinalizado) {
      window.AndroidBridge.onPdfFinalizado(false);
    }
  } finally {
    document.body.removeChild(container);
  }
}