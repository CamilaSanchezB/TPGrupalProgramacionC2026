package model.nave;

import java.util.List;

import model.Recursos;
import model.ClasesMotorWarp.MotorWarp;
import model.tripulantes.*;

/**
 * INV: motorWarp, recursos y tripulantes nunca son null (se asignan una sola vez, en el constructor)
 * INV: tripulantes no contiene elementos null (lo garantiza TripulantesCollection)
 * INV: los recursos respetan los invariantes de Recursos en todo momento:
 *      0 <= combustible <= 100, 0 <= energía <= 100, 0 <= desgaste <= 100
 * INV: el motor siempre está en exactamente uno de sus cuatro estados
 *      (Disponible, Preparando salto, En warp, Enfriamiento)
 * INV: el estado interno solo se modifica a través de los métodos públicos de la nave
 *      (la tripulación se expone como vista de solo lectura)
 */
public abstract class Nave {
    private MotorWarp motorWarp;
    private Recursos recursos;
    private TripulantesCollection tripulantes;
    private boolean destruidaEnBatalla = false;   // una vez true, no vuelve a false
    /**
     * PRE: 0 <= combustibleIni <= 100, 0 <= energiaIni <= 100 y 0 <= desgaste <= 100
     *      (lo exige Recursos)
     * POST: los recursos quedan con los valores indicados, el motor queda en estado
     *       Disponible y la tripulación queda con la tripulación mínima requerida
     *       (Capitan, Alferez, Consejero y Teniente, en ese orden)
     */
    protected Nave(int combustibleIni, int energiaIni, int desgaste) {
        this.recursos = new Recursos(combustibleIni, energiaIni, desgaste);
        this.motorWarp = new MotorWarp(recursos);
        tripulantes = new TripulantesCollection();
        asignarTripulante(new Capitan("CAPITAN"));
        asignarTripulante(new Alferez("ALFEREZ"));
        asignarTripulante(new Consejero("CONSEJERO"));
        asignarTripulante(new Teniente("TENIENTE"));
    }

    // ---------- Tripulación  ----------
    /**
     * PRE: t != null
     * POST: t queda al final de la tripulación y los tripulantes anteriores no se alteran
     */
    public void asignarTripulante(Tripulante t) {
        tripulantes.agregar(t);
    }

    /**
     * PRE: nombre != null y no vacío, editado != null, existeTripulante(nombre) == true
     * POST: el primer tripulante con ese nombre queda reemplazado por editado, en la misma
     *       posición; el resto de la tripulación no se altera
     */
    public void editarTripulante(String nombre, Tripulante editado) {
        tripulantes.editar(nombre, editado);
    }

    /**
     * PRE: nombre != null y no vacío
     * POST: si lo encuentra, el primer tripulante con ese nombre deja de estar en la
     *       tripulación; el resto conserva su orden relativo
     */
    public void eliminarTripulante(String nombre) {
        tripulantes.eliminar(nombre);
    }

    /**
     * PRE: nombre != null
     * @return true si y solo si algún tripulante tiene ese nombre
     * POST: es una consulta; no modifica nada
     */
    public boolean existeTripulante(String nombre) {
        return tripulantes.existeTripulante(nombre);
    }

    /**
     * @return tripulacióm en orden de asignación
     * POST: no modifica la nave; la vista no permite agregar ni quitar tripulantes
     *       (intentarlo lanza UnsupportedOperationException)
     */
    public List<Tripulante> getTripulantes() {
        return tripulantes.getTripulantes();
    }

    /**
     * PRE: cargo != null
     * @return lista nueva con los tripulantes de ese cargo, en orden de asignación
     *         (vacía si no hay ninguno)
     * POST: no modifica la nave
     */
    public List<Tripulante> getTripulantesPorCargo(String cargo) {
        return tripulantes.getTripulantesPorCargo(cargo);
    }



    // ---------- Motor Warp (delegado en State) ----------
    // Solo dos operaciones; cada llamada es un turno. Devuelven los casilleros avanzados
    public int saltar() {
        return motorWarp.saltar();
    }

    public int saltarWarp() {
        return motorWarp.saltarWarp();
    }

    public boolean estaDisponible() {
        return motorWarp.estaDisponible();
    }

    public String getEstadoMotor() {
        return motorWarp.getNombreEstado();
    }

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

    public int getGemas() {
        return recursos.getGemas();
    }

    public boolean estaDestruida() {
        return recursos.estaDestruida();
    }

    public void agregarGemas(int cantidad) {
        recursos.agregarGemas(cantidad);
    }

    public void recargarHastaTope(int combustible, int energia) {
        recursos.recargarHastaTope(combustible, energia);
    }

    public void recibirImpacto(int energia, int desgaste) {
        recursos.recibirImpacto(energia, desgaste);
    }

    public Recursos copiaRecursos() {
        return recursos.copia();
    }
    /**
     * PRE: enemiga != null
     * POST: devuelve el resultado del combate desde el punto de vista de esta nave;
     *       no modifica ninguna de las dos naves (quien llama aplica las consecuencias)
     * @throws IllegalArgumentException si enemiga es null
     */
    public abstract ResultadoBatalla batallar(Nave enemiga);

    // Segundo despacho: "yo soy la enemiga, ¿cómo me va contra esta nave propia?"
    protected abstract ResultadoBatalla recibirBatallaDe(NaveExploradora propia);
    protected abstract ResultadoBatalla recibirBatallaDe(NaveCarguero propia);
    protected abstract ResultadoBatalla recibirBatallaDe(NaveCombate propia);

    /**
     * Validación común de batallar() Las subclases la llaman antes de despachar  
     * @throws IllegalArgumentException si enemiga es null
     */
    protected static void exigirEnemiga(Nave enemiga) {
    if (enemiga == null)
        throw new IllegalArgumentException("La nave enemiga no puede ser nula");
    }

    /**
     * @return true si la nave quedó inoperativa: desgaste 100, energía 0, combustible 0
     *         o fue destruida en batalla
     * POST: es una consulta; no modifica nada
     */
    public boolean estaDestruida() {
        return destruidaEnBatalla || recursos.getDesgaste() >= Recursos.DESGASTE_MAXIMO || recursos.getEnergia() == 0 || recursos.getCombustible() == 0;
    }

    /**
     * Se usa cuando la nave pierde una batalla.
     * POST: estaDestruida() == true y lo seguirá siendo
     */
    public void destruir() {
        destruidaEnBatalla = true;
    }

}