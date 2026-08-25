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

  // 1. Crear contenedor temporal en el DOM
  const container = document.createElement('div');
  container.style.width = '800px';
  container.style.background = '#ffffff';
  document.body.appendChild(container);

  // 2. Renderizar OSMD
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

  // 3. Opciones de exportación PDF
  const options = {
    margin: [15, 10, 15, 10],
    filename: outputFileName,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { scale: 2, useCORS: true },
    jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
  };

  // 4. Generar el PDF y obtener la cadena Base64
  try {
    const pdfBase64 = await html2pdf()
      .set(options)
      .from(container)
      .outputPdf('datauristring'); // Genera un Data URI: "data:application/pdf;base64,..."

    // Extraemos solo el contenido Base64 quitando el encabezado "data:application/pdf;base64,"
    const base64Data = pdfBase64.split(',')[1];

    // Enviamos los datos a la interfaz Java de Android
    if (window.AndroidBridge && window.AndroidBridge.guardarPdf) {
      window.AndroidBridge.guardarPdf(base64Data, outputFileName);
      console.log('PDF enviado a Android con éxito.');
    } else {
      console.error('AndroidBridge no disponible');
    }
  } catch (error) {
    console.error('Error al generar el PDF:', error);
  } finally {
    // 5. Limpieza del DOM
    document.body.removeChild(container);
  }
}