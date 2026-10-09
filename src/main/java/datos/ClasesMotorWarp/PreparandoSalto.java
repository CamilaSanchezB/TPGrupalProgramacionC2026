package datos.ClasesMotorWarp;

public class PreparandoSalto extends EstadoMotorBase {
    @Override
    public void entrarEnWarp(MotorWarp motor) {
        motor.setEstado(new EnWarp());
    }

    @Override
    public String getNombreEstado() { return "Preparando salto"; }
}
