package model.ClasesMotorWarp;
import model.Recursos;

/**
 * INV: estadoActual != null y recursos != NULL
 * Hacia afuera solo expone saltar() y saltarWrap()
 */
public class MotorWarp {
    public static final int COSTO_COMBUSTIBLE_SALTO = 2;
    public static final int COSTO_ENERGIA_WARP = 5;
    public static final int DESGASTE_ENFRIAMIENTO = 3;
    public static final int TURNOS_PREPARACION = 2;

    private EstadoMotorBase estadoActual;
    private final Recursos recursos;

    /**
     * PRE: recursos != null (los de la nave que contiene al motor)
     * POST: el motor queda en estado Disponible y se cumple el invariante
     */
    public MotorWarp(Recursos recursos) {
        assert recursos != null : "El motor necesita los recursos de la nave";
        this.recursos = recursos;
        this.estadoActual = new Disponible();
    }

    /**
     * retrun 1 si avanzó, 0 si no pudo(motor ocupado o sin combustible
     */
    public int saltar() {
        return estadoActual.saltar(this);
    }

    /**
     * return 2 solo en el turno que se materializa el salto; 0 en los demás
     */
    public int saltarWarp() {
        return estadoActual.saltarWarp(this);
    }

    /**
     * Solo lo invocan los estados
     * PRE: nuevoEstado != null
     * POST: actualiza el estado y mantiene el invariante
     */

   public boolean estaDisponible() {
        return estadoActual.estaDisponible();
   }

   public String getNombreEstado() {
       return estadoActual.getNombreEstado();
   }

   // Solo los estados (visibilidad del paquete)
   void setEstado(EstadoMotorBase nuevo) {
       assert nuevo != null : "El estado no puede ser nulo";
       estadoActual = nuevo;
   }

    Recursos getRecursos() { return recursos; }
}
