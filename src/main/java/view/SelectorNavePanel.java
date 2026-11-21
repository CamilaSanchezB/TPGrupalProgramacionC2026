package view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// SelectorNavePanel: 3 figuras clicables
public class SelectorNavePanel extends JPanel {
    private final List<ActionListener> oyentes = new ArrayList<ActionListener>();

    public SelectorNavePanel() {
        super(new GridLayout(1, 3, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        add(new Figura("COMBATE"));
        add(new Figura("CARGUERA"));
        add(new Figura("EXPLORADORA"));
    }

    public void addElegirListener(ActionListener listener) {
        oyentes.add(listener);
    }

    private class Figura extends JPanel {
        private final String tipo;

        Figura(String tipo) {
            super(new BorderLayout());
            this.tipo = tipo;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            String archivo;
            if ("COMBATE".equals(tipo)) {
                archivo = "combate.png";
            } else if ("CARGUERA".equals(tipo)) {
                archivo = "carguera.png";
            } else {
                archivo = "exploradora.png";
            }

            JLabel imagen = new JLabel(Assets.cargarEscalada(archivo, 180), SwingConstants.CENTER);
            add(imagen, BorderLayout.CENTER);

            JLabel nombre = new JLabel(tipo, SwingConstants.CENTER);
            nombre.setFont(ConfigView.FUENTE_TITULO);
            nombre.setForeground(ConfigView.TITULO);
            add(nombre, BorderLayout.SOUTH);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    for (ActionListener oyente : oyentes) {
                        oyente.actionPerformed(new ActionEvent(
                                Figura.this,
                                ActionEvent.ACTION_PERFORMED,
                                tipo));
                    }
                }
            });
        }
    }
}