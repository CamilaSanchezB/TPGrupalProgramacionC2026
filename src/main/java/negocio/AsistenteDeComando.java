package negocio;

/**
 * Único canal de acceso a la nave: toda consulta u orden pasa por acá.
 * Cada asistente opera UNA sola nave y lleva el registro (Bitácora) de lo ocurrido.
 * Las misiones dependen de esta interfaz, nunca de Nave ni de Recursos directamente.
 *
 * INV: está asociado a una única nave y a una única bitácora, que no cambian
 *      durante toda su vida (nunca null)
 * INV: los recursos de la nave respetan sus rangos en todo momento:
 *      0 <= combustible <= 100, 0 <= energía <= 100, 0 <= desgaste <= 100
 * INV: la bitácora solo crece: los eventos registrados no se modifican ni se eliminan
 *      y se conservan en orden cronológico
 * INV: toda orden rechazada deja la nave sin modificar
 *
 * Convención de errores:
 *  - Rechazo de una orden sobre recursos: se registra el motivo y se lanza
 *    OperacionRechazadaException (la nave no cambia).
 *  - Transición inválida del Motor Warp: no tiene efecto, se registra el motivo
 *    y se devuelve false (no se lanza excepción).
 */
public interface AsistenteDeComando {

    // ---------- Consultas (no modifican la nave ni la bitácora) ----------

    /**
     * @return true si el Motor Warp de la nave está en estado Disponible
     * POST: es una consulta; no modifica nada ni registra eventos
     */
    boolean estaDisponible();

    /**
     * @return combustible actual de la nave
     * POST: 0 <= resultado <= 100; no modifica nada
     */
    int getCombustible();

    /**
     * @return energía actual de la nave
     * POST: 0 <= resultado <= 100; no modifica nada
     */
    int getEnergia();

    /**
     * @return desgaste actual de la nave
     * POST: 0 <= resultado <= 100; no modifica nada
     */
    int getDesgaste();

    /**
     * Consulta si el consumo es posible sin violar ningún límite.
     * Es lo que usa Mision.preparar() para verificar.
     * @return true si combustible >= 0, energia >= 0, desgaste >= 0,
     *         combustible <= combustible actual, energia <= energía actual
     *         y el desgaste actual + desgaste <= 100
     * POST: es una consulta; no modifica nada ni registra eventos
     */
    boolean puedeConsumir(int combustible, int energia, int desgaste);

    /**
     * @return true si el desgaste actual es 80 o superior
     * POST: es una consulta; no modifica nada ni registra eventos
     */
    boolean requiereMantenimiento();

    // ---------- Órdenes sobre recursos ----------

    /**
     * Consume recursos de forma atómica (todo o nada).
     * PRE: puedeConsumir(combustible, energia, desgaste) == true
     * POST: si se cumple la PRE, el combustible y la energía bajan, el desgaste sube
     *       según lo pedido y queda un evento en la bitácora
     * POST: si no se cumple, la nave no cambia, el motivo queda en la bitácora y se
     *       lanza OperacionRechazadaException
     */
    void consumir(int combustible, int energia, int desgaste);

    /**
     * PRE: cantidad > 0 y combustible actual + cantidad <= 100
     * POST: si se cumple la PRE, el combustible aumenta en cantidad y queda un
     *       evento en la bitácora
     * POST: si no se cumple, la nave no cambia, el motivo queda en la bitácora y se
     *       lanza OperacionRechazadaException
     */
    void cargarCombustible(int cantidad);

    /**
     * PRE: cantidad > 0 y energía actual + cantidad <= 100
     * POST: si se cumple la PRE, la energía aumenta en cantidad y queda un
     *       evento en la bitácora
     * POST: si no se cumple, la nave no cambia, el motivo queda en la bitácora y se
     *       lanza OperacionRechazadaException
     */
    void cargarEnergia(int cantidad);

    /**
     * PRE: ninguna
     * POST: desgaste == 0, combustible y energía sin cambios, y queda un evento
     *       en la bitácora
     */
    void realizarMantenimiento();

    // ---------- Órdenes sobre el Motor Warp ----------
    // Devuelven boolean: false = la acción no era válida en el estado actual
    // (no tuvo efecto y el asistente ya dejó constancia en la Bitácora).

    /**
     * PRE: ninguna (si el motor no está Disponible, se rechaza y devuelve false)
     * POST: si devuelve true, el motor pasó de Disponible a Preparando salto
     * POST: si devuelve false, el estado del motor no cambió
     * POST: en ambos casos queda un evento en la bitácora; los recursos no cambian
     */
    boolean prepararSalto();

    /**
     * PRE: ninguna (si el motor no está en Preparando salto, se rechaza y devuelve false)
     * POST: si devuelve true, el motor pasó de Preparando salto a En warp
     * POST: si devuelve false, el estado del motor no cambió
     * POST: en ambos casos queda un evento en la bitácora; los recursos no cambian
     */
    boolean saltar();

    /**
     * Termina el salto: por ahora la nave vuelve directo a Disponible (no se modela el tiempo).
     * PRE: ninguna (si el motor no está En warp, se rechaza y devuelve false)
     * POST: si devuelve true, el motor pasó de En warp a Disponible
     * POST: si devuelve false, el estado del motor no cambió
     * POST: en ambos casos queda un evento en la bitácora; los recursos no cambian
     */
    boolean terminarSalto();

    // ---------- Bitácora ----------

    /**
     * Registra un evento (con fecha y hora).
     * PRE: evento != null y no vacío
     * POST: el evento queda al final de la bitácora con su fecha y hora, y los
     *       eventos anteriores no se alteran
     */
    void registrar(String evento);
}