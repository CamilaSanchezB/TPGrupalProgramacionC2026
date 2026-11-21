package view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Apariencia compartida por todas las ventanas: Look and Feel, paleta y fuentes.
 * Para que todas se vean igual y para cambiar el aspecto de la aplicación entera tocando un solo archivo.
 */
public final class ConfigView {

	// ---- Paleta oscura (la misma familia de colores que usan MainPanel y ResultadoPanel) ----
	public static final Color FONDO = new Color(13, 17, 23);
	public static final Color SUPERFICIE = new Color(22, 27, 34);
	public static final Color BORDE = new Color(48, 54, 61);

	/** Azul para las acciones principales (botones). */
	public static final Color ACENTO = new Color(31, 111, 235);
	/** Celeste para títulos y detalles. */
	public static final Color ACENTO_CIAN = new Color(88, 166, 255);
	public static final Color ACENTO_CALIDO = new Color(210, 153, 34);

	public static final Color TITULO = new Color(230, 237, 243);
	public static final Color TEXTO_CLARO = new Color(230, 237, 243);
	public static final Color TEXTO_SECUNDARIO = new Color(139, 148, 158);
	public static final Color TEXTO_BANNER = new Color(139, 148, 158);

	public static final Color ERROR = new Color(248, 81, 73);
	public static final Color AVISO = new Color(63, 185, 80);

	// ---- Colores que usa TripulantesView (no se tocan para no romper esa pantalla) ----
	public static final Color FONDO_COMANDO = new Color(13, 17, 23);
	public static final Color TARJETA_COMANDO = new Color(30, 41, 55);
	public static final Color BORDE_COMANDO = new Color(59, 130, 246);
	public static final Color TEXTO_SECUNDARIO_COMANDO = new Color(139, 148, 158);
	public static final Color BOTON_SECUNDARIO_COMANDO = new Color(33, 38, 45);

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
			UIManager.setLookAndFeel(new FlatDarkLaf());
			// Los redondeos van despues de instalar el Look and Feel, no antes.
			UIManager.put("Button.arc", 12);
			UIManager.put("Component.arc", 10);
			UIManager.put("TextComponent.arc", 10);
			UIManager.put("Component.focusWidth", 1);
			UIManager.put("Component.accentColor", ACENTO);

			// Campos de texto: fondo más oscuro que la tarjeta, así "se hunden" y se distinguen.
			UIManager.put("TextField.background", FONDO);
			UIManager.put("PasswordField.background", FONDO);
			UIManager.put("Component.borderColor", BORDE);
			UIManager.put("Component.focusedBorderColor", ACENTO_CIAN);
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("No se pudo aplicar FlatLaf, sigue el Look and Feel por defecto: "
					+ e.getMessage());
		}
	}

	public static void primario(JButton boton) {
		boton.setBackground(ACENTO);
		boton.setForeground(Color.WHITE);
	}

	/** Botón de segunda importancia: fondo de tarjeta y borde celeste. */
	public static void secundario(JButton boton) {
		boton.setOpaque(true);
		boton.setBackground(SUPERFICIE);
		boton.setForeground(ACENTO_CIAN);
		boton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ACENTO_CIAN),
				BorderFactory.createEmptyBorder(6, 12, 6, 12)));
	}
}