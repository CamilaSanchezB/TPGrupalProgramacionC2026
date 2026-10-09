package datos.ClasesMotorWarp;

public class EnWarp extends EstadoMotorBase{
    @Override
    public void iniciarEnfriamiento(MotorWarp motor) {
        motor.setEstado(new Enfriamiento());
    }

    @Override
    public void terminarSalto(MotorWarp motor) {
        motor.setEstado(new Disponible());
    }

    @Override
    public String getNombreEstado() { return "En warp"; }
}

