package view;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

//esta clase sirve para generar los sprites de la interfaz grafica
public final class Assets {
    private Assets() {
    }

    private static String rutaCompleta(String ruta) {
        return ruta.startsWith("/") ? ruta : "/assets/" + ruta;
    }

    /** true si el archivo existe en /assets. Lo usa el fondo para omitir sprites que falten. */
    public static boolean existe(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return false;
        }
        return Assets.class.getResource(rutaCompleta(ruta)) != null;
    }

    public static ImageIcon cargar(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            throw new IllegalArgumentException("La ruta del asset no puede estar vacía");
        }

        URL recurso = Assets.class.getResource(rutaCompleta(ruta));
        if (recurso == null) {
            throw new IllegalStateException("No se encontró el asset: " + ruta);
        }
        return new ImageIcon(recurso);
    }

    public static ImageIcon cargarEscalada(String ruta, int tamaño) {
        if (tamaño <= 0) {
            throw new IllegalArgumentException("El tamaño del asset debe ser positivo");
        }

        ImageIcon original = cargar(ruta);
        Image imagen = original.getImage().getScaledInstance(
                tamaño,
                tamaño,
                Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }
}