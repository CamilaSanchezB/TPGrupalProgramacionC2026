package ar.edu.unmdp.startrek.negocio.clasesMotorWarp;

public interface EstadoMotor {
    void prepararSalto(MotorWarp motor);
    void entrarEnWarp(MotorWarp motor);
    void iniciarEnfriamiento(MotorWarp motor);
    void estarDisponible(MotorWarp motor);
    String getNombreEstado();
}
