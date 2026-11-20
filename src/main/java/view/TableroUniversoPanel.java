package view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.destinoytablero.Tablero;

/** Pantalla 3: tablero en vivo. */
public class TableroUniversoPanel extends JPanel {
    private TableroView tableroView;

    public TableroUniversoPanel() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.BLACK);
        // TODO: botón "Volver", bitácora, recursos, etc.
        JLabel titulo = new JLabel("TABLERO");
        titulo.setForeground(Color.WHITE);
        add(titulo, BorderLayout.NORTH);
    }

    /** Muestra el tablero en el centro, reemplazando el anterior si lo hubiera. */
    public void mostrarTablero(Tablero tablero, String tipoNave) {
        desconectar();
        tableroView = new TableroView(tablero, tipoNave);
        add(tableroView, BorderLayout.CENTER);
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
    }

    // TODO: public void addVolverListener(ActionListener listener) { ... }
}
