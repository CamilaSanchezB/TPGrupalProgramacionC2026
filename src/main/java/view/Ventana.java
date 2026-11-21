package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Única ventana de la aplicación. Muestra una pantalla (JPanel) a la vez. */
public class Ventana extends JFrame {
    public static final String INICIAR_SESION = "iniciarSesion";
    public static final String CREAR_NAVE = "crearNave";
    public static final String TABLERO = "tablero";
    
    public static final String SELECTOR = "selector", MAIN = "main",
    TRIPULANTES = "tripulantes", RESULTADO = "resultado", LOGIN = "login";
    private final java.util.Map<String, JPanel> pantallas = new java.util.HashMap<>();


    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);

    // ---- Transición entre pantallas (fundido a negro y de vuelta) ----
    /** Cuánto cambia la opacidad del velo en cada cuadro (15 ms). 0.12 => ~125 ms por mitad. */
    private static final float PASO_FUNDIDO = 0.12f;

    private float opacidadVelo = 0f;
    private boolean oscureciendo = false;
    private String destino;
    private Timer animacion;

    /** Capa transparente sobre toda la ventana: pinta el color de fondo con opacidad variable. */
    private final JPanel velo = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Color base = ConfigView.FONDO;
            g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(),
                    Math.round(opacidadVelo * 255)));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };

    public Ventana() {
        super("TP_java");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(contenedor);
        setSize(900, 700);
        setLocationRelativeTo(null);

        velo.setOpaque(false);
        // Escuchar el mouse (sin hacer nada) hace que el velo "se coma" los clics mientras dura el fundido.
        velo.addMouseListener(new MouseAdapter() { });
        velo.addMouseMotionListener(new MouseAdapter() { });
        setGlassPane(velo);
        velo.setVisible(false);
    }

    /** Registra una pantalla con un nombre para poder mostrarla después. */
    public void agregarPantalla(String nombre, JPanel pantalla) {
        JPanel vieja = pantallas.put(nombre, pantalla);
        if (vieja != null) contenedor.remove(vieja);
        contenedor.add(pantalla, nombre);
        contenedor.revalidate();
    }

    /**
     * Muestra la pantalla registrada con ese nombre, con un fundido corto.
     * La primera vez (con la ventana todavía oculta) cambia directo, sin animar.
     * Si se pide otra pantalla en pleno fundido, gana la última pedida.
     */
    public void mostrar(String nombre) {
        if (!isShowing()) {
            cartas.show(contenedor, nombre);
            return;
        }
        destino = nombre;
        oscureciendo = true; // si ya estaba aclarando, vuelve a oscurecer hacia el nuevo destino
        if (animacion != null && animacion.isRunning()) {
            return;
        }
        opacidadVelo = 0f;
        velo.setVisible(true);
        animacion = new Timer(15, e -> avanzarFundido());
        animacion.start();
    }

    private void avanzarFundido() {
        if (oscureciendo) {
            opacidadVelo += PASO_FUNDIDO;
            if (opacidadVelo >= 1f) {
                opacidadVelo = 1f;
                cartas.show(contenedor, destino); // con la pantalla tapada, hacemos el cambio
                oscureciendo = false;
            }
        } else {
            opacidadVelo -= PASO_FUNDIDO;
            if (opacidadVelo <= 0f) {
                opacidadVelo = 0f;
                animacion.stop();
                velo.setVisible(false);
            }
        }
        velo.repaint();
    }
}