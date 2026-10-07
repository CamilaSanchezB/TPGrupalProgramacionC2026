package negocio;

/**
 * Único canal de acceso a la nave: toda consulta u orden pasa por acá.
 * Cada asistente opera UNA sola nave y lleva el registro (Bitácora) de lo ocurrido.
 * Las misiones dependen de esta interfaz, nunca de Nave ni de Recursos directamente.
 */
public interface AsistenteDeComando {

    // ---------- Consultas (no modifican nada) ----------

    /**
    * Indica si el Motor Warp de la nave está en estado Disponible.
    * PRE: ninguna
    * POST: devuelve true solo si el motor está en Disponible; no modifica la nave
    */
    boolean estaDisponible();

    /** 
     * Devuelve el combustible actual
     * PRE: ninguna
     * POST: devuelve la cantidad de combustible actual (entre 0 y 100); no modifica la nave 
    */
    int getCombustible();

    /** @return energía actual de la nave */
    int getEnergia();

    /** @return desgaste actual de la nave */
    int getDesgaste();

    /**
     * Consulta si el consumo es posible sin violar ningún límite.
     * No modifica la nave. Es lo que usa Mision.preparar() para verificar.
     */
    boolean puedeConsumir(int combustible, int energia, int desgaste);

    // ---------- Órdenes sobre recursos ----------

    /**
     * Consume recursos de forma atómica (todo o nada).
     * PRE: puedeConsumir(...) == true
     * POST: combustible y energía bajan, el desgaste sube; si no se puede, no cambia nada
     */
    void consumir();

    void cargarCombustible(int cantidad);

    void cargarEnergia(int cantidad);

    void realizarMantenimiento();

    // ---------- Órdenes sobre el Motor Warp ----------
    // Devuelven boolean: false = la acción no era válida en el estado actual
    // (no tuvo efecto y el asistente ya dejó constancia en la Bitácora).

    boolean prepararSalto();

    boolean saltar();

    /** Termina el salto: por ahora la nave vuelve directo a Disponible (no se modela el tiempo) */
    boolean terminarSalto();

    // ---------- Bitácora ----------

    /** Registra un evento (con fecha y hora). El texto no puede ser nulo ni vacío. */
    void registrar(String evento);

    /** Nos dice si la nave requiere mantenimiento */
    boolean requiereMantenimiento();
}
