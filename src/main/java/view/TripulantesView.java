package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import controller.TripulantesController;
import model.tripulantes.Tripulante;
import javax.swing.JButton;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class TripulantesView extends JFrame implements ITripulantes {

	private JPanel contentPane;
	private JPanel panel;
	private JTextField textoNombre;
	private JTextField textoOrigen;
	private JTextField textoAntiguedad;
	private JTextField textoCargo;

	private TripulantesController controlador;
	private DefaultListModel<String> modelo;
	private JList<String> list;
	private ListSelectionListener seleccionTripulanteListener;
	private JButton btnActualizar;
	private JButton btnCrear;
	private JButton btnEliminar;
	private JLabel lblNombre;
	private JLabel lblAntiguedad;
	private JLabel lblCargo;
	private JLabel lblOrigen;
	
	public TripulantesView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		modelo = new DefaultListModel<>();
		list = new JList<>(modelo);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(150, 0));
		contentPane.add(scroll, BorderLayout.WEST);

		panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblNombre = new JLabel("Nombre");
		panel.add(lblNombre);

		textoNombre = new JTextField();
		panel.add(textoNombre);
		textoNombre.setColumns(10);
		
		lblAntiguedad = new JLabel("Antigüedad");
		panel.add(lblAntiguedad);

		textoAntiguedad = new JTextField();
		panel.add(textoAntiguedad);
		textoAntiguedad.setColumns(10);
		
		lblCargo = new JLabel("Cargo");
		panel.add(lblCargo);

		textoCargo = new JTextField();
		panel.add(textoCargo);
		textoCargo.setColumns(10);
		
		lblOrigen = new JLabel("Origen");
		panel.add(lblOrigen);

		textoOrigen = new JTextField();
		panel.add(textoOrigen);
		textoOrigen.setColumns(10);
		
		btnCrear = new JButton("Crear");
		panel.add(btnCrear);
		btnCrear.setActionCommand("CREAR");
		
		btnActualizar = new JButton("Actualizar");
		panel.add(btnActualizar);
		btnActualizar.setActionCommand("ACTUALIZAR");
		
		btnEliminar = new JButton("Eliminar");
		panel.add(btnEliminar);
		btnEliminar.setActionCommand("ELIMINAR");
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

	public void mostrarDatosTripulante(Tripulante tripulante) {
		this.textoNombre.setText(tripulante.getNombre());
		
	}
	
}