package negocio.ClasesMotorWarp;

public class Disponible extends EstadoMotorBase {
    @Override
    public void prepararSalto(MotorWarp motor) {
        motor.setEstado(new PreparandoSalto());
    }

    @Override
    public boolean permitirOperar() {
        return true;
    }

    @Override
    public String getNombreEstado() { return "Disponible"; }

}
