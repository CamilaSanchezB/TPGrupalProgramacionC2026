package controller;

import model.Mision.InformeMision;
import model.Mision.Mision;
import model.Mision.GestorHistorialMisiones;
import model.OperacionRechazadaException;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.Universo.ElementoDelUniverso;
import model.nave.Nave;

import java.util.List;

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
    private final GestorHistorialMisiones historial;

    /**
     * PRE: nave != null y bitacora != null
     * POST: el asistente queda asociado a esa nave y a esa bitácora
     */
    public AsistenteDeComando(Nave nave, Bitacora bitacora) {
        assert nave != null && bitacora != null : "El asistente necesita una nave y una bitácora";
        this.nave = nave;
        this.bitacora = bitacora;
        this.historial = new GestorHistorialMisiones();
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

    /**
     * Devuelve el gestor del historial de misiones acumuladas.
     * POST: es una consulta; devuelve la instancia del gestor interno sin alterar el estado.
     */
    public GestorHistorialMisiones getGestorHistorial() {
        return this.historial;
    }

    /**
     * Devuelve la lista unmodifiable de informes acumulados.
     * POST: consulta inmutable de todos los informes finalizados.
     */
    public List<InformeMision> getHistorialMisiones() {
        return this.historial.getHistorial();
    }

    // AGREGAR EN AsistenteDeComando.java (paquete controller):

    /**
     * Inspecciona e interactúa con un elemento espacial del tablero (asteroide, estación de carga, etc.),
     * aplicando sus consecuencias sobre la nave y registrando el resultado en la bitácora.
     * PRE: elemento != null
     * POST: modifica los recursos de la nave según el elemento y registra el evento en la bitácora.
     * @throws IllegalArgumentException si elemento es nulo
     */
    public void interactuarCon(ElementoDelUniverso elemento) {              // [AGREGADO]
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento del universo no puede ser nulo");
        }
        String resultado = elemento.interactuar(this.nave);
        registrar(resultado);
    }

    /**
     * Encomienda y ejecuta una misión sobre la nave asociada, la registra automáticamente
     * en el historial interno al concluir y devuelve el informe final inmutable.
     *
     * @param mision Misión concreta a realizar (M-01, M-02 o M-03)
     * @return InformeMision inmutable generado al cerrar la misión
     *
     * PRE: mision != null
     * POST: la misión ejecuta su ciclo de vida completo (realizar()), su informe queda
     *       almacenado automáticamente en el historial interno del asistente y es devuelto.
     *
     * @throws IllegalArgumentException si la misión es nula
     */
    public InformeMision encomendarMision(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión encomendada no puede ser nula");
        }
        mision.realizar();
        InformeMision informe = mision.getInforme();

        // Registra el informe internamente sin depender de nadie por fuera
        this.historial.registrarInforme(informe);

        return informe;
    }
}