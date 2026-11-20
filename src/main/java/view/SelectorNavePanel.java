package view;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// SelectorNavePanel: 3 figuras clicables
public class SelectorNavePanel extends JPanel {
    public SelectorNavePanel() {
        super(new GridLayout(1, 3, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        for (String t : new String[]{"COMBATE", "CARGUERA", "EXPLORADORA"}) add(new Figura(t));
    }
    private final java.util.List<ActionListener> oyentes = new java.util.ArrayList<>();
    public void addElegirListener(ActionListener l) { oyentes.add(l); }

    private class Figura extends JComponent {
        private final String tipo;
        Figura(String tipo) {
            this.tipo = tipo;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    for (ActionListener l : oyentes)
                        l.actionPerformed(new ActionEvent(Figura.this, 0, Figura.this.tipo));
                }
            });
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2 - 15;
            g.setColor(ConfigView.ACENTO);
            if (tipo.equals("COMBATE")) {            // flecha afilada
                g.fillPolygon(new int[]{cx, cx + 60, cx, cx - 60}, new int[]{cy - 70, cy + 60, cy + 30, cy + 60}, 4);
            } else if (tipo.equals("CARGUERA")) {    // bloque ancho
                g.fillRoundRect(cx - 70, cy - 40, 140, 80, 20, 20);
            } else {                                 // disco (exploradora)
                g.fillOval(cx - 60, cy - 40, 120, 80);
                g.fillRect(cx - 5, cy - 70, 10, 30);
            }
            g.setColor(ConfigView.TITULO);
            g.setFont(ConfigView.FUENTE_TITULO);
            g.drawString(tipo, cx - g.getFontMetrics().stringWidth(tipo) / 2, h - 30);
            g.dispose();
        }
    }
}