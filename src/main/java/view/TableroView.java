package view;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

/** Grilla de {@link CasillaView}; cada casilla se actualiza por su cuenta. */
public class TableroView extends JPanel {
    private final CasillaView[][] casillas;

    /**
     * @param tablero tablero a mostrar (no nulo)
     */
    public TableroView(Tablero tablero, String tipoNave) {
        super(new GridLayout(tablero.getX(), tablero.getY(), 1, 1));
        setOpaque(true);
        setBackground(Color.BLACK);
        int filas = tablero.getX();
        int columnas = tablero.getY();
        int lado = Math.max(8, Math.min(40, 640 / Math.max(filas, columnas)));
        casillas = new CasillaView[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                casillas[i][j] = new CasillaView(tablero, new Posicion(i, j), lado, tipoNave);
                add(casillas[i][j]);
            }
        }
    }

    public TableroView(Tablero tablero) {
        this(tablero, "EXPLORADORA");
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
