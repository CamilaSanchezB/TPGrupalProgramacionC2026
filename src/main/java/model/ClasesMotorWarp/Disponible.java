package model.ClasesMotorWarp;
import model.Recursos;

public class Disponible extends EstadoMotorBase{
    @Override
    public int saltar(MotorWarp motor){
        Recursos r = motor.getRecursos();
        if (!r.puedeConsumir(MotorWarp.COSTO_COMBUSTIBLE_SALTO, 0, 0))
            return 0;
        r.consumir(MotorWarp.COSTO_COMBUSTIBLE_SALTO, 0, 0);
        return 1;
    }

    @Override
    public int saltarWrap(MotorWarp motor) {
        motor.setEstado(new PreparandoSalto()); // este turno cuenta como el 1ero de preparación
        return 0;
    }

    @Override
    public String getNombreEstado() { return "Disponible"; }

    @Override
    public boolean estaDisponible() {
        return true;
}

}
