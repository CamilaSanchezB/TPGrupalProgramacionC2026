package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.ScrollPaneConstants;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;

/** Muestra los eventos de la bitácora en tiempo real. */
public class BitacoraView extends JPanel implements Observer {
    private final Bitacora bitacora;
    private final JTextArea eventos = new JTextArea();

    public BitacoraView(Bitacora bitacora) {
        super(new BorderLayout());
        this.bitacora = bitacora;

        setPreferredSize(new Dimension(240, 0));
        setBackground(new Color(12, 12, 12));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        eventos.setEditable(false);
        eventos.setLineWrap(true);
        eventos.setWrapStyleWord(true);
        eventos.setOpaque(true);
        eventos.setBackground(new Color(20, 20, 20));
        eventos.setForeground(Color.WHITE);
        eventos.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(eventos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
        actualizar();

        bitacora.addObserver(this);
    }

    public void desconectar() {
        bitacora.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object argumento) {
        if (SwingUtilities.isEventDispatchThread()) {
            actualizar();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    actualizar();
                }
            });
        }
    }

    private void actualizar() {
        String texto = "";
        for (RegitroBitacora evento : bitacora.getEventos()) {
            if (texto.length() > 0) {
                texto += "\n";
            }
            texto += evento.toString();
        }

        eventos.setText(texto);
        eventos.setCaretPosition(eventos.getDocument().getLength());
    }
}
