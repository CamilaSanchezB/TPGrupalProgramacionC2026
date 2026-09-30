package ar.edu.unmdp.startrek.negocio;

public class EnWarp implements  EstadoMotor{
    @Override
    public void prepararSalto(MotorWarp motor) {
        throw new IllegalStateException("Inválido: El motor ya está en Warp.");
    }

    @Override
    public void entrarEnWarp(MotorWarp motor) {
        throw new IllegalStateException("Inválido: Ya se encuentra en Warp.");
    }

    @Override
    public void iniciarEnfriamiento(MotorWarp motor) {
        motor.setEstado(new Enfriamiento());
    }

    @Override
    public void estarDisponible(MotorWarp motor) {
        throw new IllegalStateException("Inválido: Debe enfriarse antes de estar disponible.");
    }

    @Override
    public String getNombreEstado() { return "En warp"; }
}

