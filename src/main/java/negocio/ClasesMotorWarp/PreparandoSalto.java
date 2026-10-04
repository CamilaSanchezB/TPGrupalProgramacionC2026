package negocio.ClasesMotorWarp;

public class PreparandoSalto extends EstadoMotorBase {
    @Override
    public void entrarEnWarp(MotorWarp motor) {
        motor.setEstado(new SaltoWarp());
    }

    @Override
    public boolean permitirOperar() {
        return true;
    }

    @Override
    public String getNombreEstado() {
        return "Preparando salto";
    }
}
