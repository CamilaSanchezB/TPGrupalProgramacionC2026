package ar.edu.unmdp.startrek.negocio.clasesMotorWarp;

public class Disponible implements EstadoMotor{
    @Override
    public void prepararSalto(MotorWarp motor) {
        motor.setEstado(new PreparandoSalto());
    }

    @Override
    public void entrarEnWarp(MotorWarp motor) {
        throw new IllegalStateException("invalido, No se puede entrar en Warp sin preparar el salto.");
    }

    @Override
    public void iniciarEnfriamiento(MotorWarp motor) {
        throw new IllegalStateException("invalido, El motor ya está disponible, no requiere enfriamiento.");
    }

    @Override
    public void estarDisponible(MotorWarp motor) {
        throw new IllegalStateException("invalido, El motor ya se encuentra Disponible.");
    }

    @Override
    public String getNombreEstado() { return "Disponible"; }

}
