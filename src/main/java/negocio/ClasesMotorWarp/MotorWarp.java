package negocio.ClasesMotorWarp;

/**
 * INV: estadoActual!= null
 */
public class MotorWarp {
    private EstadoMotor estadoActual;

    /**
     * POST: el motor queda en estado Disponible
     */
    public MotorWarp() {
        this.estadoActual = new Disponible();
    }

    /**
     * Solo lo invocan los estados
     * PRE: nuevoEstado != null
     */
    void setEstado(EstadoMotor nuevoEstado) {
        if (nuevoEstado == null)
            throw new IllegalArgumentException("El estado no puede ser nulo");
        this.estadoActual = nuevoEstado;
    }

    public EstadoMotor getEstadoActual() {
        return estadoActual;
    }

    /**
     * @return true si el estado actual permite iniciar una operación
     */
    public boolean puedeOperar() {
        return estadoActual.permitirOperar();
    }

    /**
     * Cada acción:
     * POST: cambia de estado.
     */
    public void prepararSalto() {
        estadoActual.prepararSalto(this);
    }
    public void entrarEnWarp() {
        estadoActual.entrarEnWarp(this);
    }
    public void iniciarEnfriamiento() {
        estadoActual.iniciarEnfriamiento(this);
    }
    public void estarDisponible() {
        estadoActual.estarDisponible(this);
    }
    public void terminarSalto() {
        estadoActual.terminarSalto(this);
    }
}

