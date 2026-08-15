val webView = findViewById<WebView>(R.id.webView)

// Configurar el WebView para permitir ejecución local
webView.settings.javaScriptEnabled = true
webView.settings.allowFileAccess = true

// Cargar el HTML local que guardamos en la carpeta js con opensheetmusicdisplay
// webView.loadUrl("file:///android_asset/index.html")
webView.loadUrl("file:///js/index.html")

// Función para enviar el XML que se leyo del telefono movil
fun mostrarPartituraDesdeArchivoLocal(xmlString: String) {
    // Escapamos saltos de línea y comillas para enviarlo de forma segura a JS
    val cleanXml = xmlString
        .replace("\\", "\\\\")
        .replace("`", "\\`")
        .replace("\n", " ")
        .replace("\r", " ")

    // Ejecutamos la función JavaScript definida en el index.html
    webView.post {
        webView.evaluateJavascript("renderizarXmlLocal(`$cleanXml`);", null)
    }
}