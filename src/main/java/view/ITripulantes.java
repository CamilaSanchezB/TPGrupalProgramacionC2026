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
    
	public void setControlador(TripulantesController c);
	public void arranca();
	public void cargaTripulantes(List<String> tripulantes);
	public void escribeDatos();
	public void setActionListener(ActionListener listener);
	public void setSeleccionTripulanteListener(ListSelectionListener listener);
	public int getTripulanteSeleccionado();
	public void mostrarDatosTripulante(Tripulante tripulante);
}