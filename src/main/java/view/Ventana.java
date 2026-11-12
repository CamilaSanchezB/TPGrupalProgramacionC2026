package view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Única ventana de la aplicación. Muestra una pantalla (JPanel) a la vez. */
public class Ventana extends JFrame {
    public static final String INICIAR_SESION = "iniciarSesion";
    public static final String CREAR_NAVE = "crearNave";
    public static final String TABLERO = "tablero";

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
        contenedor.add(pantalla, nombre);
    }

    /** Muestra la pantalla registrada con ese nombre. */
    public void mostrar(String nombre) {
        cartas.show(contenedor, nombre);
    }
}
