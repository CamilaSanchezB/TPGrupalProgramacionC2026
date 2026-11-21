package view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.destinoytablero.Tablero;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import model.ClasesBitacora.Bitacora;

/** Pantalla 3: tablero en vivo. */
public class TableroUniversoPanel extends JPanel {
    private TableroView tableroView;
    private BitacoraView bitacoraView;
    private final JButton btnUniverso = new JButton("Abrir universo paralelo");

    public TableroUniversoPanel() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.BLACK);
        // TODO: botón "Volver", bitácora, recursos, etc.
       JLabel titulo = new JLabel("TABLERO");
    titulo.setForeground(Color.WHITE);

    JPanel barra = new JPanel(new BorderLayout());
    barra.setOpaque(false);
    barra.add(titulo, BorderLayout.WEST);
    barra.add(btnUniverso, BorderLayout.EAST);
    add(barra, BorderLayout.NORTH);
    }

    /** Muestra el tablero en el centro, reemplazando el anterior si lo hubiera. */
    public void mostrarTablero(Tablero tablero, String tipoNave, Bitacora bitacora) {
        desconectar();
        tableroView = new TableroView(tablero, tipoNave);
        bitacoraView = new BitacoraView(bitacora);
        add(tableroView, BorderLayout.CENTER);
        add(bitacoraView, BorderLayout.EAST);
        revalidate();
        repaint();
    }

    /** Quita el tablero y deja de observarlo. Llamar antes de salir de esta pantalla. */
    public void desconectar() {
        if (tableroView != null) {
            tableroView.desconectar();
            remove(tableroView);
            tableroView = null;
        }
        if (bitacoraView != null) {
            bitacoraView.desconectar();
            remove(bitacoraView);
            bitacoraView = null;
        }
    }

    public void addUniversoListener(ActionListener l) {
    btnUniverso.addActionListener(l);
    }

    // TODO: public void addVolverListener(ActionListener listener) { ... }
}
