package view;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
public class MainPanel extends JPanel {
    private final JButton btnTripulantes = new JButton("Agregar tripulantes");
    private final JButton btnIniciar = new JButton("Iniciar partida");
    public MainPanel() {
        super(new MigLayout("insets 40, wrap 1, align center center", "[240!]", "[48!]20[48!]"));
        add(btnTripulantes, "grow"); add(btnIniciar, "grow");
    }
    public void addTripulantesListener(ActionListener l) { btnTripulantes.addActionListener(l); }
    public void addIniciarListener(ActionListener l) { btnIniciar.addActionListener(l); }
}