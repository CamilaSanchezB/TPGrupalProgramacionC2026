package view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// SelectorNavePanel: 3 figuras clicables
public class SelectorNavePanel extends FondoEstrellas {
    private final List<ActionListener> oyentes = new ArrayList<ActionListener>();

    private static final Color COLOR_COMBATE = new Color(167, 139, 250);
    private static final Color COLOR_CARGUERA = new Color(45, 212, 191);
    private static final Color COLOR_EXPLORADORA = new Color(148, 163, 184);

    public SelectorNavePanel() {
        super(new BorderLayout(0, 24));
        setOpaque(true);
        setBackground(ConfigView.FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 6));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Elegí tu nave", SwingConstants.CENTER);
        titulo.setFont(ConfigView.FUENTE_TITULO_GRANDE);
        titulo.setForeground(ConfigView.TITULO);
        encabezado.add(titulo);

        add(encabezado, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(1, 3, 20, 0));
        opciones.setOpaque(false);
        opciones.add(new Figura("COMBATE", "combate.png", COLOR_COMBATE));
        opciones.add(new Figura("CARGUERA", "carguera.png", COLOR_CARGUERA));
        opciones.add(new Figura("EXPLORADORA", "exploradora.png", COLOR_EXPLORADORA));

        add(opciones, BorderLayout.CENTER);
    }

    public void addElegirListener(ActionListener listener) {
        oyentes.add(listener);
    }

    private class Figura extends JPanel {
        private final String tipo;

        Figura(String tipo, String archivo, Color color) {
            super(new BorderLayout(0, 12));
            this.tipo = tipo;
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            final JPanel marcoImagen = new JPanel(new BorderLayout());
            marcoImagen.setOpaque(true);
            marcoImagen.setBackground(ConfigView.SUPERFICIE);
            marcoImagen.setBorder(marco(ConfigView.BORDE, 2));

            JLabel imagen = new JLabel(
                    Assets.cargarEscalada(archivo, 150), SwingConstants.CENTER);
            marcoImagen.add(imagen, BorderLayout.CENTER);
            add(marcoImagen, BorderLayout.CENTER);

            JLabel nombre = new JLabel(tipo, SwingConstants.CENTER);
            nombre.setFont(ConfigView.FUENTE_TITULO);
            nombre.setForeground(color);
            add(nombre, BorderLayout.SOUTH);

            MouseAdapter selector = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    for (ActionListener oyente : oyentes) {
                        oyente.actionPerformed(new ActionEvent(
                                Figura.this,
                                ActionEvent.ACTION_PERFORMED,
                                tipo));
                    }
                }
            };

            // Hover: al pasar el mouse el marco toma el color de la nave y se aclara el fondo.
            MouseAdapter resaltar = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent evento) {
                    marcoImagen.setBorder(marco(color, 3));
                    marcoImagen.setBackground(ConfigView.TARJETA_COMANDO);
                }

                @Override
                public void mouseExited(MouseEvent evento) {
                    // Pasar de la figura a su imagen también dispara "exited": solo
                    // quitamos el resaltado si el mouse salió de verdad de toda la figura.
                    Point p = SwingUtilities.convertPoint(
                            (Component) evento.getSource(), evento.getPoint(), Figura.this);
                    if (!Figura.this.contains(p)) {
                        marcoImagen.setBorder(marco(ConfigView.BORDE, 2));
                        marcoImagen.setBackground(ConfigView.SUPERFICIE);
                    }
                }
            };

            for (Component c : new Component[] { this, marcoImagen, imagen, nombre }) {
                c.addMouseListener(selector);
                c.addMouseListener(resaltar);
            }
        }

        private javax.swing.border.Border marco(Color color, int grosor) {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, grosor),
                    BorderFactory.createEmptyBorder(14 - (grosor - 2), 14 - (grosor - 2),
                            14 - (grosor - 2), 14 - (grosor - 2)));
        }
    }
}