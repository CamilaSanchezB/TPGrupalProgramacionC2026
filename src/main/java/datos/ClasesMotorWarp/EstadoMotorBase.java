package datos.ClasesMotorWarp;
// La implementacion de esta clase es para que no haya un metodo de rechazo general en todos los estados

public abstract class EstadoMotorBase{

    /**
     * @param accion Accion del motor que fallo
     * PRE: solo se llamará cuando se haya intentado reproducir una acción inválida
     * POST: evalúa que la precondición de que la acción sea inválida en este estado se cumpla (falla si se intenta violar el contrato del estado)
     */
    protected void rechazar (String accion) {
        assert false : "Transicion invalida: no se puede " + accion + " en estado " + getNombreEstado();
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
