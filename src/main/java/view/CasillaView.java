package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

/**
 * Un cuadrado del tablero. Observa al {@link Tablero} y, cuando este notifica,
 * consulta su propia casilla y solo se repinta si su contenido cambió.
 */
public class CasillaView extends JPanel implements Observer {
    private static final Color COLOR_VACIA = Color.BLACK;

    private final Tablero tablero;
    private final Posicion posicion;
    private final ImageIcon iconoNave;
    private final ImageIcon iconoPlaneta;
    private final ImageIcon iconoAsteroide;
    private final JLabel etiqueta = new JLabel("", SwingConstants.CENTER);

    private ElementoDelUniverso elemento;
    private boolean hayNave;
    private boolean hayObjetivo;

    /**
     * @param tablero tablero observado (no nulo)
     * @param posicion casilla que representa este cuadrado (no nula)
     * @param lado lado del cuadrado en píxeles
     */
    public CasillaView(
            Tablero tablero,
            Posicion posicion,
            int lado,
            ImageIcon iconoNave,
            ImageIcon iconoPlaneta,
            ImageIcon iconoAsteroide) {
        super(new BorderLayout());
        this.tablero = tablero;
        this.posicion = posicion;
        this.iconoNave = iconoNave;
        this.iconoPlaneta = iconoPlaneta;
        this.iconoAsteroide = iconoAsteroide;
        setOpaque(true);
        setBackground(COLOR_VACIA);
        setPreferredSize(new Dimension(lado, lado));
        setBorder(BorderFactory.createLineBorder(new Color(24, 24, 24)));
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, lado / 2)));
        etiqueta.setForeground(Color.WHITE);
        add(etiqueta, BorderLayout.CENTER);
        tablero.addObserver(this);
        actualizarSiCambio();
    }

    /** Deja de observar al tablero. */
    public void desconectar() {
        tablero.deleteObserver(this);
    }

    @Override
    public void update(Observable origen, Object argumento) {
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            actualizarSiCambio();
        } else {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    actualizarSiCambio();
                }
            });
        }
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
        setBackground(COLOR_VACIA);

        if (hayNave) {
            etiqueta.setIcon(iconoNave);
        } else if (elemento instanceof Asteroide) {
            etiqueta.setIcon(iconoAsteroide);
        } else if (hayObjetivo) {
            etiqueta.setIcon(iconoPlaneta);
        } else {
            etiqueta.setIcon(null);
        }

        if (hayNave) {
            etiqueta.setText("");
            setToolTipText(null);
        } else if (elemento instanceof Asteroide) {
            etiqueta.setText("");
            setToolTipText(elemento.toString());
        } else if (elemento != null) {
            etiqueta.setText(elemento.getClass().getSimpleName().substring(0, 1));
            setToolTipText(elemento.toString());
        } else if (hayObjetivo) {
            etiqueta.setText("");
            setToolTipText("Objetivo");
        } else {
            etiqueta.setText("");
            setToolTipText(null);
        }
        repaint();
    }
}
