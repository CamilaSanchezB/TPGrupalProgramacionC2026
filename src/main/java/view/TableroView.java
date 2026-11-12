package view;

import java.awt.GridLayout;
import javax.swing.JPanel;
import model.Posicion;
import model.Tablero;

/** Grilla de {@link CasillaView}; cada casilla se actualiza por su cuenta. */
public class TableroView extends JPanel {
    private final CasillaView[][] casillas;

    /**
     * @param tablero tablero a mostrar (no nulo)
     */
    public TableroView(Tablero tablero) {
        super(new GridLayout(tablero.getX(), tablero.getY(), 1, 1));
        int filas = tablero.getX();
        int columnas = tablero.getY();
        int lado = Math.max(8, Math.min(40, 640 / Math.max(filas, columnas)));
        casillas = new CasillaView[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                casillas[i][j] = new CasillaView(tablero, new Posicion(i, j), lado);
                add(casillas[i][j]);
            }
        }
    }

    /** Desregistra todas las casillas del tablero observado. */
    public void desconectar() {
        for (CasillaView[] fila : casillas) {
            for (CasillaView casilla : fila) {
                casilla.desconectar();
            }
        }
    }
}
