package com.digitarra.gestion_partituras;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class GeneradorPDF {

    private final Context context;

    public GeneradorPDF(Context context) {
        this.context = context;
    }

    public String obtenerPDF(String rutaXML, String rutaPDF) {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] exito = {false};

        // Forzar normalización de la ruta de salida (convertir \ en /)
        final String rutaPDFNormalizada = rutaPDF.replace("\\", "/");
        final String rutaXMLNormalizada = rutaXML.replace("\\", "/");

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                // 1. Leer contenido del XML
                byte[] xmlBytes = Files.readAllBytes(Paths.get(rutaXMLNormalizada));
                String xmlContent = new String(xmlBytes, StandardCharsets.UTF_8);

                String xmlEscapado = xmlContent
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                        .replace("\r", "");

                // 2. Configurar WebView
                WebView webView = new WebView(context);
                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setAllowFileAccess(true);

                // 3. Registrar UN SOLO puente Javascript que escriba en rutaPDF
                webView.addJavascriptInterface(new Object() {
                    @JavascriptInterface
                    public void guardarPdf(String base64Data, String outputFileName) {
                        if (base64Data != null && !base64Data.isEmpty()) {
                            try {
                                byte[] pdfBytes = Base64.decode(base64Data, Base64.DEFAULT);
                                File archivoPDF = new File(rutaPDFNormalizada);

                                if (archivoPDF.getParentFile() != null) {
                                    archivoPDF.getParentFile().mkdirs();
                                }

                                try (FileOutputStream fos = new FileOutputStream(archivoPDF)) {
                                    fos.write(pdfBytes);
                                    fos.flush();
                                }

                                exito[0] = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // Liberar el hilo de espera en cualquier caso
                        latch.countDown();
                    }
                }, "AndroidBridge");

                // 4. Cargar la página e inyectar el script
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        String script = String.format("convertMusicXmlToPdfAndroid('%s', '%s');",
                                xmlEscapado, new File(rutaPDFNormalizada).getName());
                        webView.evaluateJavascript(script, null);
                    }
                });

                webView.loadUrl("file:///android_asset/js/Index.html");

            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });

        try {
            // Esperar la respuesta del puente JS
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return exito[0] ? rutaPDFNormalizada : null;
    }
}