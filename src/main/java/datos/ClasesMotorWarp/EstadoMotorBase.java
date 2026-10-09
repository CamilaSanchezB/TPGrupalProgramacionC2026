package datos.ClasesMotorWarp;
// La implementacion de esta clase es para que no haya un metodo de rechazo general en todos los estados

public abstract class EstadoMotorBase{

    protected void rechazar(String accion) {
        throw new IllegalStateException(
                "Transicion invalida: no se puede " + accion + " en estado " + getNombreEstado());
    }

    public void prepararSalto(MotorWarp motor) {
        rechazar("preparar salto");
    }

    public void entrarEnWarp(MotorWarp motor) {
        rechazar("entrar en warp");
    }

    public void terminarSalto(MotorWarp motor) {
        rechazar("terminar salto");
    }

    public void iniciarEnfriamiento(MotorWarp motor) {
        rechazar("iniciar enfriamiento");
    }

    public void estarDisponible(MotorWarp motor) {
        rechazar("volver a disponible");
    }

    public abstract String getNombreEstado();

    public boolean estaDisponible() {
        return false;
    }
}
