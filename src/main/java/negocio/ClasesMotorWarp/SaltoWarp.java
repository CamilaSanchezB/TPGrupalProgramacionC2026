package negocio.ClasesMotorWarp;

/**
 * Salidas válidas:
 *  - terminarSalto() -> Disponible (por ahora, sin paso del tiempo; R3)
 *  - iniciarEnfriamiento() -> Enfriamiento (existe aunque aún no se use)
 */
public class SaltoWarp extends EstadoMotorBase{
    @Override
    public void iniciarEnfriamiento(MotorWarp motor) {
        motor.setEstado(new Enfriamiento());
    }

    @Override
    public void terminarSalto(MotorWarp motor) {
        motor.setEstado(new Disponible());
    }

    @Override
    public boolean permitirOperar() {
        return true;
    }

    @Override
    public String getNombreEstado() {
        return "En warp";
    }
}

