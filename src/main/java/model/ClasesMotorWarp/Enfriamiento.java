package model.ClasesMotorWarp;

//import ar.edu.unmdp.startrek.negocio.clasesMotorWarp.MotorWarp.Disponible;

public class Enfriamiento extends  EstadoMotorBase{
    @Override
    public int saltar(MotorWarp motor) {
        return enfriar(motor);
    }

    @Override
    public int saltarWarp(MotorWarp motor) {
        return enfriar(motor);
    }

    private int enfriar(MotorWarp motor) {
        motor.getRecursos().recibirImpacto(0, MotorWarp.DESGASTE_ENFRIAMIENTO);
        motor.setEstado(new Disponible());
        return 0;
    }

    @Override
    public String getNombreEstado() { return "Enfriamiento"; }
}
