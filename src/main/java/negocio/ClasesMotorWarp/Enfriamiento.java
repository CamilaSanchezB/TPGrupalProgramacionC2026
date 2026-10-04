package negocio.ClasesMotorWarp;

//import ar.edu.unmdp.startrek.negocio.clasesMotorWarp.MotorWarp.Disponible;

public class Enfriamiento implements  EstadoMotor{
    @Override
    public void prepararSalto(MotorWarp motor) {
        throw new IllegalStateException("Inválido: El motor se está enfriando, espere a que esté disponible.");
    }

    @Override
    public void entrarEnWarp(MotorWarp motor) {
        throw new IllegalStateException("Inválido: No puede saltar mientras se enfría.");
    }

    @Override
    public void iniciarEnfriamiento(MotorWarp motor) {
        throw new IllegalStateException("Inválido: El motor ya se está enfriando.");
    }

    @Override
    public void estarDisponible(MotorWarp motor) {
        motor.setEstado(new Disponible());
    }

    @Override
    public String getNombreEstado() { return "Enfriamiento"; }
}
