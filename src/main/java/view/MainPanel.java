package view;

import controller.AsistenteDeComando;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import model.Mision.InformeMision;
import model.Mision.ResultadoMision;
import model.nave.Nave;
import model.tripulantes.Tripulante;
import net.miginfocom.swing.MigLayout;

/** Pantalla 2: Centro de Comando y Hangar con telemetría de la nave, misiones e historial. */
public class MainPanel extends JPanel {
    private static final Color FONDO = new Color(13, 17, 23);
    private static final Color TARJETA = new Color(22, 27, 34);
    private static final Color BORDE = new Color(48, 54, 61);
    private static final Color TEXTO_SECUNDARIO = new Color(139, 148, 158);
    private static final Color ACENTO_CIAN = new Color(88, 166, 255);
    private static final Color ACENTO_VERDE = new Color(63, 185, 80);
    private static final Color ACENTO_AMBAR = new Color(210, 153, 34);
    private static final Color ACENTO_ROJO = new Color(248, 81, 73);

    // Panel Izquierdo: Hangar y Telemetría
    private final JLabel lblImagenNave = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblTituloNave = new JLabel("NAVE EN HANGAR", SwingConstants.CENTER);
    private final JLabel lblEstadoOperativo = new JLabel("OPERATIVA", SwingConstants.CENTER);

    private final JProgressBar barCombustible = crearBarra(ACENTO_VERDE);
    private final JProgressBar barEnergia = crearBarra(ACENTO_CIAN);
    private final JProgressBar barDesgaste = crearBarra(ACENTO_AMBAR);

    private final JLabel lblGemas = new JLabel("Gemas: 0");
    private final JLabel lblMotor = new JLabel("Motor: Disp.");
    private final JLabel lblEstadisticas = new JLabel("Misiones: 0/0");
    private final JTextArea txtTripulantes = new JTextArea();

    // Panel Derecho: Misiones e Historial
    private final JButton btnTripulantes = crearBoton("Gestionar Tripulación", new Color(33, 38, 45));
    private final JButton btnIntercepcion = crearBoton("M-01: Intercepción y Asistencia", new Color(25, 65, 110));
    private final JButton btnRecoleccion = crearBoton("M-02: Recolección de Gemas", new Color(25, 85, 55));
    private final JButton btnRetorno = crearBoton("M-03: Retorno Seguro a Base", new Color(85, 60, 20));

    private final JLabel lblReqM01 = new JLabel("Req: 4 Comb | 5 Energ | 4 Desg — Auxilio a nave aliada");
    private final JLabel lblReqM02 = new JLabel("Req: 4 Comb | 10 Energ | 4 Desg — Extracción de 20-100 gemas");
    private final JLabel lblReqM03 = new JLabel("Req: 4 Comb | 0 Energ | 4 Desg — Regreso a (10,10) + recarga");

    private final JTextArea txtHistorial = new JTextArea();

