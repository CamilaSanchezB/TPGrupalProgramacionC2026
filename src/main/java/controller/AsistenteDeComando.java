package controller;

import model.OperacionRechazadaException;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.nave.Nave;

/**
 * Único canal de acceso a la nave. Un asistente, una nave.
 * Consulta antes de operar: si una consulta da false, registra el rechazo
 * y lanza OperacionRechazadaException sin modificar la nave.
 *
 * INV: nave != null y bitacora != null (se asignan una sola vez, en el constructor)
 */
public class AsistenteDeComando {
    private final Nave nave;
    private final Bitacora bitacora;

    /**
     * PRE: nave != null y bitacora != null
     * POST: el asistente queda asociado a esa nave y a esa bitácora
     */
    public AsistenteDeComando(Nave nave, Bitacora bitacora) {
        assert nave != null && bitacora != null : "El asistente necesita una nave y una bitácora";
        this.nave = nave;
        this.bitacora = bitacora;
    }

    // ---------- Consultas (no modifican nada) ----------

    public Bitacora getBitacora() { return this.bitacora; }

    public boolean estaDisponible() { return nave.estaDisponible(); }

    public int getCombustible() { return nave.getCombustible(); }

    public int getEnergia() { return nave.getEnergia(); }

    public int getDesgaste() { return nave.getDesgaste(); }

    public boolean puedeConsumir(int combustible, int energia, int desgaste) {
        return nave.puedeConsumir(combustible, energia, desgaste);
    }

    public boolean requiereMantenimiento() { return nave.requiereMantenimiento(); }

    // ---------- Órdenes sobre recursos ----------

    /**
     * PRE: puedeConsumir(...) == true; si no, lanza OperacionRechazadaException
     * POST: la nave consumió los recursos y quedó registrado en la bitácora
     */
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
    public void realizarMantenimiento() {
        nave.realizarMantenimiento();
        registrar("Mantenimiento realizado, desgaste actual: " + nave.getDesgaste());
    }

    // ---------- Órdenes sobre el Motor Warp ----------
    // Cada llamada es un turno del motor. Devuelven los casilleros que avanzó la nave.

    public int saltar() {
        String antes = nave.getEstadoMotor();
        int casilleros = nave.saltar();
        registrar("Salto normal: avanza " + casilleros + " casillero(s). Motor: "
                + antes + " -> " + nave.getEstadoMotor());
        return casilleros;
    }

    public int saltarWarp() {
        String antes = nave.getEstadoMotor();
        int casilleros = nave.saltarWarp();
        registrar("Salto warp: avanza " + casilleros + " casillero(s). Motor: "
                + antes + " -> " + nave.getEstadoMotor());
        return casilleros;
    }

    // ---------- Bitácora ----------

    /**
     * PRE: evento != null y no vacío (lo valida Bitacora.agregarEvento)
     * POST: el evento queda al final de la bitácora
     */
    public void registrar(String evento) {
        bitacora.registrar(evento);
    }

    public void mostrarReporteBitacora() {
        System.out.println("=== INFORME OFICIAL DE LA MISIÓN ===");

        for (RegitroBitacora evento : bitacora.getEventos()) {
            System.out.println("[" + evento.getTiempoActual() + "] -> " + evento.getDescripcion());
        }
    }

    // ---------- Auxiliar ----------

    /** Deja constancia del rechazo y lo propaga. Nunca retorna normalmente. */
    private void rechazar(String motivo) {
        registrar(motivo);
        throw new OperacionRechazadaException(motivo);
    }

    public boolean estaDestruida() { return nave.estaDestruida(); }
}