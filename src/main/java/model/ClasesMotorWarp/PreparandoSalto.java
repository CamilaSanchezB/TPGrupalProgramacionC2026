package model.ClasesMotorWarp;

public class PreparandoSalto extends EstadoMotorBase {
    private int turnos = 1;

    @Override
    public int saltar(MotorWarp motor) {
        return 0; // el motor ya esta ocupado preparando
    }

    @Override
    public int saltarWrao(MotorWarp motor) {
        if (!motor.getRecursos().puedeConsumir(0, MotorWarp.COSTO_ENERGIA_WARP, 0))
            return 0; // sin energía de queda preparando
        turnos++;
        if (turnos >= MotorWarp.TURNOS_PREPARACION)
            motor.setEstado(new EnWarp()); // preparacion completa
        return 0;
    }

    @Override
    public String getNombreEstado() { return "Preparando salto"; }
}
