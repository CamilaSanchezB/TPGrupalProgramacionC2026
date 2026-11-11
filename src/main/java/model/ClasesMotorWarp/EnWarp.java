package model.ClasesMotorWarp;
import model.Recursos;

public class EnWarp extends EstadoMotorBase{
    @Override
    public int saltar(MotorWarp motor) {
        return 0;
    }

    @Override
    public int saltarWrap(MotorWarp motor) {
        Recursos r = motor.getRecursos();
        if (r.puedeConsumir(0,MotorWarp.COSTO_ENERGIA_WARP, 0)) {
            motor.setEstado(new PreparandoSalto()); // aborta y vuelve a preparación
            return 0;
        }
        r.consumir(0, MotorWarp.COSTO_ENERGIA_WARP, 0);
        motor.setEstado(new Enfriamiento());
        return 2;
    }

    @Override
    public String getNombreEstado() { return "En warp"; }
}

