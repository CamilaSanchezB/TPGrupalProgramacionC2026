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
import model.tripulantes.Decorator;
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
			tripulacion.add(t.getNombre() + " - " + t.getCargo()+ " " + t.getOrigen());
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
		if (ITripulantes.CREAR.equals(e.getActionCommand())) {
			crearNuevoTripulante();
		} else if (ITripulantes.ACTUALIZAR.equals(e.getActionCommand())) {
			actualizarTripulante();
		} else if (ITripulantes.ELIMINAR.equals(e.getActionCommand())) {
			eliminarTripulante();
		} else if (ITripulantes.DESELECCIONAR.equals(e.getActionCommand())) {
			vista.limpiarSeleccion();
		} else if (ITripulantes.LIQUIDAR.equals(e.getActionCommand())) {
			liquidarTripulacion();
		}
	}

	/**
	 * Da de alta un tripulante con los datos del formulario y lo agrega a la nave.
	 * Valida para cumplir precondiciones
	 */
	private void crearNuevoTripulante() {
		String nombre = leerNombre();
		if (nombre == null) {
			return;
		}
		if (nombreRepetido(nombre, null)) {
			return;
		}
		Integer antiguedad = leerAntiguedad();
		if (antiguedad == null) {
			return;
		}
		Integer consejos = leerCantidadConsejos(vista.getCargo());
		if (consejos == null) {
			return;
		}
		nave.asignarTripulante(
				crearTripulante(nombre, antiguedad, vista.getCargo(), vista.getOrigen(), consejos));
		cargarTripulantesEnVista();
		vista.limpiarSeleccion();
	}

	/** Reemplaza al tripulante seleccionado por uno nuevo con los datos de la vista. */
	private void actualizarTripulante() {
		Tripulante seleccionado = getSeleccionado("actualizar");
		if (seleccionado == null) {
			return;
		}
		String nombre = leerNombre();
		if (nombre == null) {
			return;
		}
		if (nombreRepetido(nombre, seleccionado.getNombre())) {
			return;
		}
		Integer antiguedad = leerAntiguedad();
		if (antiguedad == null) {
			return;
		}
		Integer consejos = leerCantidadConsejos(vista.getCargo());
		if (consejos == null) {
			return;
		}
		nave.editarTripulante(seleccionado.getNombre(),
				crearTripulante(nombre, antiguedad, vista.getCargo(), vista.getOrigen(), consejos));
		cargarTripulantesEnVista();
	}

	/**
	 * Avisa y devuelve true si el nombre ya lo usa otro tripulante. Busca,
	 * edita y elimina por nombre tomando la primera coincidencia.
	 */
	private boolean nombreRepetido(String nombre, String nombreActual) {
		if (nombre.equals(nombreActual)) {
			return false;
		}
		if (nave.existeTripulante(nombre)) {
			vista.mostrarError("Ya hay un tripulante con el nombre " + nombre + ".");
			return true;
		}
		return false;
	}

	/** Devuelve el nombre del formulario sin espacios, o null avisando si esta vacio. */
	private String leerNombre() {
		String nombre = vista.getNombre().trim();
		if (nombre.isEmpty()) {
			vista.mostrarError("El nombre no puede estar vacio.");
			return null;
		}
		return nombre;
	}

	/** Devuelve la antiguedad del formulario, o null avisando si no es un entero >= 0. */
	private Integer leerAntiguedad() {
		int antiguedad;
		try {
			antiguedad = Integer.parseInt(vista.getAntiguedad().trim());
		} catch (NumberFormatException ex) {
			vista.mostrarError("La antiguedad debe ser un numero entero.");
			return null;
		}
		if (antiguedad < 0) {
			vista.mostrarError("La antiguedad no puede ser negativa.");
			return null;
		}
		return antiguedad;
	}

	/**
	 * Devuelve la cantidad de consejos del formulario, o null avisando si es invalida.
	 * Da 0 si el cargo no es consejero (el campo esta bloqueado) o si quedo vacio.
	 */
	private Integer leerCantidadConsejos(String cargo) {
		if (!ITripulantes.CARGO_CONSEJERO.equals(cargo)) {
			return 0;
		}
		String texto = vista.getCantidadConsejos().trim();
		if (texto.isEmpty()) {
			return 0;
		}
		int consejos;
		try {
			consejos = Integer.parseInt(texto);
		} catch (NumberFormatException ex) {
			vista.mostrarError("La cantidad de consejos debe ser un numero entero.");
			return null;
		}
		if (consejos < 0) {
			vista.mostrarError("La cantidad de consejos no puede ser negativa.");
			return null;
		}
		return consejos;
	}

	/**
	 * Liquida los haberes de toda la tripulacion. No necesita seleccion.
	 * Liquidar reinicia el periodo de cada tripulante, asi que despues se refrescan
	 * los datos en pantalla (al consejero le quedan 0 consejos).
	 */
	private void liquidarTripulacion() {
		vista.mostrarLiquidacion(liq.liquidar(nave));
		actualizarVistaTripulanteSeleccionado();
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

	/**
	 * Arma un tripulante del cargo indicado, decorado con su origen si tiene uno.
	 * cantidadConsejos solo lo usa el Consejero; los demas cargos lo ignoran.
	 */
	private Tripulante crearTripulante(String nombre, int antiguedad, String cargo, String origen,
			int cantidadConsejos) {
		Tripulante tripulante;
		switch (cargo) {
		case "Capitan":
			tripulante = new Capitan(nombre, antiguedad);
			break;
		case "Alferez":
			tripulante = new Alferez(nombre, antiguedad);
			break;
		case "Consejero":
			tripulante = new Consejero(nombre, antiguedad, cantidadConsejos);
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
		mostrarEnVista(nave.getTripulantes().get(index));
	}

	/** Pasa los datos del tripulante a la vista campo por campo, asi la vista no conoce el modelo. */
	private void mostrarEnVista(Tripulante tripulante) {
		vista.setNombreSeleccionado(tripulante.getNombre());
		vista.setAntiguedadSeleccionado(tripulante.getAntiguedad());
		vista.setCargoSeleccionado(tripulante.getCargo());
		vista.setOrigenSeleccionado(tripulante.getOrigen());
		Consejero consejero = comoConsejero(tripulante);
		vista.setCantidadConsejosSeleccionado(
				consejero == null ? null : Integer.valueOf(consejero.getCantidadConsejos()));
	}

	/**
	 * Devuelve el Consejero que hay detras del tripulante, atravesando los decoradores
	 * de origen, o null si el cargo no es consejero. Hace falta porque getCantidadConsejos()
	 * es propio de Consejero y Decorator no lo delega.
	 */
	private Consejero comoConsejero(Tripulante tripulante) {
		Tripulante actual = tripulante;
		while (actual instanceof Decorator) {
			actual = ((Decorator) actual).getTripulante();
		}
		return (actual instanceof Consejero) ? (Consejero) actual : null;
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