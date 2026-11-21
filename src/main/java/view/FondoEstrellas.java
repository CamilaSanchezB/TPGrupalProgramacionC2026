package view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Panel con fondo espacial animado: estrellas que parpadean y bajan despacio, y
 * naves del propio juego que cruzan la pantalla de vez en cuando.
 * Las pantallas que quieran este fondo extienden esta clase en lugar de JPanel
 * y agregan sus componentes encima como siempre.
 */
@SuppressWarnings("serial")
public class FondoEstrellas extends JPanel {

	private static final int CANTIDAD = 90;
	private static final int MS_POR_CUADRO = 40; // ~25 cuadros por segundo

	// ---- Naves voladoras ----
	/** Sprites de /assets que pueden volar. Si alguno no existe, se omite. */
	private static final String[] ARCHIVOS_NAVES = {
			"combate.png", "carguera.png", "exploradora.png", "naveEnemiga.png" };
	/** Tres "profundidades": chica/lenta/tenue (lejos) hasta grande/rápida/nítida (cerca). */
	private static final int[] TAMANOS = { 40, 56, 72 };
	private static final double[] VELOCIDADES = { 45, 70, 100 }; // pixeles por segundo
	private static final float[] OPACIDADES = { 0.55f, 0.70f, 0.85f };
	/** Cuántas naves pueden estar volando a la vez. */
	private static final int CANTIDAD_NAVES = 4;
	/**
	 * Hacia dónde "mira" el dibujo original. PI/2 = mira hacia ARRIBA.
	 * Si en pantalla ves las naves de costado o de espaldas, cambiá este valor:
	 * 0 = el dibujo mira a la derecha, Math.PI = mira a la izquierda, -Math.PI/2 = mira abajo.
	 */
	private static final double OFFSET_SPRITE = Math.PI / 2;

	private static List<Image[]> sprites; // un arreglo de 3 tamaños por cada tipo de nave

	// Cada estrella se guarda con valores entre 0 y 1, así sirve para cualquier tamaño de ventana.
	private final double[] x = new double[CANTIDAD];
	private final double[] y = new double[CANTIDAD];
	private final double[] velocidad = new double[CANTIDAD]; // fracción de pantalla por segundo
	private final double[] fase = new double[CANTIDAD];      // desfasa el parpadeo de cada una
	private final int[] lado = new int[CANTIDAD];            // 1, 2 o 3 pixeles

	private final List<NaveVoladora> naves = new ArrayList<NaveVoladora>();
	private final Random azar = new Random();
	private long ultimoCuadro = System.nanoTime();

	private final long inicio = System.nanoTime();
	private final Timer reloj;

	/** Estado de una nave que cruza la pantalla. */
	private static class NaveVoladora {
		boolean activa;
		double espera; // segundos que faltan para que aparezca
		int tipo;      // índice en "sprites"
		int variante;  // índice de profundidad (0, 1 o 2)
		double x, y, vx, vy, angulo;
	}

	public FondoEstrellas() {
		this(new BorderLayout());
	}

	public FondoEstrellas(LayoutManager layout) {
		super(layout);
		setOpaque(true);
		setBackground(ConfigView.FONDO);

		Random fijo = new Random(7); // semilla fija: el cielo de estrellas es siempre el mismo
		for (int i = 0; i < CANTIDAD; i++) {
			x[i] = fijo.nextDouble();
			y[i] = fijo.nextDouble();
			lado[i] = 1 + fijo.nextInt(3);
			// Las estrellas grandes se mueven más rápido: da sensación de profundidad.
			velocidad[i] = 0.004 + lado[i] * 0.004 + fijo.nextDouble() * 0.004;
			fase[i] = fijo.nextDouble() * Math.PI * 2;
		}

		if (!cargarSprites().isEmpty()) {
			for (int i = 0; i < CANTIDAD_NAVES; i++) {
				NaveVoladora nave = new NaveVoladora();
				nave.espera = 0.5 + azar.nextDouble() * 4; // aparecen escalonadas
				naves.add(nave);
			}
		}

		reloj = new Timer(MS_POR_CUADRO, e -> {
			long ahora = System.nanoTime();
			// Tope de 0.1 s para que, si la ventana estuvo oculta, las naves no den un salto.
			double dt = Math.min((ahora - ultimoCuadro) / 1e9, 0.1);
			ultimoCuadro = ahora;
			if (isShowing()) { // no gastamos CPU si esta pantalla está oculta
				moverNaves(dt);
				repaint();
			}
		});
	}

	/** Carga una sola vez los sprites (compartidos por todas las pantallas). */
	private static synchronized List<Image[]> cargarSprites() {
		if (sprites == null) {
			sprites = new ArrayList<Image[]>();
			for (String archivo : ARCHIVOS_NAVES) {
				if (!Assets.existe(archivo)) {
					continue;
				}
				try {
					Image[] variantes = new Image[TAMANOS.length];
					for (int i = 0; i < TAMANOS.length; i++) {
						variantes[i] = Assets.cargarEscalada(archivo, TAMANOS[i]).getImage();
					}
					sprites.add(variantes);
				} catch (RuntimeException ex) {
					System.err.println("No se pudo cargar la nave de fondo " + archivo);
				}
			}
		}
		return sprites;
	}

