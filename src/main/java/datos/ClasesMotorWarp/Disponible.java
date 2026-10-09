package datos.ClasesMotorWarp;

public class Disponible extends EstadoMotorBase{
    @Override
    public void prepararSalto(MotorWarp motor) {
        motor.setEstado(new PreparandoSalto());
    }

    @Override
    public String getNombreEstado() { return "Disponible"; }

    @Override
    public boolean estaDisponible() {
        return true;
}

}
