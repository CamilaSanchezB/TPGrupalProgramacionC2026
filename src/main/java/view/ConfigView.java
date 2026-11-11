package view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * Apariencia compartida por todas las ventanas: Look and Feel, paleta y fuentes.
 * Para que todas se vean igual y para cambiar el aspecto de la aplicación entera tocando un solo archivo.
 */
public final class ConfigView {

	/** Azul de acento para las acciones principales. */
	public static final Color ACENTO = new Color(2, 132, 199);
	public static final Color TITULO = new Color(15, 23, 42);
	public static final Color ERROR = new Color(190, 18, 60);
	public static final Color AVISO = new Color(21, 128, 61);

	public static final String FAMILIA = "Segoe UI";
	public static final Font FUENTE_TITULO_GRANDE = new Font(FAMILIA, Font.BOLD, 20);
	public static final Font FUENTE_TITULO = new Font(FAMILIA, Font.BOLD, 16);
	public static final Font FUENTE_ETIQUETA = new Font(FAMILIA, Font.PLAIN, 13);
	public static final Font FUENTE_MENSAJE = new Font(FAMILIA, Font.PLAIN, 12);
	public static final Font FUENTE_DETALLE = new Font("Consolas", Font.PLAIN, 13);

	private static boolean aplicado = false;

	private ConfigView() {
	}

	/**
	 * Instala el Look and Feel. Cada vista lo llama como primera línea de su
	 * initialize(), antes de crear cualquier componente: los que se construyen
	 * antes se quedarían con el aspecto viejo.
	 *
	 * <b>POST</b>
	 * - el Look and Feel queda instalado; llamarlo de nuevo no hace nada
	 */
	public static void aplicar() {
		if (aplicado) {
			return;
		}
		aplicado = true;
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
			// Los redondeos van despues de instalar el Look and Feel, no antes.
			UIManager.put("Button.arc", 12);
			UIManager.put("Component.arc", 10);
			UIManager.put("TextComponent.arc", 10);
			UIManager.put("Component.focusWidth", 1);
			UIManager.put("Component.accentColor", ACENTO);
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("No se pudo aplicar FlatLaf, sigue el Look and Feel por defecto: "
					+ e.getMessage());
		}
	}
}
