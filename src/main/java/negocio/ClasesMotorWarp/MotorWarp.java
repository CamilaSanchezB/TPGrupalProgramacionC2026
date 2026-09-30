package negocio.ClasesMotorWarp;

import negocio.Bitacora;
//import ar.edu.unmdp.startrek.negocio.clasesMotorWarp.Disponible;

public class MotorWarp {
    private EstadoMotor estadoActual;
    private Bitacora bitacora;

    public MotorWarp(Bitacora bitacora) {
        this.bitacora = bitacora;
        this.estadoActual = new Disponible();
        this.bitacora.agregarEvento("motor warp cambia a: " + this.estadoActual.getNombreEstado());
    }

    public void setEstado(EstadoMotor nuevoEstado) {
        this.estadoActual = nuevoEstado;
        this.bitacora.agregarEvento("motor warp cambia a: " + nuevoEstado.getNombreEstado());

    }

    public EstadoMotor getEstadoActual() {
        return estadoActual;
    }

    //delega la acción al estado actual
    public void prepararSalto() {
        estadoActual.prepararSalto(this);
    }
    public void entrarEnWarp() {
        estadoActual.entrarEnWarp(this);
    }
    public void iniciarEnfriamiento() {
        estadoActual.iniciarEnfriamiento(this);
    }
    public void estarDisponible() {
        estadoActual.estarDisponible(this);
    }
}

