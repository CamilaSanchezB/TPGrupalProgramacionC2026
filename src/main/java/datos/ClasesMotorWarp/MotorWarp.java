package datos.ClasesMotorWarp;

/**
 * INV: estadoActual != null
 */
public class MotorWarp {
    private EstadoMotorBase estadoActual;

    /**
     * POST: el motor queda en estado Disponible y se cumple el invariante
     */
    public MotorWarp() {
        this.estadoActual = new Disponible();
    }

    /**
     * Solo lo invocan los estados
     * PRE: nuevoEstado != null
     * POST: actualiza el estado y mantiene el invariante
     */
    public void setEstado(EstadoMotorBase nuevoEstado) {
        assert nuevoEstado != null : "PRE violada: El estado no puede ser nulo";
        this.estadoActual = nuevoEstado;
    }

    public EstadoMotorBase getEstadoActual() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        return estadoActual;
    }

    /**
     * Cada acción:
     * POST: cambia de estado y mantiene el invariante.
     */
    public void prepararSalto() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        estadoActual.prepararSalto(this);
        assert this.estadoActual != null : "INV violado: estadoActual quedó nulo tras prepararSalto";
    }

    public void entrarEnWarp() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        estadoActual.entrarEnWarp(this);
        assert this.estadoActual != null : "INV violado: estadoActual quedó nulo tras entrarEnWarp";
    }

    public void iniciarEnfriamiento() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        estadoActual.iniciarEnfriamiento(this);
        assert this.estadoActual != null : "INV violado: estadoActual quedó nulo tras iniciarEnfriamiento";
    }

    public void estarDisponible() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        estadoActual.estarDisponible(this);
        assert this.estadoActual != null : "INV violado: estadoActual quedó nulo tras estarDisponible";
    }

    public void terminarSalto() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        estadoActual.terminarSalto(this);
        assert this.estadoActual != null : "INV violado: estadoActual quedó nulo tras terminarSalto";
    }

    /**
     * @return true si el estado actual es Disponible
     */
    public boolean estaDisponible() {
        assert this.estadoActual != null : "INV violado: estadoActual es nulo";
        return estadoActual.estaDisponible();
    }
}
