import android.webkit.JavascriptInterface;

public class WebAppInterface {
    @JavascriptInterface
    public void guardarPdf(String base64Data, String nombre) {
        byte[] pdfBytes = Base64.decode(base64Data, Base64.DEFAULT);
        File carpeta = new File(context.getExternalFilesDir(null), "Biblioteca");
        if (!carpeta.exists()) carpeta.mkdirs();

        File archivo = new File(carpeta, nombre);
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}