    public MainPanel() {
        super(new BorderLayout(12, 12));
        setOpaque(true);
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel encabezado = new JLabel("CENTRO DE COMANDO ESPACIAL — PUENTE DE MANDO");
        encabezado.setForeground(Color.WHITE);
        encabezado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)));
        add(encabezado, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 14, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(construirPanelHangar());
        cuerpo.add(construirPanelOperaciones());
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel construirPanelHangar() {
        JPanel panel = crearTarjeta("TELEMETRÍA DE LA NAVE");
        panel.setLayout(new MigLayout(
                "insets 10, wrap 1, fillx",
                "[0:0, grow, fill]",
                "[]4[]2[]10[]6[]6[]10[]4[]8[grow]"));

        lblImagenNave.setPreferredSize(new Dimension(76, 76));
        lblTituloNave.setForeground(Color.WHITE);
        lblTituloNave.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        lblEstadoOperativo.setForeground(ACENTO_VERDE);
        lblEstadoOperativo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));

        panel.add(lblImagenNave, "align center");
        panel.add(lblTituloNave, "growx");
        panel.add(lblEstadoOperativo, "growx");

        panel.add(crearFilaBarra("Combustible", barCombustible), "growx, wmin 0");
        panel.add(crearFilaBarra("Energía", barEnergia), "growx, wmin 0");
        panel.add(crearFilaBarra("Desgaste", barDesgaste), "growx, wmin 0");

        JPanel kpis = new JPanel(new GridLayout(1, 3, 6, 0));
        kpis.setOpaque(false);
        estilizarKpi(lblGemas, ACENTO_AMBAR);
        estilizarKpi(lblMotor, ACENTO_CIAN);
        estilizarKpi(lblEstadisticas, Color.WHITE);
        kpis.add(lblGemas);
        kpis.add(lblMotor);
        kpis.add(lblEstadisticas);
        panel.add(kpis, "growx, wmin 0");

        JLabel lblTrip = new JLabel("TRIPULACIÓN EMBARCADA");
        lblTrip.setForeground(TEXTO_SECUNDARIO);
        lblTrip.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        panel.add(lblTrip, "wmin 0");

        txtTripulantes.setEditable(false);
        txtTripulantes.setBackground(FONDO);
        txtTripulantes.setForeground(new Color(210, 220, 230));
        txtTripulantes.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtTripulantes.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scrollTrip = new JScrollPane(txtTripulantes);
        scrollTrip.setBorder(BorderFactory.createLineBorder(BORDE));
        panel.add(scrollTrip, "grow, wmin 0, hmin 70");

        return panel;
    }

    private JPanel construirPanelOperaciones() {
        JPanel panel = crearTarjeta("DESPACHO DE MISIONES E HISTORIAL");
        panel.setLayout(new MigLayout(
                "insets 10, wrap 1, fillx",
                "[0:0, grow, fill]",
                "[34!]10[36!]2[]8[36!]2[]8[36!]2[]10[]4[grow]"));

        panel.add(btnTripulantes, "growx, wmin 0");

        estilizarSubtexto(lblReqM01);
        estilizarSubtexto(lblReqM02);
        estilizarSubtexto(lblReqM03);

        panel.add(btnIntercepcion, "growx, wmin 0");
        panel.add(lblReqM01, "growx, wmin 0");

        panel.add(btnRecoleccion, "growx, wmin 0");
        panel.add(lblReqM02, "growx, wmin 0");

        panel.add(btnRetorno, "growx, wmin 0");
        panel.add(lblReqM03, "growx, wmin 0");

        JLabel lblHist = new JLabel("BITÁCORA DE MISIONES COMPLETADAS");
        lblHist.setForeground(TEXTO_SECUNDARIO);
        lblHist.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        panel.add(lblHist, "wmin 0");

        txtHistorial.setEditable(false);
        txtHistorial.setBackground(FONDO);
        txtHistorial.setForeground(new Color(200, 210, 220));
        txtHistorial.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        txtHistorial.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scrollHist = new JScrollPane(txtHistorial);
        scrollHist.setBorder(BorderFactory.createLineBorder(BORDE));
        panel.add(scrollHist, "grow, wmin 0, hmin 80");

        return panel;
    }

    /** Refresca toda la telemetría de la nave, tripulación e historial cada vez que se entra al menú. */
    public void actualizarEstado(Nave nave, AsistenteDeComando asistente, String tipoNave) {
        if (nave == null || asistente == null) return;

        String tipo = tipoNave == null ? "EXPLORADORA" : tipoNave.toUpperCase(Locale.ROOT);
        String archivo = "COMBATE".equals(tipo) ? "combate.png"
                : ("CARGUERA".equals(tipo) || "CARGUERO".equals(tipo)) ? "carguera.png" : "exploradora.png";
        ImageIcon icono = Assets.cargarEscalada(archivo, 72);
        lblImagenNave.setIcon(icono);
        lblTituloNave.setText("NAVE " + tipo);

        if (nave.estaDestruida()) {
            lblEstadoOperativo.setText("ESTADO: INOPERATIVA / DESTRUIDA");
            lblEstadoOperativo.setForeground(ACENTO_ROJO);
        } else if (nave.requiereMantenimiento()) {
            lblEstadoOperativo.setText("ALERTA: REQUIERE MANTENIMIENTO (Desgaste >= 80)");
            lblEstadoOperativo.setForeground(ACENTO_AMBAR);
        } else {
            lblEstadoOperativo.setText("ESTADO: OPERATIVA PARA DESPEGUE");
            lblEstadoOperativo.setForeground(ACENTO_VERDE);
        }

        int comb = nave.getCombustible();
        int energ = nave.getEnergia();
        int desg = nave.getDesgaste();

        barCombustible.setValue(comb);
        barCombustible.setString(comb + " / 100");
        barCombustible.setForeground(comb <= 20 ? ACENTO_ROJO : ACENTO_VERDE);

        barEnergia.setValue(energ);
        barEnergia.setString(energ + " / 100");
        barEnergia.setForeground(energ <= 20 ? ACENTO_ROJO : ACENTO_CIAN);

        barDesgaste.setValue(desg);
        barDesgaste.setString(desg + " / 100");
        barDesgaste.setForeground(desg >= 80 ? ACENTO_ROJO : (desg >= 50 ? ACENTO_AMBAR : ACENTO_VERDE));

        lblGemas.setText("Gemas: " + nave.getGemas());
        lblMotor.setText("Motor: " + nave.getEstadoMotor());

        actualizarEtiquetaRequisito(lblReqM01, nave.puedeConsumir(4, 5, 4),
                "Req: 4 Comb | 5 Energ | 4 Desg — Auxilio a nave aliada");
        actualizarEtiquetaRequisito(lblReqM02, nave.puedeConsumir(4, 10, 4),
                "Req: 4 Comb | 10 Energ | 4 Desg — Extracción de 20-100 gemas");
        actualizarEtiquetaRequisito(lblReqM03, nave.puedeConsumir(4, 0, 4),
                "Req: 4 Comb | 0 Energ | 4 Desg — Regreso a (10,10) + recarga");

        // Tripulantes con cargo y nombre limpios (sin imprimir @hashcode)
        StringBuilder sbTrip = new StringBuilder();
        List<Tripulante> tripulantes = nave.getTripulantes();
        for (Tripulante t : tripulantes) {
            String cargo = t.getClass().getSimpleName();
            String nombre = t.getNombre();
            sbTrip.append("• ").append(cargo).append(": ").append(nombre).append("\n");
        }
        txtTripulantes.setText(sbTrip.toString().trim());
        txtTripulantes.setCaretPosition(0);

        // Historial de misiones
        List<InformeMision> historial = asistente.getHistorialMisiones();
        int exitosas = 0;
        StringBuilder sbHist = new StringBuilder();
        if (historial.isEmpty()) {
            sbHist.append("Sin misiones registradas aún.\nSeleccioná una misión para iniciar.");
        } else {
            for (int i = historial.size() - 1; i >= 0; i--) {
                InformeMision inf = historial.get(i);
                if (inf.getResultado() == ResultadoMision.EXITOSA) exitosas++;
                sbHist.append(String.format("[%s] %s (Comb:%+d Energ:%+d Desg:%+d)\n",
                        inf.getResultado(),
                        inf.getNombreMision(),
                        -inf.getCombustibleConsumido(),
                        -inf.getEnergiaConsumida(),
                        inf.getDesgasteGenerado()));
            }
        }
        lblEstadisticas.setText("Éxitos: " + exitosas + "/" + historial.size());
        txtHistorial.setText(sbHist.toString().trim());
        txtHistorial.setCaretPosition(0);
    }

    private void actualizarEtiquetaRequisito(JLabel lbl, boolean alcanza, String textoBase) {
        if (alcanza) {
            lbl.setForeground(TEXTO_SECUNDARIO);
            lbl.setText(textoBase);
        } else {
            lbl.setForeground(ACENTO_ROJO);
            lbl.setText(textoBase + " [INSUFICIENTE]");
        }
    }

    private static JPanel crearTarjeta(String titulo) {
        JPanel p = new JPanel();
        p.setBackground(TARJETA);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDE),
                " " + titulo + " ",
                0,
                0,
                new Font(Font.SANS_SERIF, Font.BOLD, 12),
                ACENTO_CIAN));
        return p;
    }

    private static JProgressBar crearBarra(Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setBackground(FONDO);
        bar.setForeground(color);
        bar.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        bar.setBorder(BorderFactory.createLineBorder(BORDE));
        bar.setPreferredSize(new Dimension(10, 20));
        return bar;
    }

    private static JPanel crearFilaBarra(String nombre, JProgressBar barra) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        JLabel lbl = new JLabel(nombre);
        lbl.setPreferredSize(new Dimension(82, 20));
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        fila.add(lbl, BorderLayout.WEST);
        fila.add(barra, BorderLayout.CENTER);
        return fila;
    }

    private static void estilizarKpi(JLabel lbl, Color color) {
        lbl.setOpaque(true);
        lbl.setBackground(FONDO);
        lbl.setForeground(color);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(5, 2, 5, 2)));
    }

    private static void estilizarSubtexto(JLabel lbl) {
        lbl.setForeground(TEXTO_SECUNDARIO);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    }

    private static JButton crearBoton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return b;
    }

    public void addTripulantesListener(ActionListener l) {
        btnTripulantes.addActionListener(l);
    }

    public void addIntercepcionListener(ActionListener l) {
        btnIntercepcion.addActionListener(l);
    }

    public void addRecoleccionListener(ActionListener l) {
        btnRecoleccion.addActionListener(l);
    }

    public void addRetornoListener(ActionListener l) {
        btnRetorno.addActionListener(l);
    }
}