package negocio.nave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import datos.tripulantes.Tripulante;
import negocio.Recursos;
import negocio.ClasesMotorWarp.MotorWarp;

/**
 * INV: motorWarp, recursos y tripulantes nunca son null (se asignan una sola vez, en el constructor)
 * INV: tripulantes no contiene elementos null
 * INV: los recursos respetan los invariantes de Recursos en todo momento:
 *      0 <= combustible <= 100, 0 <= energía <= 100, 0 <= desgaste <= 100
 * INV: el motor siempre está en exactamente uno de sus cuatro estados
 *      (Disponible, Preparando salto, En warp, Enfriamiento)
 * INV: el estado interno solo se modifica a través de los métodos públicos de la nave
 *      (la tripulación se expone como vista de solo lectura)
 */
public abstract class Nave {
    private final MotorWarp motorWarp;
    private final Recursos recursos;
    private final List<Tripulante> tripulantes;

    /**
     * PRE: 0 <= combustibleIni <= 100, 0 <= energiaIni <= 100 y 0 <= desgaste <= 100
     *      (lo exige Recursos)
     * POST: los recursos quedan con los valores indicados, el motor queda en estado
     *       Disponible y la tripulación queda vacía
     */
    protected Nave(int combustibleIni, int energiaIni, int desgaste) {
        this.motorWarp = new MotorWarp();
        this.recursos = new Recursos(combustibleIni, energiaIni, desgaste);
        this.tripulantes = new ArrayList<>();
    }

    // ---------- Tripulación ----------

    /**
     * PRE: t != null
     * POST: t queda al final de la tripulación y los tripulantes anteriores no se alteran
     * Si no se cumple la PRE, lanza IllegalArgumentException y la tripulación no cambia
     */
    public void asignarTripulante(Tripulante t) {
        if (t == null)
            throw new IllegalArgumentException("tripulante invalido");
        tripulantes.add(t);
    }

    /**
     * @return vista de solo lectura de la tripulación, en orden de asignación
     * POST: no modifica la nave; la vista no permite agregar ni quitar tripulantes
     *       (intentarlo lanza UnsupportedOperationException)
     */
    public List<Tripulante> getTripulantes() {
        return Collections.unmodifiableList(tripulantes);
    }

    // ---------- Motor Warp (delegado en State) ----------
    // Transiciones válidas:
    //   Disponible --prepararSalto--> Preparando salto --entrarEnWarp--> En warp
    //   En warp --iniciarEnfriamiento--> Enfriamiento --estarDisponible--> Disponible
    //   En warp --terminarSalto--> Disponible (atajo mientras no se modele el tiempo)
    // Cualquier otra combinación es una transición inválida:
    //   POST: lanza IllegalStateException, el estado del motor no cambia y los recursos
    //         tampoco.

    /**
     * @return true si el motor está en estado Disponible
     * POST: es una consulta; no modifica nada
     */
    public boolean estaDisponible() { return motorWarp.estaDisponible(); }

    /**
     * PRE: el motor está Disponible
     * POST: el motor pasa a Preparando salto
     */
    public void prepararSalto() { motorWarp.prepararSalto(); }

    /**
     * PRE: el motor está en Preparando salto
     * POST: el motor pasa a En warp
     */
    public void entrarEnWarp() { motorWarp.entrarEnWarp(); }

    /**
     * PRE: el motor está En warp
     * POST: el motor pasa a Enfriamiento
     */
    public void iniciarEnfriamiento() { motorWarp.iniciarEnfriamiento(); }

    /**
     * PRE: el motor está en Enfriamiento
     * POST: el motor pasa a Disponible
     */
    public void estarDisponible() { motorWarp.estarDisponible(); }

    /**
     * PRE: el motor está En warp
     * POST: el motor pasa directamente a Disponible
     */
    public void terminarSalto() { motorWarp.terminarSalto(); }

    // ---------- Recursos: consultas ----------
    // Ninguna modifica la nave.

    /** @return combustible actual. POST: 0 <= resultado <= 100 */
    public int getCombustible() { return recursos.getCombustible(); }

    /** @return energía actual. POST: 0 <= resultado <= 100 */
    public int getEnergia() { return recursos.getEnergia(); }

    /** @return desgaste actual. POST: 0 <= resultado <= 100 */
    public int getDesgaste() { return recursos.getDesgaste(); }

    /** @return true si y solo si el desgaste actual es 80 o superior */
    public boolean requiereMantenimiento() { return recursos.requiereMantenimiento(); }

    /**
     * @return true si los tres valores son >= 0, el combustible y la energía pedidos
     *         no superan los actuales y el desgaste actual + desgaste <= 100
     */
    public boolean puedeConsumir(int combustible, int energia, int desgaste) {
        return recursos.puedeConsumir(combustible, energia, desgaste);
    }

    /** @return true si cantidad > 0 y combustible actual + cantidad <= 100 */
    public boolean puedeCargarCombustible(int cantidad) {
        return recursos.puedeCargarCombustible(cantidad);
    }

    /** @return true si cantidad > 0 y energía actual + cantidad <= 100 */
    public boolean puedeCargarEnergia(int cantidad) {
        return recursos.puedeCargarEnergia(cantidad);
    }

    // ---------- Recursos: órdenes ----------

    /**
     * PRE: puedeConsumir(combustible, energia, desgaste) == true
     * POST: el combustible y la energía bajan y el desgaste sube según lo pedido
     *       (todo o nada); el motor no cambia de estado
     * Si no se cumple la PRE, el comportamiento lo define Recursos (assert):
     * quien llama debe haber consultado antes.
     */
    public void consumir(int combustible, int energia, int desgaste) {
        recursos.consumir(combustible, energia, desgaste);
    }

    /**
     * PRE: puedeCargarCombustible(cantidad) == true
     * POST: el combustible aumenta en cantidad y no supera 100; energía y desgaste
     *       no cambian
     */
    public void cargarCombustible(int cantidad) { recursos.cargarCombustible(cantidad); }

    /**
     * PRE: puedeCargarEnergia(cantidad) == true
     * POST: la energía aumenta en cantidad y no supera 100; combustible y desgaste
     *       no cambian
     */
    public void cargarEnergia(int cantidad) { recursos.cargarEnergia(cantidad); }

    /**
     * PRE: ninguna
     * POST: desgaste == 0, requiereMantenimiento() == false; combustible y energía
     *       no cambian
     */
    public void realizarMantenimiento() { recursos.realizarMantenimiento(); }
}