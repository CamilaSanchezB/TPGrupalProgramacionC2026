package datos.ClasesMotorWarp;

//import ar.edu.unmdp.startrek.negocio.clasesMotorWarp.MotorWarp.Disponible;

public class Enfriamiento extends  EstadoMotorBase{
    @Override
    public void estarDisponible(MotorWarp motor) {
        motor.setEstado(new Disponible());
    }

    @Override
    public String getNombreEstado() { return "Enfriamiento"; }
}
