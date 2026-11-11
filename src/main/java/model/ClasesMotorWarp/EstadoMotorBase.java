package model.ClasesMotorWarp;

/**
 * La implementacion de esta clase es para que no haya un metodo de rechazo general en todos los estados
 * Estado del Motor Warp (State). Cada llamada es un turno del motor
 * Estado del Motor Warp (State). Cada llamada es un turno del motor
 */

public abstract class EstadoMotorBase {
    public abstract int saltar(MotorWarp motor);

    public abstract int saltarWarp(MotorWarp motor);

    public abstract String getNombreEstado();

    public boolean estaDisponible() {
        return false;
    }
}
