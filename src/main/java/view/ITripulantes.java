package view;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.ListSelectionListener;

import controller.TripulantesController;
import model.tripulantes.Tripulante;

public interface ITripulantes {
	
	/** Action commands de los botones, para que el controlador sepa cuál se presionó. */
    String CREAR = "CREAR";
    String ACTUALIZAR = "ACTUALIZAR";
    String ELIMINAR = "ELIMINAR";
    String DESELECCIONAR = "DESELECCIONAR";
    String LIQUIDAR = "LIQUIDAR";

    /** Unico cargo que tiene cantidad de consejos. */
    String CARGO_CONSEJERO = "Consejero";

    /** Opciones de los selectores de cargo y de origen. */
    String[] CARGOS = { "Capitan", "Alferez", CARGO_CONSEJERO, "Teniente" };
    String[] ORIGENES = { Tripulante.SIN_ORIGEN, "Terricola", "Marciano", "Vulcano" };

	public void setControlador(TripulantesController c);
	public void arranca();
	public void cargaTripulantes(List<String> tripulantes);
	public void escribeDatos();
	public void setActionListener(ActionListener listener);
	public void setSeleccionTripulanteListener(ListSelectionListener listener);
	public int getTripulanteSeleccionado();
	public void setNombreSeleccionado(String nombre);
	public void setAntiguedadSeleccionado(int antiguedad);
	public void setCargoSeleccionado(String cargo);
	public void setOrigenSeleccionado(String origen);
	/**
	 * Escribe la cantidad de consejos; null deja el campo vacio.
	 * El bloqueo no depende de este valor sino del cargo elegido en la vista.
	 */
	public void setCantidadConsejosSeleccionado(Integer cantidadConsejos);
	public String getNombre();
	public String getAntiguedad();
	public String getCargo();
	public String getOrigen();
	public String getCantidadConsejos();
	public void mostrarError(String mensaje);
	public void limpiarSeleccion();
	public void mostrarLiquidacion(String recibo);
}