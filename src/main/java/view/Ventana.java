package view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

    public Ventana() {
        super("TP_java");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(contenedor);
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    /** Registra una pantalla con un nombre para poder mostrarla después. */
    public void agregarPantalla(String nombre, JPanel pantalla) {
        JPanel vieja = pantallas.put(nombre, pantalla);
        if (vieja != null) contenedor.remove(vieja);
        contenedor.add(pantalla, nombre);
        contenedor.revalidate();
    }

    /** Muestra la pantalla registrada con ese nombre. */
    public void mostrar(String nombre) {
        cartas.show(contenedor, nombre);
    }
}
