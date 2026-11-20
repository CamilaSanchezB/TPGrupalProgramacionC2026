package view;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
public class ResultadoPanel extends JPanel {
    private final JLabel lbl = new JLabel("", SwingConstants.CENTER);
    private final JButton btn = new JButton();
    public ResultadoPanel() {
        super(new MigLayout("insets 40, wrap 1, align center center", "[300!]", "[]20[48!]"));
        lbl.setFont(ConfigView.FUENTE_TITULO_GRANDE);
        add(lbl, "grow"); add(btn, "grow");
    }
    public void mostrar(boolean gano) {
        lbl.setForeground(gano ? ConfigView.AVISO : ConfigView.ERROR);
        lbl.setText(gano ? "¡Llegaste al objetivo!" : "Perdiste");
        btn.setText(gano ? "Continuar partida" : "Partida nueva");
    }
    public void addAccionListener(ActionListener l) { btn.addActionListener(l); }
}