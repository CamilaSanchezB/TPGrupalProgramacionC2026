package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.nave.Nave;
import model.tripulantes.Alferez;
import model.tripulantes.Capitan;
import model.tripulantes.Consejero;
import model.tripulantes.Marciano;
import model.tripulantes.Teniente;
import model.tripulantes.Terricola;
import model.tripulantes.Tripulante;
import model.tripulantes.Vulcano;
import view.ITripulantes;

public class TripulantesController implements ActionListener, ListSelectionListener {
	private Nave nave;
	private LiquidadorHaberes liq;
	private ITripulantes vista;
	private Tripulante nuevoTripulante;
	
	public TripulantesController(Nave nave, LiquidadorHaberes liq) {
		this.nave = nave;
		this.liq = liq;
	}

	public ITripulantes getVista() {
		return vista;
	}

	public void setVista(ITripulantes vista) {
		this.vista = vista;
		this.vista.setControlador(this);
		this.vista.setActionListener(this);
		this.vista.setSeleccionTripulanteListener(this);
		cargarTripulantesEnVista();
	}

	private void cargarTripulantesEnVista() {
		List<String> tripulacion = new ArrayList<>();
		for (Tripulante t : nave.getTripulantes()) {
			tripulacion.add(t.getNombre() + " - " + t.getCargo());
		}
		vista.cargaTripulantes(tripulacion);
	}

	public List<Tripulante> getTripulantes() {
		return nave.getTripulantes();
	}

	public Nave getNave() {
		return nave;
	}

	public LiquidadorHaberes getLiq() {
		return liq;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ITripulantes.ACTUALIZAR.equals(e.getActionCommand())) {
			actualizarTripulante();
		} else if (ITripulantes.ELIMINAR.equals(e.getActionCommand())) {
			eliminarTripulante();
		}
	}

	/** Reemplaza al tripulante seleccionado por uno nuevo con los datos de la vista. */
	private void actualizarTripulante() {
		Tripulante seleccionado = getSeleccionado("actualizar");
		if (seleccionado == null) {
			return;
		}
		String nombre = vista.getNombre().trim();
		if (nombre.isEmpty()) {
			vista.mostrarError("El nombre no puede estar vacio.");
			return;
		}
		int antiguedad;
		try {
			antiguedad = Integer.parseInt(vista.getAntiguedad().trim());
		} catch (NumberFormatException ex) {
			vista.mostrarError("La antiguedad debe ser un numero entero.");
			return;
		}
		if (antiguedad < 0) {
			vista.mostrarError("La antiguedad no puede ser negativa.");
			return;
		}
		nave.editarTripulante(seleccionado.getNombre(),
				crearTripulante(nombre, antiguedad, vista.getCargo(), vista.getOrigen()));
		cargarTripulantesEnVista();
	}

	private void eliminarTripulante() {
		Tripulante seleccionado = getSeleccionado("eliminar");
		if (seleccionado == null) {
			return;
		}
		nave.eliminarTripulante(seleccionado.getNombre());
		cargarTripulantesEnVista();
	}

	/** Devuelve el tripulante seleccionado en la vista o null, avisando el error. */
	private Tripulante getSeleccionado(String accion) {
		int index = vista.getTripulanteSeleccionado();
		if (index < 0 || index >= nave.getTripulantes().size()) {
			vista.mostrarError("Seleccione un tripulante para " + accion + ".");
			return null;
		}
		return nave.getTripulantes().get(index);
	}

	/** Arma un tripulante del cargo indicado, decorado con su origen si tiene uno. */
	private Tripulante crearTripulante(String nombre, int antiguedad, String cargo, String origen) {
		Tripulante tripulante;
		switch (cargo) {
		case "Capitan":
			tripulante = new Capitan(nombre, antiguedad);
			break;
		case "Alferez":
			tripulante = new Alferez(nombre, antiguedad);
			break;
		case "Consejero":
			tripulante = new Consejero(nombre, antiguedad);
			break;
		case "Teniente":
			tripulante = new Teniente(nombre, antiguedad);
			break;
		default:
			throw new IllegalArgumentException("Cargo desconocido: " + cargo);
		}
		switch (origen) {
		case "Terricola":
			tripulante = new Terricola(tripulante);
			break;
		case "Marciano":
			tripulante = new Marciano(tripulante);
			break;
		case "Vulcano":
			tripulante = new Vulcano(tripulante);
			break;
		default:
			break;
		}
		return tripulante;
	}

	private void actualizarVistaTripulanteSeleccionado() {
		int index = vista.getTripulanteSeleccionado();
		if (index < 0 || index >= nave.getTripulantes().size()) {
			return;
		}
		vista.mostrarDatosTripulante(nave.getTripulantes().get(index));
	}
	
	/** Al cambiar el tripulante seleccionado se muestran sus datos. */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        //vista.clearError();
        actualizarVistaTripulanteSeleccionado();
    }
	
}