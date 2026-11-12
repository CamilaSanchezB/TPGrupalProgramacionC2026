package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import model.ElementoDelUniverso;
import model.Posicion;
import model.Tablero;

/**
 * Un cuadrado del tablero. Observa al {@link Tablero} y, cuando este notifica,
 * consulta su propia casilla y solo se repinta si su contenido cambió.
 */
public class CasillaView extends JPanel implements Observer {
    private static final Color COLOR_VACIA = new Color(30, 30, 46);
    private static final Color COLOR_ELEMENTO = new Color(230, 140, 40);
    private static final Color COLOR_OBJETIVO = new Color(60, 180, 90);
    private static final Color COLOR_NAVE = new Color(70, 130, 230);

    private final Tablero tablero;
    private final Posicion posicion;
    private final JLabel etiqueta = new JLabel("", SwingConstants.CENTER);

    private ElementoDelUniverso elemento;
    private boolean hayNave;
    private boolean hayObjetivo;

    /**
     * @param tablero tablero observado (no nulo)
     * @param posicion casilla que representa este cuadrado (no nula)
     * @param lado lado del cuadrado en píxeles
     */
    public CasillaView(Tablero tablero, Posicion posicion, int lado) {
        super(new BorderLayout());
        this.tablero = tablero;
        this.posicion = posicion;
        setPreferredSize(new Dimension(lado, lado));
        setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, lado / 2)));
        etiqueta.setForeground(Color.WHITE);
        add(etiqueta, BorderLayout.CENTER);
        actualizarSiCambio();
        tablero.addObserver(this);
    }

    /** Llamado por el tablero cuando decide notificar un cambio. */
    @Override
    public void update(Observable origen, Object argumento) {
        if (SwingUtilities.isEventDispatchThread()) {
            actualizarSiCambio();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    actualizarSiCambio();
                }
            });
        }
    }

    /** Deja de observar al tablero (llamar al cerrar la vista). */
    public void desconectar() {
        tablero.deleteObserver(this);
    }

    private void actualizarSiCambio() {
        ElementoDelUniverso nuevoElemento = tablero.getElementoEn(posicion);
        boolean nuevaNave = posicion.equals(tablero.getPosicionNave());
        boolean nuevoObjetivo = posicion.equals(tablero.getObjetivo());
        if (nuevoElemento == elemento && nuevaNave == hayNave && nuevoObjetivo == hayObjetivo) {
            return;
        }
        elemento = nuevoElemento;
        hayNave = nuevaNave;
        hayObjetivo = nuevoObjetivo;
        pintar();
    }

    private void pintar() {
        if (hayNave) {
            setBackground(COLOR_NAVE);
        } else if (hayObjetivo) {
            setBackground(COLOR_OBJETIVO);
        } else if (elemento != null) {
            setBackground(COLOR_ELEMENTO);
        } else {
            setBackground(COLOR_VACIA);
        }
        if (elemento != null) {
            etiqueta.setText(elemento.getClass().getSimpleName().substring(0, 1));
            setToolTipText(elemento.toString());
        } else {
            etiqueta.setText(hayNave ? "N" : hayObjetivo ? "X" : "");
            setToolTipText(null);
        }
        repaint();
    }
}
