package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Vista de usuario de la plataforma de baterias.
 *
 *  - Izquierda: naves generadoras (un rectangulo por nave) + boton para agregar.
 *  - Centro:    baterias (un rectangulo por bateria) + log debajo.
 *  - Derecha:   naves consumidoras (un rectangulo por nave) + boton para agregar.
 *  - Arriba:    boton "Volver".
 *
 * Todos los metodos publicos pueden llamarse desde cualquier hilo (por ejemplo,
 * desde el hilo de cada nave): internamente se ejecutan en el hilo de Swing (EDT).
 */
public class VistaNaves extends JFrame {

    private static final Color COLOR_GENERADORA = new Color(190, 215, 255);
    private static final Color COLOR_CONSUMIDORA = new Color(255, 218, 170);

    private final JButton btnVolver = new JButton("< Volver");
    private final JButton btnAgregarGeneradora = new JButton("Agregar nave generadora");
    private final JButton btnAgregarConsumidora = new JButton("Agregar nave consumidora");

    private final JPanel listaGeneradoras = new JPanel(new GridLayout(0, 1, 0, 6));
    private final JPanel listaConsumidoras = new JPanel(new GridLayout(0, 1, 0, 6));
    private final PanelBaterias panelBaterias = new PanelBaterias();
    private final JLabel lblBaterias = new JLabel("Baterias: 0", SwingConstants.CENTER);
    private final JTextArea log = new JTextArea();

    public VistaNaves() {
        super("Plataforma de baterias");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Arriba: boton volver
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(btnVolver, BorderLayout.WEST);
        add(norte, BorderLayout.NORTH);

        // Izquierda: generadoras
        add(armarColumnaNaves("Naves generadoras", listaGeneradoras, btnAgregarGeneradora),
                BorderLayout.WEST);

        // Derecha: consumidoras
        add(armarColumnaNaves("Naves consumidoras", listaConsumidoras, btnAgregarConsumidora),
                BorderLayout.EAST);

        // Centro: baterias arriba, log abajo
        add(armarCentro(), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Armado de la interfaz
    // ------------------------------------------------------------------

    private JPanel armarColumnaNaves(String titulo, JPanel lista, JButton boton) {
        JPanel contenedorLista = new JPanel(new BorderLayout());
        contenedorLista.add(lista, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(contenedorLista);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel columna = new JPanel(new BorderLayout(0, 6));
        columna.setPreferredSize(new Dimension(220, 0));
        columna.add(scroll, BorderLayout.CENTER);
        columna.add(boton, BorderLayout.SOUTH);
        return columna;
    }

    private JPanel armarCentro() {
        // Baterias
        JPanel baterias = new JPanel(new BorderLayout());
        baterias.setBorder(BorderFactory.createTitledBorder("Baterias"));
        lblBaterias.setFont(lblBaterias.getFont().deriveFont(Font.BOLD));
        baterias.add(lblBaterias, BorderLayout.NORTH);
        baterias.add(new JScrollPane(panelBaterias), BorderLayout.CENTER);

        // Log
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(log);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log"));

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 8));
        centro.add(baterias);
        centro.add(scrollLog);
        return centro;
    }

    // ------------------------------------------------------------------
    // Listeners (los usa el controlador)
    // ------------------------------------------------------------------

    public void addVolverListener(ActionListener l) {
        btnVolver.addActionListener(l);
    }

    public void addAgregarGeneradoraListener(ActionListener l) {
        btnAgregarGeneradora.addActionListener(l);
    }

    public void addAgregarConsumidoraListener(ActionListener l) {
        btnAgregarConsumidora.addActionListener(l);
    }

    // ------------------------------------------------------------------
    // Metodos que usa el controlador / las naves para actualizar la vista
    // ------------------------------------------------------------------

    /** Agrega el rectangulo de una nave generadora a la columna izquierda. */
    public void agregarNaveGeneradora(String nombre) {
        enEDT(() -> agregarNave(listaGeneradoras, nombre, COLOR_GENERADORA));
    }

    /** Agrega el rectangulo de una nave consumidora a la columna derecha. */
    public void agregarNaveConsumidora(String nombre) {
        enEDT(() -> agregarNave(listaConsumidoras, nombre, COLOR_CONSUMIDORA));
    }

    /** Una nave deposito una bateria: aparece un rectangulo y se registra en el log. */
    public void naveGeneroBateria(String nombreNave) {
        enEDT(() -> {
            panelBaterias.setCantidad(panelBaterias.getCantidad() + 1);
            actualizarContador();
            escribirLog(nombreNave + " genero una bateria");
        });
    }

    /** Una nave consumio una bateria: desaparece un rectangulo y se registra en el log. */
    public void naveConsumioBateria(String nombreNave) {
        enEDT(() -> {
            if (panelBaterias.getCantidad() > 0) {
                panelBaterias.setCantidad(panelBaterias.getCantidad() - 1);
            }
            actualizarContador();
            escribirLog(nombreNave + " consumio una bateria");
        });
    }

    /** Escribe una linea libre en el log. */
    public void agregarLog(String texto) {
        enEDT(() -> escribirLog(texto));
    }

    // ------------------------------------------------------------------
    // Internos (siempre se ejecutan en el EDT)
    // ------------------------------------------------------------------

    private void agregarNave(JPanel lista, String nombre, Color color) {
        JLabel nave = new JLabel(nombre, SwingConstants.CENTER);
        nave.setOpaque(true);
        nave.setBackground(color);
        nave.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        nave.setPreferredSize(new Dimension(0, 36));
        lista.add(nave);
        lista.revalidate();
        lista.repaint();
    }

    private void actualizarContador() {
        lblBaterias.setText("Baterias: " + panelBaterias.getCantidad());
    }

    private void escribirLog(String texto) {
        log.append(texto + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private static void enEDT(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    // ------------------------------------------------------------------
    // Panel que dibuja un rectangulo por bateria, acomodados en filas
    // ------------------------------------------------------------------

    private static class PanelBaterias extends JPanel implements Scrollable {
        private static final int ANCHO = 44;
        private static final int ALTO = 24;
        private static final int GAP = 8;

        private int cantidad = 0;

        int getCantidad() {
            return cantidad;
        }

        void setCantidad(int cantidad) {
            this.cantidad = cantidad;
            revalidate();
            repaint();
        }

        private int columnas(int anchoDisponible) {
            return Math.max(1, (anchoDisponible - GAP) / (ANCHO + GAP));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cols = columnas(getWidth());
            for (int i = 0; i < cantidad; i++) {
                int x = GAP + (i % cols) * (ANCHO + GAP);
                int y = GAP + (i / cols) * (ALTO + GAP);
                g.setColor(new Color(90, 200, 90));
                g.fillRect(x, y, ANCHO, ALTO);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, ANCHO, ALTO);
                g.fillRect(x + ANCHO, y + ALTO / 3, 3, ALTO / 3); // "polo" de la bateria
            }
        }

        @Override
        public Dimension getPreferredSize() {
            int ancho = getParent() != null ? getParent().getWidth() : 300;
            int cols = columnas(ancho);
            int filas = Math.max(1, (cantidad + cols - 1) / cols);
            return new Dimension(ancho, GAP + filas * (ALTO + GAP));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(300, 150);
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return ALTO + GAP;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return visibleRect.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
