package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;
import model.nave.Nave;

/** Pantalla 3: tablero en vivo con telemetría de la nave, alertas de combate y bitácora. */
public class TableroUniversoPanel extends JPanel {
    private TableroView tableroView;
    private BitacoraView bitacoraView;

    private final JLabel titulo = new JLabel("TABLERO");
    private final JLabel estadoNave = new JLabel("");
    private final JLabel bannerAlerta = new JLabel("Radar activo. Explorando sector...", SwingConstants.CENTER);
    private final JButton btnUniverso = new JButton("Abrir universo paralelo");

    private Timer timerHud;
    private int ultimoIndiceEvento = 0;

    public TableroUniversoPanel() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Color.BLACK);

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        estadoNave.setForeground(new Color(140, 230, 180));
        estadoNave.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

        // Fila 1: Título de la misión a la izquierda y botón a la derecha
        JPanel filaSuperior = new JPanel(new BorderLayout(10, 0));
        filaSuperior.setOpaque(false);
        filaSuperior.add(titulo, BorderLayout.CENTER);
        filaSuperior.add(btnUniverso, BorderLayout.EAST);

        // Fila 2: Telemetría de recursos debajo, sin riesgo de superposición
        JPanel barraSuperior = new JPanel(new GridLayout(2, 1, 0, 4));
        barraSuperior.setOpaque(false);
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        barraSuperior.add(filaSuperior);
        barraSuperior.add(estadoNave);
        add(barraSuperior, BorderLayout.NORTH);

        // Banner inferior para notificaciones visuales en vivo
        bannerAlerta.setOpaque(true);
        bannerAlerta.setBackground(new Color(22, 26, 38));
        bannerAlerta.setForeground(new Color(190, 210, 240));
        bannerAlerta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        bannerAlerta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 60, 85)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        add(bannerAlerta, BorderLayout.SOUTH);
    }

    /** Actualiza el encabezado superior con el nombre y destino de la misión actual. */
    public void setTitulo(String texto) {
        titulo.setText(texto);
    }

    /** Muestra el tablero en el centro con HUD de recursos y banner de eventos en tiempo real. */
    public void mostrarTablero(
            Tablero tablero,
            String tipoNave,
            Nave nave,
            Posicion destinoMision,
            String tipoMision,
            Bitacora bitacora,
            Set<Posicion> reveladas) {
        desconectar();
        tableroView = new TableroView(tablero, tipoNave, destinoMision, tipoMision, reveladas);
        bitacoraView = new BitacoraView(bitacora);
        add(tableroView, BorderLayout.CENTER);
        add(bitacoraView, BorderLayout.EAST);

        ultimoIndiceEvento = bitacora.getEventos().size();
        bannerAlerta.setBackground(new Color(22, 26, 38));
        bannerAlerta.setForeground(new Color(190, 210, 240));
        bannerAlerta.setText("Travesía iniciada. Escaneando sector...");

        actualizarHud(nave, bitacora);
        timerHud = new Timer(200, e -> actualizarHud(nave, bitacora));
        timerHud.start();

        revalidate();
        repaint();
    }

    private void actualizarHud(Nave nave, Bitacora bitacora) {
        if (nave != null) {
            estadoNave.setText(String.format(
                    "Combustible: %d | Energía: %d | Desgaste: %d | Gemas: %d | Motor: %s",
                    nave.getCombustible(),
                    nave.getEnergia(),
                    nave.getDesgaste(),
                    nave.getGemas(),
                    nave.getEstadoMotor()));
        }

        if (bitacora != null) {
            List<RegitroBitacora> eventos = bitacora.getEventos();
            while (ultimoIndiceEvento < eventos.size()) {
                String desc = eventos.get(ultimoIndiceEvento).getDescripcion();
                ultimoIndiceEvento++;
                procesarNotificacionVisual(desc);
            }
        }
    }

    private void procesarNotificacionVisual(String desc) {
        if (desc == null) return;
        String lower = desc.toLowerCase(Locale.ROOT);

        if (lower.contains("venció") || lower.contains("vencio")) {
            bannerAlerta.setBackground(new Color(20, 95, 45));
            bannerAlerta.setForeground(new Color(210, 255, 220));
            bannerAlerta.setText("¡VICTORIA EN COMBATE! " + desc);
        } else if (lower.contains("fue destruida") || lower.contains("asteroide") || lower.contains("rechazad") || lower.contains("fallo")) {
            bannerAlerta.setBackground(new Color(130, 25, 25));
            bannerAlerta.setForeground(new Color(255, 220, 220));
            bannerAlerta.setText("¡ALERTA CRÍTICA! " + desc);
        } else if (lower.contains("pacto de paz") || lower.contains("restos de una nave")) {
            bannerAlerta.setBackground(new Color(110, 80, 15));
            bannerAlerta.setForeground(new Color(255, 240, 180));
            bannerAlerta.setText("ENCUENTRO EN EL SECTOR: " + desc);
        } else if (lower.contains("estación") || lower.contains("estacion") || lower.contains("recarga") || lower.contains("objetivo alcanzado") || lower.contains("regreso")) {
            bannerAlerta.setBackground(new Color(20, 75, 115));
            bannerAlerta.setForeground(new Color(200, 240, 255));
            bannerAlerta.setText("EVENTO DE MISIÓN: " + desc);
        } else if (lower.contains("salto warp")) {
            bannerAlerta.setBackground(new Color(65, 35, 110));
            bannerAlerta.setForeground(new Color(230, 210, 255));
            bannerAlerta.setText("MOTOR WARP: " + desc);
        }
    }

    /** Quita el tablero y deja de observarlo. Llamar antes de salir de esta pantalla. */
    public void desconectar() {
        if (timerHud != null) {
            timerHud.stop();
            timerHud = null;
        }
        if (tableroView != null) {
            tableroView.desconectar();
            remove(tableroView);
            tableroView = null;
        }
        if (bitacoraView != null) {
            bitacoraView.desconectar();
            remove(bitacoraView);
            bitacoraView = null;
        }
    }

    public void addUniversoListener(ActionListener l) {
        btnUniverso.addActionListener(l);
    }
}