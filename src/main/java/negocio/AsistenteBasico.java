package negocio;

import datos.Bitacora;
import negocio.nave.Nave;

/**
 * Único canal de acceso a la nave. Un asistente, una nave.
 * Consulta antes de operar: si una consulta da false, registra el rechazo
 * y lanza OperacionRechazadaException sin modificar la nave.
 *
 * INV: nave != null y bitacora != null (se asignan una sola vez, en el constructor)
 */
public class AsistenteBasico implements AsistenteDeComando {
    private final Nave nave;
    private final Bitacora bitacora;

    /**
     * PRE: nave != null y bitacora != null
     * POST: el asistente queda asociado a esa nave y a esa bitácora
     */
    public AsistenteBasico(Nave nave, Bitacora bitacora) {
        assert nave != null && bitacora != null : "El asistente necesita una nave y una bitácora";
        this.nave = nave;
        this.bitacora = bitacora;
    }

    // ---------- Consultas (no modifican nada) ----------

    @Override
    public boolean estaDisponible() { return nave.estaDisponible(); }

    @Override
    public int getCombustible() { return nave.getCombustible(); }

    @Override
    public int getEnergia() { return nave.getEnergia(); }

    @Override
    public int getDesgaste() { return nave.getDesgaste(); }

    @Override
    public boolean puedeConsumir(int combustible, int energia, int desgaste) {
        return nave.puedeConsumir(combustible, energia, desgaste);
    }

    @Override
    public boolean requiereMantenimiento() { return nave.requiereMantenimiento(); }

    // ---------- Órdenes sobre recursos ----------

    /**
     * PRE: puedeConsumir(...) == true; si no, lanza OperacionRechazadaException
     * POST: la nave consumió los recursos y quedó registrado en la bitácora
     */
    @Override
    public void consumir(int combustible, int energia, int desgaste) {
        if (!nave.puedeConsumir(combustible, energia, desgaste))
            rechazar("Consumo rechazado: recursos insuficientes o desgaste excedido.");
        nave.consumir(combustible, energia, desgaste);
        registrar("Consumo realizado, combustible: " + combustible + ", energia: " + energia
                + ", desgaste: " + desgaste + ".");
    }

    /**
     * PRE: la carga es positiva y no supera la capacidad máxima; si no, lanza
     *      OperacionRechazadaException
     */
    @Override
    public void cargarCombustible(int cantidad) {
        if (!nave.puedeCargarCombustible(cantidad))
            rechazar("Carga de combustible rechazada: " + cantidad
                    + " (debe ser positiva y no superar la capacidad máxima).");
        nave.cargarCombustible(cantidad);
        registrar("Carga de combustible realizada, combustible actual: " + nave.getCombustible());
    }

    /**
     * PRE: la carga es positiva y no supera la capacidad máxima; si no, lanza
     *      OperacionRechazadaException
     */
    @Override
    public void cargarEnergia(int cantidad) {
        if (!nave.puedeCargarEnergia(cantidad))
            rechazar("Carga de energia rechazada: " + cantidad
                    + " (debe ser positiva y no superar la capacidad máxima).");
        nave.cargarEnergia(cantidad);
        registrar("Carga de energia realizada, energia actual: " + nave.getEnergia());
    }

    /**
     * POST: desgaste de la nave == 0 y quedó registrado en la bitácora
     */
    @Override
    public void realizarMantenimiento() {
        nave.realizarMantenimiento();
        registrar("Mantenimiento realizado, desgaste actual: " + nave.getDesgaste());
    }

    // ---------- Órdenes sobre el Motor Warp ----------
    // Una transición inválida lanza IllegalStateException desde el estado actual
    // (comportamiento propio de State): no tiene efecto sobre la nave, se registra
    // y se devuelve false.

    @Override
    public boolean prepararSalto() {
        try {
            nave.prepararSalto();
        } catch (IllegalStateException e) {
            registrar(e.getMessage());
            return false;
        }
        registrar("Preparando salto...");
        return true;
    }

    @Override
    public boolean saltar() {
        try {
            nave.entrarEnWarp();
        } catch (IllegalStateException e) {
            registrar(e.getMessage());
            return false;
        }
        registrar("Salto realizado, entrando en Warp...");
        return true;
    }

    /** Por ahora la nave vuelve directo a Disponible (no se modela el tiempo). */
    @Override
    public boolean terminarSalto() {
        try {
            nave.terminarSalto();
        } catch (IllegalStateException e) {
            registrar(e.getMessage());
            return false;
        }
        registrar("Salto terminado, la nave vuelve a estar disponible.");
        return true;
    }

    // ---------- Bitácora ----------

    /**
     * PRE: evento != null y no vacío (lo valida Bitacora.agregarEvento)
     * POST: el evento queda al final de la bitácora
     */
    @Override
    public void registrar(String evento) {
        bitacora.agregarEvento(evento);
    }

    // ---------- Auxiliar ----------

    /** Deja constancia del rechazo y lo propaga. Nunca retorna normalmente. */
    private void rechazar(String motivo) {
        registrar(motivo);
        throw new OperacionRechazadaException(motivo);
    }
}