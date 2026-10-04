package negocio.ClasesMotorWarp;
// La implementacion de esta clase es para que no haya un metodo de rechazo general en todos los estados

public abstract class EstadoMotorBase implements EstadoMotor {

    /**
     * @param accion Accion del motor que fallo
     * PRE: solo se llamará cuando se haya intentado reproducir una acción inválida
     * POST: siempre lanza una IllegalStateException (indica que el método fue invocado en un momento inapropiado)
     */
    protected void rechazar (String accion) {
        throw new IllegalStateException(
                "Transicion invalida: no se puede " + accion + " en estado " + getNombreEstado());
    }
    @Override public void prepararSalto(MotorWarp motor) {
        rechazar("preparar salto");
    }

    @Override public void entrarEnWarp(MotorWarp motor) {
        rechazar("entrar en warp");
    }

    @Override public void terminarSalto(MotorWarp motor) {
        rechazar("terminar salto");
    }

    @Override public void iniciarEnfriamiento(MotorWarp motor) {
        rechazar("iniciar enfriamiento");
    }

    @Override public void estarDisponible(MotorWarp motor) {
        rechazar("volver a disponible");
    }

    @Override public boolean permitirOperar() {
        return false;
    }

}
