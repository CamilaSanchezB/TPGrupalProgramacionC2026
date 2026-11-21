package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

/** Grilla de {@link CasillaView}; cada casilla se actualiza por su cuenta. */
public class TableroView extends JPanel {
    private final CasillaView[][] casillas;

    /**
     * @param tablero tablero a mostrar (no nulo)
     */
    public TableroView(Tablero tablero, String tipoNave, Set<Posicion> reveladas) {
        super(new GridLayout(tablero.getX(), tablero.getY(), 1, 1));
        setOpaque(true);
        setBackground(Color.BLACK);
        int filas = tablero.getX();
        int columnas = tablero.getY();
        int lado = Math.max(8, Math.min(40, 640 / Math.max(filas, columnas)));
        String archivoNave;
        String tipo = tipoNave == null ? "" : tipoNave.toUpperCase(java.util.Locale.ROOT);
        if ("COMBATE".equals(tipo)) {
            archivoNave = "combate.png";
        } else if ("CARGUERA".equals(tipo) || "CARGUERO".equals(tipo)) {
            archivoNave = "carguera.png";
        } else {
            archivoNave = "exploradora.png";
        }
        ImageIcon iconoNave = Assets.cargarEscalada(archivoNave, Math.max(8, lado - 6));
        ImageIcon iconoPlaneta = Assets.cargarEscalada("planeta.png", Math.max(8, lado - 6));
        ImageIcon iconoAsteroide = Assets.cargarEscalada("asteroide.png", Math.max(8, lado - 6));
        casillas = new CasillaView[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                casillas[i][j] = new CasillaView(
                        tablero,
                        new Posicion(i, j),
                        lado,
                        iconoNave,
                        iconoPlaneta,
                        iconoAsteroide,
                        reveladas);
                add(casillas[i][j]);
            }
        }
    }

    public TableroView(Tablero tablero, Set<Posicion> reveladas) {
        this(tablero, "EXPLORADORA", reveladas);
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
