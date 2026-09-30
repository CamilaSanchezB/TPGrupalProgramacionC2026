package negocio.ClasesMotorWarp;

public class PreparandoSalto implements  EstadoMotor {
    @Override
    public void prepararSalto(MotorWarp motor) {
        throw new IllegalStateException("Inválido: El motor ya se está preparando.");
    }

    @Override
    public void entrarEnWarp(MotorWarp motor) {
        motor.setEstado(new EnWarp());
    }

    @Override
    public void iniciarEnfriamiento(MotorWarp motor) {
        throw new IllegalStateException("Inválido: Debe entrar en Warp o cancelar antes de enfriar.");
    }

    @Override
    public void estarDisponible(MotorWarp motor) {
        throw new IllegalStateException("Inválido: No puede pasar a disponible directamente.");
    }

    @Override
    public String getNombreEstado() { return "Preparando salto"; }
}