	private void moverNaves(double dt) {
		int w = getWidth();
		int h = getHeight();
		if (w <= 0 || h <= 0) {
			return;
		}
		for (NaveVoladora nave : naves) {
			if (!nave.activa) {
				nave.espera -= dt;
				if (nave.espera <= 0) {
					lanzar(nave, w, h);
				}
				continue;
			}
			nave.x += nave.vx * dt;
			nave.y += nave.vy * dt;
			int margen = 2 * TAMANOS[nave.variante];
			if (nave.x < -margen || nave.x > w + margen || nave.y < -margen || nave.y > h + margen) {
				nave.activa = false;                       // ya salió de pantalla
				nave.espera = 1.5 + azar.nextDouble() * 4; // descansa un rato y vuelve a salir
			}
		}
	}

	/** Hace aparecer la nave por un borde al azar, apuntando a un punto del borde opuesto. */
	private void lanzar(NaveVoladora nave, int w, int h) {
		nave.tipo = azar.nextInt(cargarSprites().size());
		nave.variante = azar.nextInt(TAMANOS.length);
		int m = TAMANOS[nave.variante];

		double x0, y0, x1, y1;
		switch (azar.nextInt(4)) {
		case 0: // de izquierda a derecha
			x0 = -m; y0 = azar.nextDouble() * h;
			x1 = w + m; y1 = azar.nextDouble() * h;
			break;
		case 1: // de derecha a izquierda
			x0 = w + m; y0 = azar.nextDouble() * h;
			x1 = -m; y1 = azar.nextDouble() * h;
			break;
		case 2: // de arriba hacia abajo
			x0 = azar.nextDouble() * w; y0 = -m;
			x1 = azar.nextDouble() * w; y1 = h + m;
			break;
		default: // de abajo hacia arriba
			x0 = azar.nextDouble() * w; y0 = h + m;
			x1 = azar.nextDouble() * w; y1 = -m;
			break;
		}

		double dx = x1 - x0;
		double dy = y1 - y0;
		double largo = Math.hypot(dx, dy);
		double rapidez = VELOCIDADES[nave.variante];
		nave.x = x0;
		nave.y = y0;
		nave.vx = dx / largo * rapidez;
		nave.vy = dy / largo * rapidez;
		nave.angulo = Math.atan2(dy, dx); // hacia dónde viaja
		nave.activa = true;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		ultimoCuadro = System.nanoTime();
		reloj.start();
	}

	@Override
	public void removeNotify() {
		reloj.stop();
		super.removeNotify();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // pinta el fondo liso
		Graphics2D g2 = (Graphics2D) g.create();
		int w = getWidth();
		int h = getHeight();
		double t = (System.nanoTime() - inicio) / 1e9;

		// Resplandor azul muy suave en el centro (le da profundidad al fondo).
		float radio = Math.max(w, h) * 0.6f;
		g2.setPaint(new RadialGradientPaint(
				w / 2f, h / 2f, radio,
				new float[] { 0f, 1f },
				new Color[] { new Color(31, 111, 235, 34), new Color(31, 111, 235, 0) }));
		g2.fillRect(0, 0, w, h);

		for (int i = 0; i < CANTIDAD; i++) {
			double py = (y[i] + velocidad[i] * t) % 1.0; // al llegar abajo reaparece arriba
			double brillo = 0.5 + 0.5 * Math.sin(t * 1.6 + fase[i]);
			int alfa = (int) (50 + 150 * brillo);
			g2.setColor(new Color(200, 225, 255, alfa));
			g2.fillRect((int) (x[i] * w), (int) (py * h), lado[i], lado[i]);
		}

		pintarNaves(g2);
		g2.dispose();
	}

	private void pintarNaves(Graphics2D g2) {
		if (naves.isEmpty()) {
			return;
		}
		List<Image[]> tipos = cargarSprites();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		for (NaveVoladora nave : naves) {
			if (!nave.activa) {
				continue;
			}
			int tam = TAMANOS[nave.variante];
			float opacidad = OPACIDADES[nave.variante];
			double ux = Math.cos(nave.angulo);
			double uy = Math.sin(nave.angulo);

			// Estela: cinco cuadraditos celestes detrás de la nave que se van apagando.
			for (int k = 1; k <= 5; k++) {
				float a = opacidad * (1f - k / 6f) * 0.8f;
				g2.setColor(new Color(88, 166, 255, (int) (255 * a)));
				int px = (int) (nave.x - ux * (tam / 2.0 + k * 6));
				int py = (int) (nave.y - uy * (tam / 2.0 + k * 6));
				g2.fillRect(px - 1, py - 1, 3, 3);
			}

			// La nave, girada hacia donde viaja.
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
			AffineTransform original = g2.getTransform();
			g2.translate(nave.x, nave.y);
			g2.rotate(nave.angulo + OFFSET_SPRITE);
			g2.drawImage(tipos.get(nave.tipo)[nave.variante], -tam / 2, -tam / 2, null);
			g2.setTransform(original);
			g2.setComposite(AlphaComposite.SrcOver);
		}
	}
}