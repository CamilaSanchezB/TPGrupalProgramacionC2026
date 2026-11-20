package view;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.Universo.ElementoDelUniverso;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

/**
 * Un cuadrado del tablero. Observa al {@link Tablero} y, cuando este notifica,
 * consulta su propia casilla y solo se repinta si su contenido cambió.
 */
public class CasillaView extends JPanel implements Observer {
    private static final Color COLOR_VACIA = Color.BLACK;
    private static final Color COLOR_ELEMENTO = new Color(230, 140, 40);
    private static final Color COLOR_OBJETIVO = new Color(60, 180, 90);

    private final Tablero tablero;
    private final Posicion posicion;
    private final String tipoNave;
    private final JLabel etiqueta = new JLabel("", SwingConstants.CENTER);

    private ElementoDelUniverso elemento;
    private boolean hayNave;
    private boolean hayObjetivo;

    /**
     * @param tablero tablero observado (no nulo)
     * @param posicion casilla que representa este cuadrado (no nula)
     * @param lado lado del cuadrado en píxeles
     */
    public CasillaView(Tablero tablero, Posicion posicion, int lado, String tipoNave) {
        super(new BorderLayout());
        this.tablero = tablero;
        this.posicion = posicion;
        this.tipoNave = tipoNave == null ? "EXPLORADORA" : tipoNave.toUpperCase(java.util.Locale.ROOT);
        setOpaque(true);
        setBackground(COLOR_VACIA);
        setPreferredSize(new Dimension(lado, lado));
        setBorder(BorderFactory.createLineBorder(new Color(24, 24, 24)));
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, lado / 2)));
        etiqueta.setForeground(Color.WHITE);
        add(etiqueta, BorderLayout.CENTER);
        actualizarSiCambio();
        tablero.addObserver(this);
    }

    public CasillaView(Tablero tablero, Posicion posicion, int lado) {
        this(tablero, posicion, lado, "EXPLORADORA");
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
        ElementoDelUniverso nuevoElemento = tablero.verTablero()[posicion.getX()][posicion.getY()];
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
            setBackground(COLOR_VACIA);
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

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!hayNave) {
            return;
        }

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.max(8, Math.min(getWidth(), getHeight()) - 6);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int half = size / 2;

        if ("COMBATE".equals(tipoNave)) {
            g.setColor(new Color(255, 90, 70));
            g.fillPolygon(new Polygon(
                    new int[]{cx, cx + half, cx + half / 3, cx, cx - half / 3, cx - half},
                    new int[]{cy - half, cy + half / 2, cy + half / 3, cy + half, cy + half / 3, cy + half / 2},
                    6));
        } else if ("CARGUERA".equals(tipoNave) || "CARGUERO".equals(tipoNave)) {
            g.setColor(new Color(255, 190, 60));
            g.fillRoundRect(cx - half, cy - half / 2, size, size, 4, 4);
            g.setColor(new Color(30, 30, 30));
            g.fillRect(cx - half / 2, cy - half / 2, half / 2, half);
            g.fillRect(cx + 2, cy - half / 2, half / 2, half);
        } else {
            g.setColor(new Color(70, 210, 255));
            g.fillOval(cx - half / 2, cy - half / 2, half, half);
            g.setStroke(new BasicStroke(Math.max(1f, size / 10f)));
            g.drawOval(cx - half, cy - half / 2, size, half);
            g.drawLine(cx, cy - half / 2, cx, cy - half);
        }
        g.dispose();
    }
}
