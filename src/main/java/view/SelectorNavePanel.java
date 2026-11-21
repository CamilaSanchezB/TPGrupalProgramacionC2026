package view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// SelectorNavePanel: 3 figuras clicables
public class SelectorNavePanel extends JPanel {
    private final List<ActionListener> oyentes = new ArrayList<ActionListener>();

    private static final Color COLOR_COMBATE = new Color(124, 58, 237);
    private static final Color COLOR_CARGUERA = new Color(14, 165, 164);
    private static final Color COLOR_EXPLORADORA = new Color(71, 85, 105);

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

            JPanel marcoImagen = new JPanel(new BorderLayout());
            marcoImagen.setOpaque(true);
            marcoImagen.setBackground(ConfigView.SUPERFICIE);
            marcoImagen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));

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

            addMouseListener(selector);
            marcoImagen.addMouseListener(selector);
            imagen.addMouseListener(selector);
            nombre.addMouseListener(selector);
        }
    }
}