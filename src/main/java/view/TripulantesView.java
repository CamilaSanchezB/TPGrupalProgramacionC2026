package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.BorderFactory;

import controller.TripulantesController;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TripulantesView extends JPanel implements ITripulantes {

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
	private JButton btnVolver;

	public TripulantesView() {
		initialize();
	}

	/** Crea y ubica todos los componentes de la pantalla. */
	private void initialize() {
		ConfigView.aplicar();

		setOpaque(true);
		setBackground(ConfigView.FONDO_COMANDO);

		contentPane = new JPanel();
		contentPane.setLayout(new MigLayout("insets 20, gap 20", "[320!][grow]", "[grow]"));
		contentPane.setOpaque(true);
		contentPane.setBackground(ConfigView.FONDO_COMANDO);
		setLayout(new BorderLayout());
		add(contentPane);

		// ---------- Columna izquierda: la tripulacion ----------
		panelLista = new JPanel();
		panelLista.setLayout(new MigLayout("insets 0, gap 10", "[grow]", "[]10[grow][][]"));
		estilizarTarjeta(panelLista);
		contentPane.add(panelLista, "cell 0 0, grow");

		lblTripulacion = new JLabel("Tripulación");
		lblTripulacion.setFont(ConfigView.FUENTE_TITULO);
		lblTripulacion.setForeground(ConfigView.ACENTO_CIAN);
		panelLista.add(lblTripulacion, "cell 0 0");

		modelo = new DefaultListModel<String>();
		list = new JList<String>(modelo);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(ConfigView.FUENTE_ETIQUETA);
		list.setBackground(ConfigView.FONDO_COMANDO);
		list.setForeground(ConfigView.TEXTO_CLARO);
		list.setSelectionBackground(ConfigView.ACENTO);
		list.setSelectionForeground(Color.WHITE);
		scrollLista = new JScrollPane(list);
		scrollLista.setBorder(BorderFactory.createLineBorder(ConfigView.BORDE_COMANDO));
		scrollLista.getViewport().setBackground(ConfigView.FONDO_COMANDO);
		panelLista.add(scrollLista, "cell 0 1, grow");

		// Botones que operan sobre la lista, debajo de ella.
		panelBotonesLista = new JPanel();
		panelBotonesLista.setLayout(new MigLayout("insets 0, gap 10", "[grow][grow]", "[36!]"));
		panelBotonesLista.setOpaque(false);
		panelLista.add(panelBotonesLista, "cell 0 2, growx");

		btnDeseleccionar = new JButton("Deseleccionar");
		btnDeseleccionar.setActionCommand("DESELECCIONAR");
		estilizarBotonSecundario(btnDeseleccionar);
		panelBotonesLista.add(btnDeseleccionar, "cell 0 0, grow");

		btnLiquidar = new JButton("Liquidar todos");
		btnLiquidar.setActionCommand("LIQUIDAR");
		ConfigView.primario(btnLiquidar);
		panelBotonesLista.add(btnLiquidar, "cell 1 0, grow");

		// Volver al main: el listener lo engancha quien arma el flujo (view.Main).
		btnVolver = new JButton("Volver");
		estilizarBotonSecundario(btnVolver);
		panelLista.add(btnVolver, "cell 0 3, growx, h 36!");

		// ---------- Columna derecha: formulario y detalle ----------
		panelCentro = new JPanel();
		panelCentro.setLayout(new MigLayout("insets 0, gap 10", "[grow]", "[]18[]10[grow]"));
		estilizarTarjeta(panelCentro);
		contentPane.add(panelCentro, "cell 1 0, grow");

		panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0, gap 14 10", "[110!][300:560:700,grow]",
				"[]12[32!][32!][32!][32!][32!]16[]"));
		panelCentro.add(panel, "cell 0 0, growx");
		panel.setOpaque(false);

		lblDatos = new JLabel("Datos del tripulante");
		lblDatos.setFont(ConfigView.FUENTE_TITULO);
		lblDatos.setForeground(ConfigView.ACENTO_CIAN);
		panel.add(lblDatos, "cell 0 0 2 1");

		lblNombre = new JLabel("Nombre");
		lblNombre.setFont(ConfigView.FUENTE_ETIQUETA);
		lblNombre.setForeground(ConfigView.TEXTO_CLARO);
		panel.add(lblNombre, "cell 0 1");

		textoNombre = new JTextField();
		textoNombre.setColumns(10);
		panel.add(textoNombre, "cell 1 1, growx");

		lblAntiguedad = new JLabel("Antigüedad");
		lblAntiguedad.setFont(ConfigView.FUENTE_ETIQUETA);
		lblAntiguedad.setForeground(ConfigView.TEXTO_CLARO);
		panel.add(lblAntiguedad, "cell 0 2");

		textoAntiguedad = new JTextField();
		textoAntiguedad.setColumns(10);
		panel.add(textoAntiguedad, "cell 1 2, growx");

		lblCargo = new JLabel("Cargo");
		lblCargo.setFont(ConfigView.FUENTE_ETIQUETA);
		lblCargo.setForeground(ConfigView.TEXTO_CLARO);
		panel.add(lblCargo, "cell 0 3");

		comboCargo = new JComboBox<String>();
		comboCargo.setModel(new DefaultComboBoxModel<String>(CARGOS));
		panel.add(comboCargo, "cell 1 3, growx");

		lblOrigen = new JLabel("Origen");
		lblOrigen.setFont(ConfigView.FUENTE_ETIQUETA);
		lblOrigen.setForeground(ConfigView.TEXTO_CLARO);
		panel.add(lblOrigen, "cell 0 4");

		comboOrigen = new JComboBox<String>();
		comboOrigen.setModel(new DefaultComboBoxModel<String>(ORIGENES));
		panel.add(comboOrigen, "cell 1 4, growx");

		lblConsejos = new JLabel("Consejos");
		lblConsejos.setFont(ConfigView.FUENTE_ETIQUETA);
		panel.add(lblConsejos, "cell 0 5");

		textoConsejos = new JTextField();
		textoConsejos.setColumns(10);
		panel.add(textoConsejos, "cell 1 5, growx");

		panelBotonesForm = new JPanel();
		panelBotonesForm.setLayout(new MigLayout("insets 0, gap 10", "[110!][110!][110!]", "[36!]"));
		panelBotonesForm.setOpaque(false);
		panel.add(panelBotonesForm, "cell 1 6, alignx right");

		btnCrear = new JButton("Crear");
		btnCrear.setActionCommand("CREAR");
		ConfigView.primario(btnCrear);
		panelBotonesForm.add(btnCrear, "cell 0 0, grow");

		btnActualizar = new JButton("Actualizar");
		btnActualizar.setActionCommand("ACTUALIZAR");
		estilizarBotonSecundario(btnDeseleccionar);
		panelBotonesForm.add(btnActualizar, "cell 1 0, grow");

		btnEliminar = new JButton("Eliminar");
		btnEliminar.setActionCommand("ELIMINAR");
		estilizarBotonSecundario(btnDeseleccionar);
		panelBotonesForm.add(btnEliminar, "cell 2 0, grow");

		lblDetalle = new JLabel("Detalle de la liquidación");
		lblDetalle.setFont(ConfigView.FUENTE_TITULO);
		lblDetalle.setForeground(ConfigView.ACENTO_CIAN);
		panelCentro.add(lblDetalle, "cell 0 1");

		// Pantalla en blanco: ahi se escribe el detalle de la liquidacion.
		areaLiquidacion = new JTextArea();
		areaLiquidacion.setEditable(false);
		areaLiquidacion.setFont(ConfigView.FUENTE_DETALLE);
		areaLiquidacion.setBackground(ConfigView.FONDO_COMANDO);
		areaLiquidacion.setForeground(ConfigView.TEXTO_CLARO);
		areaLiquidacion.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
		areaLiquidacion.setRows(10);
		scrollLiquidacion = new JScrollPane(areaLiquidacion);
		scrollLiquidacion.setBorder(BorderFactory.createLineBorder(ConfigView.BORDE_COMANDO));
		scrollLiquidacion.getViewport().setBackground(ConfigView.FONDO_COMANDO);
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

	private static void estilizarTarjeta(JPanel panel) {
		panel.setOpaque(true);
		panel.setBackground(ConfigView.TARJETA_COMANDO);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ConfigView.BORDE_COMANDO),
				BorderFactory.createEmptyBorder(14, 14, 14, 14)));
	}

	private static void estilizarBotonSecundario(JButton boton) {
		boton.setBackground(ConfigView.BOTON_SECUNDARIO_COMANDO);
		boton.setForeground(ConfigView.TEXTO_CLARO);
		boton.setFocusPainted(false);
		boton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ConfigView.BORDE_COMANDO),
				BorderFactory.createEmptyBorder(6, 12, 6, 12)));
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

	/** Listener del botón Volver (lo usa view.Main para cambiar de pantalla). */
	public void addVolverListener(ActionListener listener) {
		btnVolver.addActionListener(listener);
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