package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.formdev.flatlaf.FlatLightLaf;

import controller.TripulantesController;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TripulantesView extends JFrame implements ITripulantes {

	/** Azul de acento para las acciones principales. */
	private static final Color ACENTO = new Color(2, 132, 199);
	private static final Color TITULO = new Color(15, 23, 42);
	private static final String FAMILIA = "Segoe UI";
	private static final Font FUENTE_TITULO = new Font(FAMILIA, Font.BOLD, 16);
	private static final Font FUENTE_ETIQUETA = new Font(FAMILIA, Font.PLAIN, 13);
	private static final Font FUENTE_DETALLE = new Font("Consolas", Font.PLAIN, 13);

	/*
	 * El Look and Feel se aplica al cargar la clase, antes de que exista cualquier
	 * componente, para que valga sin importar quien instancie la vista.
	 */
	static {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
			// Los redondeos van despues de instalar el Look and Feel, no antes.
			UIManager.put("Button.arc", 12);
			UIManager.put("Component.arc", 10);
			UIManager.put("TextComponent.arc", 10);
			UIManager.put("Component.focusWidth", 1);
			UIManager.put("Component.accentColor", ACENTO);
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("No se pudo aplicar FlatLaf, sigue el Look and Feel por defecto: "
					+ e.getMessage());
		}
	}

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panelLista;
	private JPanel panelBotonesLista;
	private JPanel panelBotonesForm;
	private JPanel panelCentro;
	private JTextArea areaLiquidacion;
	private JScrollPane scrollLiquidacion;
	private JScrollPane scrollLista;
	private JTextField textoNombre;
	private JTextField textoAntiguedad;
	private JComboBox<String> comboCargo;
	private JComboBox<String> comboOrigen;

	private TripulantesController controlador;
	private DefaultListModel<String> modelo;
	private JList<String> list;
	private ListSelectionListener seleccionTripulanteListener;
	private JButton btnActualizar;
	private JButton btnCrear;
	private JButton btnEliminar;
	private JLabel lblTripulacion;
	private JLabel lblDatos;
	private JLabel lblDetalle;
	private JLabel lblNombre;
	private JLabel lblAntiguedad;
	private JLabel lblCargo;
	private JLabel lblOrigen;
	private JLabel lblConsejos;
	private JTextField textoConsejos;
	private JButton btnDeseleccionar;
	private JButton btnLiquidar;

	public TripulantesView() {
		initialize();
	}

	/** Crea y ubica todos los componentes de la ventana. */
	private void initialize() {
		setTitle("Tripulación");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 640);
		// Abre maximizada; setBounds queda como el tamanio al restaurarla.
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		contentPane = new JPanel();
		contentPane.setLayout(new MigLayout("insets 20, gap 20", "[320!][grow]", "[grow]"));
		setContentPane(contentPane);

		// ---------- Columna izquierda: la tripulacion ----------
		panelLista = new JPanel();
		panelLista.setLayout(new MigLayout("insets 0, gap 10", "[grow]", "[]10[grow][]"));
		contentPane.add(panelLista, "cell 0 0, grow");

		lblTripulacion = new JLabel("Tripulación");
		lblTripulacion.setFont(FUENTE_TITULO);
		lblTripulacion.setForeground(TITULO);
		panelLista.add(lblTripulacion, "cell 0 0");

		modelo = new DefaultListModel<String>();
		list = new JList<String>(modelo);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(FUENTE_ETIQUETA);
		scrollLista = new JScrollPane(list);
		panelLista.add(scrollLista, "cell 0 1, grow");

		// Botones que operan sobre la lista, debajo de ella.
		panelBotonesLista = new JPanel();
		panelBotonesLista.setLayout(new MigLayout("insets 0, gap 10", "[grow][grow]", "[36!]"));
		panelLista.add(panelBotonesLista, "cell 0 2, growx");

		btnDeseleccionar = new JButton("Deseleccionar");
		btnDeseleccionar.setActionCommand("DESELECCIONAR");
		panelBotonesLista.add(btnDeseleccionar, "cell 0 0, grow");

		btnLiquidar = new JButton("Liquidar todos");
		btnLiquidar.setActionCommand("LIQUIDAR");
		btnLiquidar.setBackground(ACENTO);
		btnLiquidar.setForeground(Color.WHITE);
		panelBotonesLista.add(btnLiquidar, "cell 1 0, grow");

		// ---------- Columna derecha: formulario y detalle ----------
		panelCentro = new JPanel();
		panelCentro.setLayout(new MigLayout("insets 0, gap 10", "[grow]", "[]18[]10[grow]"));
		contentPane.add(panelCentro, "cell 1 0, grow");

		panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0, gap 14 10", "[110!][300:560:700,grow]",
				"[]12[32!][32!][32!][32!][32!]16[]"));
		panelCentro.add(panel, "cell 0 0, growx");

		lblDatos = new JLabel("Datos del tripulante");
		lblDatos.setFont(FUENTE_TITULO);
		lblDatos.setForeground(TITULO);
		panel.add(lblDatos, "cell 0 0 2 1");

		lblNombre = new JLabel("Nombre");
		lblNombre.setFont(FUENTE_ETIQUETA);
		panel.add(lblNombre, "cell 0 1");

		textoNombre = new JTextField();
		textoNombre.setColumns(10);
		panel.add(textoNombre, "cell 1 1, growx");

		lblAntiguedad = new JLabel("Antigüedad");
		lblAntiguedad.setFont(FUENTE_ETIQUETA);
		panel.add(lblAntiguedad, "cell 0 2");

		textoAntiguedad = new JTextField();
		textoAntiguedad.setColumns(10);
		panel.add(textoAntiguedad, "cell 1 2, growx");

		lblCargo = new JLabel("Cargo");
		lblCargo.setFont(FUENTE_ETIQUETA);
		panel.add(lblCargo, "cell 0 3");

		comboCargo = new JComboBox<String>();
		comboCargo.setModel(new DefaultComboBoxModel<String>(CARGOS));
		panel.add(comboCargo, "cell 1 3, growx");

		lblOrigen = new JLabel("Origen");
		lblOrigen.setFont(FUENTE_ETIQUETA);
		panel.add(lblOrigen, "cell 0 4");

		comboOrigen = new JComboBox<String>();
		comboOrigen.setModel(new DefaultComboBoxModel<String>(ORIGENES));
		panel.add(comboOrigen, "cell 1 4, growx");

		lblConsejos = new JLabel("Consejos");
		lblConsejos.setFont(FUENTE_ETIQUETA);
		panel.add(lblConsejos, "cell 0 5");

		textoConsejos = new JTextField();
		textoConsejos.setColumns(10);
		panel.add(textoConsejos, "cell 1 5, growx");

		panelBotonesForm = new JPanel();
		panelBotonesForm.setLayout(new MigLayout("insets 0, gap 10", "[110!][110!][110!]", "[36!]"));
		panel.add(panelBotonesForm, "cell 1 6, alignx right");

		btnCrear = new JButton("Crear");
		btnCrear.setActionCommand("CREAR");
		btnCrear.setBackground(ACENTO);
		btnCrear.setForeground(Color.WHITE);
		panelBotonesForm.add(btnCrear, "cell 0 0, grow");

		btnActualizar = new JButton("Actualizar");
		btnActualizar.setActionCommand("ACTUALIZAR");
		panelBotonesForm.add(btnActualizar, "cell 1 0, grow");

		btnEliminar = new JButton("Eliminar");
		btnEliminar.setActionCommand("ELIMINAR");
		panelBotonesForm.add(btnEliminar, "cell 2 0, grow");

		lblDetalle = new JLabel("Detalle de la liquidación");
		lblDetalle.setFont(FUENTE_TITULO);
		lblDetalle.setForeground(TITULO);
		panelCentro.add(lblDetalle, "cell 0 1");

		// Pantalla en blanco: ahi se escribe el detalle de la liquidacion.
		areaLiquidacion = new JTextArea();
		areaLiquidacion.setEditable(false);
		areaLiquidacion.setFont(FUENTE_DETALLE);
		areaLiquidacion.setRows(10);
		scrollLiquidacion = new JScrollPane(areaLiquidacion);
		panelCentro.add(scrollLiquidacion, "cell 0 2, grow");

		// Sin seleccion: arranca habilitado Crear y deshabilitados Actualizar y Eliminar.
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				actualizarBotonesSegunSeleccion();
			}
		});
		actualizarBotonesSegunSeleccion();

		// El bloqueo de Consejos sigue al cargo elegido, haya o no un tripulante seleccionado.
		comboCargo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarBloqueoConsejos();
			}
		});
		actualizarBloqueoConsejos();
	}

	/**
	 * La cantidad de consejos solo aplica al cargo Consejero: con cualquier otro cargo
	 * el campo queda vacio y bloqueado. Vale igual para un tripulante seleccionado
	 * (el cargo lo escribe el controlador) y para un alta (lo elige el usuario en el combo).
	 */
	private void actualizarBloqueoConsejos() {
		boolean esConsejero = CARGO_CONSEJERO.equals(comboCargo.getSelectedItem());
		if (!esConsejero) {
			textoConsejos.setText("");
		}
		textoConsejos.setEnabled(esConsejero);
		lblConsejos.setEnabled(esConsejero);
	}

	/**
	 * Actualizar y Eliminar solo tienen sentido con un tripulante seleccionado,
	 * asi que se habilitan unicamente cuando la lista tiene exactamente uno.
	 * Crear es el caso inverso: se habilita solo cuando no hay ninguno seleccionado,
	 * porque el formulario pasa a describir un tripulante nuevo.
	 */
	private void actualizarBotonesSegunSeleccion() {
		boolean haySeleccion = list.getSelectedIndices().length == 1;
		btnActualizar.setEnabled(haySeleccion);
		btnEliminar.setEnabled(haySeleccion);
		btnCrear.setEnabled(!haySeleccion);
	}

	@Override
	public void setControlador(TripulantesController c) {
		this.controlador = c;
	}

	@Override
	public void arranca() {
		setVisible(true);
	}

	@Override
	public void setActionListener(ActionListener listener) {
		this.btnCrear.addActionListener(listener);
		this.btnActualizar.addActionListener(listener);
		this.btnEliminar.addActionListener(listener);
		this.btnDeseleccionar.addActionListener(listener);
		this.btnLiquidar.addActionListener(listener);
	}

	@Override
	public void cargaTripulantes(List<String> tripulantes) {

		int seleccionada = list.getSelectedIndex();

		// Se quita el listener para que repoblar la lista no dispare eventos de selección.
		if (seleccionTripulanteListener != null) {
			list.removeListSelectionListener(seleccionTripulanteListener);
		}
		modelo.clear();
		for (String t : tripulantes) {
			modelo.addElement(t);
		}
		if (seleccionada >= 0 && seleccionada < tripulantes.size()) {
			list.setSelectedIndex(seleccionada);
		}
		if (seleccionTripulanteListener != null) {
			list.addListSelectionListener(seleccionTripulanteListener);
		}
		actualizarBotonesSegunSeleccion();
	}

	@Override
	public void escribeDatos() {
		// TODO
	}

	@Override
	public void setSeleccionTripulanteListener(ListSelectionListener listener) {
		if (seleccionTripulanteListener != null) {
			list.removeListSelectionListener(seleccionTripulanteListener);
		}
		seleccionTripulanteListener = listener;
		list.addListSelectionListener(listener);
	}

	@Override
	public int getTripulanteSeleccionado() {
		return list.getSelectedIndex();
	}

	@Override
	public void setNombreSeleccionado(String nombre) {
		textoNombre.setText(nombre);
	}

	@Override
	public void setAntiguedadSeleccionado(int antiguedad) {
		textoAntiguedad.setText(String.valueOf(antiguedad));
	}

	@Override
	public void setCargoSeleccionado(String cargo) {
		comboCargo.setSelectedItem(cargo);
		actualizarBloqueoConsejos();
	}

	@Override
	public void setOrigenSeleccionado(String origen) {
		comboOrigen.setSelectedItem(origen);
	}

	@Override
	public void setCantidadConsejosSeleccionado(Integer cantidadConsejos) {
		textoConsejos.setText(cantidadConsejos == null ? "" : String.valueOf(cantidadConsejos));
	}

	@Override
	public String getNombre() {
		return textoNombre.getText();
	}

	@Override
	public String getAntiguedad() {
		return textoAntiguedad.getText();
	}

	@Override
	public String getCargo() {
		return (String) comboCargo.getSelectedItem();
	}

	@Override
	public String getOrigen() {
		return (String) comboOrigen.getSelectedItem();
	}

	@Override
	public String getCantidadConsejos() {
		return textoConsejos.getText();
	}

	@Override
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void limpiarSeleccion() {
		list.clearSelection();
		actualizarBotonesSegunSeleccion();
		textoNombre.setText("");
		textoAntiguedad.setText("");
		comboCargo.setSelectedIndex(0);
		comboOrigen.setSelectedIndex(0);
		actualizarBloqueoConsejos();
		areaLiquidacion.setText("");
	}

	@Override
	public void mostrarLiquidacion(String recibo) {
		areaLiquidacion.setText(recibo);
		areaLiquidacion.setCaretPosition(0);
	}

}
