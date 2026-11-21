package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

/** Grilla de {@link CasillaView}; cada casilla se actualiza por su cuenta. */
public class TableroView extends JPanel {
    private final CasillaView[][] casillas;

    public TableroView(
            Tablero tablero,
            String tipoNave,
            Posicion destinoMision,
            String tipoMision,
            Set<Posicion> reveladas) {
        super(new GridLayout(tablero.getX(), tablero.getY(), 1, 1));
        setOpaque(true);
        setBackground(Color.BLACK);
        int filas = tablero.getX();
        int columnas = tablero.getY();
        int lado = Math.max(8, Math.min(40, 640 / Math.max(filas, columnas)));
        int tamIcono = Math.max(8, lado - 6);

        String archivoNave;
        String tipo = tipoNave == null ? "" : tipoNave.toUpperCase(java.util.Locale.ROOT);
        if ("COMBATE".equals(tipo)) {
            archivoNave = "combate.png";
        } else if ("CARGUERA".equals(tipo) || "CARGUERO".equals(tipo)) {
            archivoNave = "carguera.png";
        } else {
            archivoNave = "exploradora.png";
        }

        String[] planetas = {"planeta.png", "planeta2.png", "planeta3.png"};
        String archivoPlaneta = planetas[ThreadLocalRandom.current().nextInt(planetas.length)];
        ImageIcon iconoNave = Assets.cargarEscalada(archivoNave, tamIcono);
        ImageIcon iconoEnemiga = Assets.cargarEscalada("naveEnemiga.png", tamIcono);
        ImageIcon iconoEstacion = Assets.cargarEscalada("estacionDeCarga.png", tamIcono);
        ImageIcon iconoPlaneta = Assets.cargarEscalada(archivoPlaneta, tamIcono);
        ImageIcon iconoAsteroide = Assets.cargarEscalada("asteroide.png", tamIcono);
        ImageIcon iconoCombate = Assets.cargarEscalada("combate.png", tamIcono);
        ImageIcon iconoCarguera = Assets.cargarEscalada("carguera.png", tamIcono);
        ImageIcon iconoExploradora = Assets.cargarEscalada("exploradora.png", tamIcono);
        casillas = new CasillaView[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                casillas[i][j] = new CasillaView(
                        tablero,
                        new Posicion(i, j),
                        destinoMision,
                        tipoMision,
                        lado,
                        iconoNave,
                        iconoEnemiga,
                        iconoEstacion,
                        iconoPlaneta,
                        iconoAsteroide,
                        iconoCombate,
                        iconoCarguera,
                        iconoExploradora,
                        reveladas);
                add(casillas[i][j]);
            }
        }
    }

    public TableroView(Tablero tablero, String tipoNave, Set<Posicion> reveladas) {
        this(tablero, tipoNave, tablero.getObjetivo(), "RECOLECCION", reveladas);
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