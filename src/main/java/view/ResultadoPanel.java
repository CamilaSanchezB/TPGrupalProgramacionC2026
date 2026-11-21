package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import model.ClasesBitacora.RegitroBitacora;
import model.Mision.InformeMision;
import model.Mision.ResultadoMision;
import model.nave.Nave;
import net.miginfocom.swing.MigLayout;

/** Pantalla 4: Informe Oficial de Misión (Debriefing) con balance de recursos y eventos del viaje. */
public class ResultadoPanel extends JPanel {
    private static final Color FONDO = new Color(13, 17, 23);
    private static final Color TARJETA = new Color(22, 27, 34);
    private static final Color BORDE = new Color(48, 54, 61);
    private static final Color VERDE = new Color(63, 185, 80);
    private static final Color ROJO = new Color(248, 81, 73);
    private static final Color AMBAR = new Color(210, 153, 34);
    private static final Color CIAN = new Color(88, 166, 255);

    private final JLabel lblEstado = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblMision = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblObservaciones = new JLabel("", SwingConstants.CENTER);

    private final JLabel kpiCombustible = new JLabel("", SwingConstants.CENTER);
    private final JLabel kpiEnergia = new JLabel("", SwingConstants.CENTER);
    private final JLabel kpiDesgaste = new JLabel("", SwingConstants.CENTER);
    private final JLabel kpiGemas = new JLabel("", SwingConstants.CENTER);

    private final JTextArea txtBitacoraMision = new JTextArea();
    private final JButton btnContinuar = new JButton();

    public ResultadoPanel() {
        super(new MigLayout("insets 16 24 16 24, wrap 1, fill", "[0:0, grow, fill]", "[]2[]2[]12[68!]12[grow]12[42!]"));
        setOpaque(true);
        setBackground(FONDO);

        lblEstado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblMision.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblMision.setForeground(Color.WHITE);
        lblObservaciones.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        lblObservaciones.setForeground(new Color(180, 190, 200));

        add(lblEstado, "growx, wmin 0");
        add(lblMision, "growx, wmin 0");
        add(lblObservaciones, "growx, wmin 0");

        JPanel panelBalance = new JPanel(new GridLayout(1, 4, 8, 0));
        panelBalance.setBackground(TARJETA);
        panelBalance.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDE),
                " BALANCE DE RECURSOS DEL VIAJE ",
                0, 0,
                new Font(Font.SANS_SERIF, Font.BOLD, 11),
                CIAN));

        estilizarTarjetaKpi(kpiCombustible);
        estilizarTarjetaKpi(kpiEnergia);
        estilizarTarjetaKpi(kpiDesgaste);
        estilizarTarjetaKpi(kpiGemas);

        panelBalance.add(kpiCombustible);
        panelBalance.add(kpiEnergia);
        panelBalance.add(kpiDesgaste);
        panelBalance.add(kpiGemas);
        add(panelBalance, "growx, wmin 0");

        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBackground(TARJETA);
        panelLog.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDE),
                " REGISTRO CRONOLÓGICO DE LA MISIÓN ",
                0, 0,
                new Font(Font.SANS_SERIF, Font.BOLD, 11),
                CIAN));

        txtBitacoraMision.setEditable(false);
        txtBitacoraMision.setBackground(FONDO);
        txtBitacoraMision.setForeground(new Color(210, 220, 230));
        txtBitacoraMision.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtBitacoraMision.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(txtBitacoraMision);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        panelLog.add(scroll, BorderLayout.CENTER);
        add(panelLog, "grow, wmin 0, hmin 100");

        btnContinuar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnContinuar.setBackground(new Color(33, 38, 45));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFocusPainted(false);
        btnContinuar.setOpaque(true);
        btnContinuar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        add(btnContinuar, "growx, wmin 0");
    }

    /** Muestra el informe completo de la misión recién finalizada. */
    public void mostrar(InformeMision informe, Nave nave) {
        if (informe == null) {
            mostrar(false);
            return;
        }

        ResultadoMision res = informe.getResultado();
        boolean destruida = nave != null && nave.estaDestruida();

        if (res == ResultadoMision.EXITOSA) {
            lblEstado.setForeground(VERDE);
            lblEstado.setText("MISIÓN EXITOSA");
        } else if (res == ResultadoMision.RECHAZADA) {
            lblEstado.setForeground(AMBAR);
            lblEstado.setText("MISIÓN RECHAZADA");
        } else {
            lblEstado.setForeground(ROJO);
            lblEstado.setText("MISIÓN FALLIDA");
        }

        lblMision.setText(informe.getNombreMision());
        String obs = informe.getObservaciones();
        lblObservaciones.setText(obs == null || obs.isEmpty()
                ? "Operación finalizada sin observaciones adicionales."
                : "Detalle: " + obs);

        kpiCombustible.setText(String.format("<html><center>COMBUSTIBLE<br><b>%d</b> (%+d)</center></html>",
                informe.getCombustibleFinal(), -informe.getCombustibleConsumido()));
        kpiEnergia.setText(String.format("<html><center>ENERGÍA<br><b>%d</b> (%+d)</center></html>",
                informe.getEnergiaFinal(), -informe.getEnergiaConsumida()));
        kpiDesgaste.setText(String.format("<html><center>DESGASTE<br><b>%d</b> (%+d)</center></html>",
                informe.getDesgasteFinal(), informe.getDesgasteGenerado()));
        kpiGemas.setText(String.format("<html><center>GEMAS<br><b>%d</b></center></html>",
                nave != null ? nave.getGemas() : 0));

        StringBuilder sb = new StringBuilder();
        for (RegitroBitacora ev : informe.getEventos()) {
            sb.append("[").append(ev.getTiempoActual()).append("] ")
                    .append(ev.getDescripcion()).append("\n");
        }
        txtBitacoraMision.setText(sb.toString());
        txtBitacoraMision.setCaretPosition(0);

        btnContinuar.setText(destruida
                ? "Nave destruida — Seleccionar nueva nave"
                : "Volver al Centro de Comando");
    }

    public void mostrar(boolean gano) {
        lblEstado.setForeground(gano ? VERDE : ROJO);
        lblEstado.setText(gano ? "MISIÓN EXITOSA" : "MISIÓN FALLIDA");
        lblMision.setText("");
        lblObservaciones.setText("");
        btnContinuar.setText(gano ? "Volver al Centro de Comando" : "Seleccionar nueva nave");
    }

    private static void estilizarTarjetaKpi(JLabel lbl) {
        lbl.setOpaque(true);
        lbl.setBackground(FONDO);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        lbl.setBorder(BorderFactory.createLineBorder(BORDE));
    }

    public void addAccionListener(ActionListener l) {
        btnContinuar.addActionListener(l);
    }
}