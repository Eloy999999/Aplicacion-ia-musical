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

        final String rutaPDFNormalizada = rutaPDF.replace("\\", "/");
        final String rutaXMLNormalizada = rutaXML.replace("\\", "/");

        new Handler(Looper.getMainLooper()).post(() -> { // Se ejecuta aqui con el hilo principal porque el WebView lo requiere
            try {
                // Leer y apuntar contenido del xml
                byte[] xmlBytes = Files.readAllBytes(Paths.get(rutaXMLNormalizada));
                String xmlContent = new String(xmlBytes, StandardCharsets.UTF_8);

                // Escapar caracteres para JavaScript en xmlContent
                String xmlEscapado = xmlContent
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                        .replace("\r", "");

                // Configurar WebView
                WebView webView = new WebView(context);
                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setAllowFileAccess(true);

                webView.clearCache(true);
                settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

                webView.addJavascriptInterface(new Object() { // Define el puente con js
                    @JavascriptInterface
                    public void guardarPdf(String base64Data, String outputFileName) { // lo llama Index.html para ya regresar
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
                        latch.countDown(); // Soltar latch, el WebView finaliza
                    }
                }, "AndroidBridge");

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) { // Una vez cargado Index.html, lo ejecuta
                        String script = String.format("convertMusicXmlToPdfAndroid('%s', '%s');",
                                xmlEscapado, new File(rutaPDFNormalizada).getName());
                        webView.evaluateJavascript(script, null);
                    }
                });

                webView.loadUrl("file:///android_asset/js/Index.html"); // Cargar html con js

            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });

        try {
            latch.await(); // Esperar que se termine de escribir el PDF en disco
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return exito[0] ? rutaPDFNormalizada : null;
    }
}