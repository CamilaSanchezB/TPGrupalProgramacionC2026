package negocio.ClasesMotorWarp;

public interface EstadoMotor {
    void prepararSalto(MotorWarp motor);
    void entrarEnWarp(MotorWarp motor);
    void terminarSalto(MotorWarp motor);
    void iniciarEnfriamiento(MotorWarp motor);
    void estarDisponible(MotorWarp motor);
    boolean permitirOperar();

    String getNombreEstado();
}
