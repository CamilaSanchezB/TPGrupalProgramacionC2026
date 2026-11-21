package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import model.Universo.NaveEnemiga;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;
import model.nave.NaveCarguero;
import model.nave.NaveCombate;
import model.nave.NaveExploradora;

/**
 - Un cuadrado del tablero. Observa al {@link Tablero} y, cuando este notifica,
 - consulta su propia casilla y solo se repinta si su contenido cambió.
 */
public class CasillaView extends JPanel implements Observer {
    private static final Color COLOR_VACIA = Color.BLACK;
    private static final Color COLOR_REVELADA = new Color(20, 20, 40);

    private static final Border BORDE_NORMAL = BorderFactory.createLineBorder(new Color(24, 24, 24), 1);
    private static final Border BORDE_PROPIA = BorderFactory.createLineBorder(new Color(0, 220, 130), 2);
    private static final Border BORDE_ENEMIGA = BorderFactory.createLineBorder(new Color(220, 50, 50), 1);
    private static final Border BORDE_DERROTADA = BorderFactory.createLineBorder(new Color(90, 90, 90), 1);
    private static final Border BORDE_ESTACION = BorderFactory.createLineBorder(new Color(70, 170, 255), 1);
    private static final Border BORDE_OBJETIVO = BorderFactory.createLineBorder(new Color(255, 210, 60), 2);

    private final Tablero tablero;
    private final Posicion posicion;
    private final Posicion destinoMision;
    private final String tipoMision;

    private final ImageIcon iconoNave;
    private final ImageIcon iconoEnemiga;
    private final ImageIcon iconoEstacion;
    private final ImageIcon iconoPlaneta;
    private final ImageIcon iconoAsteroide;
    private final ImageIcon iconoCombate;
    private final ImageIcon iconoCarguera;
    private final ImageIcon iconoExploradora;

    private final JLabel etiqueta = new JLabel("", SwingConstants.CENTER);
    private final Set<Posicion> reveladas;

    private ElementoDelUniverso elemento;
    private boolean hayNave;
    private boolean hayObjetivo;
    private boolean visible;
    private boolean enemigaDerrotada;

    public CasillaView(
            Tablero tablero,
            Posicion posicion,
            Posicion destinoMision,
            String tipoMision,
            int lado,
            ImageIcon iconoNave,
            ImageIcon iconoEnemiga,
            ImageIcon iconoEstacion,
            ImageIcon iconoPlaneta,
            ImageIcon iconoAsteroide,
            ImageIcon iconoCombate,
            ImageIcon iconoCarguera,
            ImageIcon iconoExploradora,
            Set<Posicion> reveladas) {
        super(new BorderLayout());
        this.tablero = tablero;
        this.posicion = posicion;
        this.destinoMision = destinoMision;
        this.tipoMision = tipoMision;
        this.iconoNave = iconoNave;
        this.iconoEnemiga = iconoEnemiga;
        this.iconoEstacion = iconoEstacion;
        this.iconoPlaneta = iconoPlaneta;
        this.iconoAsteroide = iconoAsteroide;
        this.iconoCombate = iconoCombate;
        this.iconoCarguera = iconoCarguera;
        this.iconoExploradora = iconoExploradora;
        this.reveladas = reveladas;

        setOpaque(true);
        setBackground(COLOR_VACIA);
        setPreferredSize(new Dimension(lado, lado));
        setBorder(BORDE_NORMAL);
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, lado / 2)));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setHorizontalTextPosition(SwingConstants.CENTER);
        etiqueta.setVerticalTextPosition(SwingConstants.CENTER);
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
        boolean nuevaVisible = reveladas.contains(posicion);
        ElementoDelUniverso nuevoElemento = nuevaVisible ? tablero.getElementoEn(posicion) : null;
        boolean nuevaNave = posicion.equals(tablero.getPosicionNave());
        boolean nuevoObjetivo = destinoMision != null && posicion.equals(destinoMision);
        boolean nuevaDerrotada = (nuevoElemento instanceof NaveEnemiga)
                && ((NaveEnemiga) nuevoElemento).estaDerrotada();

        if (nuevaVisible == visible && nuevoElemento == elemento
                && nuevaNave == hayNave && nuevoObjetivo == hayObjetivo
                && nuevaDerrotada == enemigaDerrotada) return;

        visible = nuevaVisible;
        elemento = nuevoElemento;
        hayNave = nuevaNave;
        hayObjetivo = nuevoObjetivo;
        enemigaDerrotada = nuevaDerrotada;
        pintar();
    }

    private void pintar() {
        setBackground(visible ? COLOR_REVELADA : COLOR_VACIA);
        setBorder(BORDE_NORMAL);
        etiqueta.setText("");
        etiqueta.setForeground(Color.WHITE);

        if (hayNave) {
            etiqueta.setIcon(iconoNave);
            setBorder(BORDE_PROPIA);
            setToolTipText("Tu nave " + posicion);
        } else if (elemento instanceof Asteroide) {
            etiqueta.setIcon(iconoAsteroide);
            setToolTipText(elemento.toString());
        } else if (elemento instanceof NaveEnemiga) {
            NaveEnemiga enemiga = (NaveEnemiga) elemento;
            etiqueta.setIcon(iconoDeNaveEnemiga(enemiga));
            if (enemiga.estaDerrotada()) {
                setBorder(BORDE_DERROTADA);
                etiqueta.setForeground(new Color(220, 70, 70));
                etiqueta.setText("X");
            } else {
                setBorder(BORDE_ENEMIGA);
                etiqueta.setForeground(Color.WHITE);
            }
            setToolTipText(enemiga.toString());
        } else if (elemento instanceof EstacionDeCarga) {
            etiqueta.setIcon(iconoEstacion);
            setBorder(BORDE_ESTACION);
            setToolTipText("Estación de Carga");
        } else if (hayObjetivo) {
            if ("INTERCEPCION".equals(tipoMision)) {
                etiqueta.setIcon(iconoExploradora);
                setToolTipText("Objetivo M-01: Nave aliada pidiendo asistencia");
            } else if ("RETORNO".equals(tipoMision)) {
                etiqueta.setIcon(iconoPlaneta);
                setToolTipText("Objetivo M-03: Base espacial (10, 10)");
            } else {
                etiqueta.setIcon(iconoPlaneta);
                setToolTipText("Objetivo M-02: Zona de recolección");
            }
            setBorder(BORDE_OBJETIVO);
        } else if (elemento != null) {
            etiqueta.setIcon(null);
            etiqueta.setForeground(Color.WHITE);
            etiqueta.setText(elemento.getClass().getSimpleName().substring(0, 1));
            setToolTipText(elemento.toString());
        } else {
            etiqueta.setIcon(null);
            setToolTipText(null);
        }

        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().repaint();
        }
    }

    private ImageIcon iconoDeNaveEnemiga(NaveEnemiga enemiga) {
        if (enemiga.getNave() instanceof NaveCombate) {
            return iconoCombate;
        }
        if (enemiga.getNave() instanceof NaveExploradora) {
            return iconoExploradora;
        }
        if (enemiga.getNave() instanceof NaveCarguero) {
            return iconoCarguera;
        }
        return iconoEnemiga;
    }
}