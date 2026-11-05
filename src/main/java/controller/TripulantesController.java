package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.nave.Nave;
import model.tripulantes.Tripulante;
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
		this.vista.setSeleccionTripulanteListener(this);
		cargarTripulantesEnVista();
	}

	private void cargarTripulantesEnVista() {
		List<String> nombres = new ArrayList<>();
		for (Tripulante t : nave.getTripulantes()) {
			nombres.add(t.getNombre());
		}
		vista.cargaTripulantes(nombres);
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
		
	}
	
	
	private void actualizarVistaTripulanteSeleccionado() {
		int index = vista.getTripulanteSeleccionado();
		if(index < 0)
			index = 0;
		
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