package model.Universo;
import java.util.ArrayList;
import java.util.List;
import controller.AsistenteDeComando;
import model.Coordenada;
import model.Recursos;

/**
 * Lleva la nave hacia un destino, un turno por vez: escanea con el radar corto, replanifica
 * si hay un obstáculo adelante y decide entre saltar (una casilla) y saltarWarp (varias).
 * Mueve la nave siempre a través del AsistenteDeComando, así todo queda en la bitácora.
 *
 * INV: asistente, universo y posicion != null
 * INV: hay viaje en curso si y solo si destino != null; en ese caso ruta no está vacía,
 *      ruta.get(0) es la posición actual y ruta termina en el destino
 * INV: turnosEnfriamiento >= 0; si es > 0, el motor está en Enfriamiento
 */
public class Navegador {
    // Decisiones del grupo (se informan en el informe)
    public static final int ALCANCE_WARP = 3;           // casillas que avanza un warp
    public static final int TURNOS_ENFRIAMIENTO = 2;    // turnos sin warp después de uno

    public static final int SALTO_COMBUSTIBLE = 1;
    public static final int SALTO_ENERGIA = 0;
    public static final int SALTO_DESGASTE = 1;

    public static final int WARP_COMBUSTIBLE = 4;
    public static final int WARP_ENERGIA = 2;
    public static final int WARP_DESGASTE = 1;

    private final AsistenteDeComando asistente;
    private final Universo universo;
    private final RadarLargoAlcance radarLargo = new RadarLargoAlcance();
    // el radio cubre todas las casillas que un warp podría saltear
    private final RadarCortoAlcance radarCorto = new RadarCortoAlcance(ALCANCE_WARP);

    private Coordenada posicion;
    private Coordenada destino;
    private List<Coordenada> ruta;
    private int turnosEnfriamiento = 0;

    /**
     * PRE: asistente, universo y origen != null; origen dentro del universo
     * POST: sin viaje en curso; la nave se considera ubicada en origen
     * @throws IllegalArgumentException si se viola la PRE
     */
    public Navegador(AsistenteDeComando asistente, Universo universo, Coordenada origen) {
        if (asistente == null || universo == null || origen == null)
            throw new IllegalArgumentException("El navegador necesita asistente, universo y origen");
        if (!universo.contiene(origen))
            throw new IllegalArgumentException("El origen debe estar dentro del universo");
        this.asistente = asistente;
        this.universo = universo;
        this.posicion = origen;
    }

    public Coordenada getPosicion() { return posicion; }

    public boolean haLlegado() { return destino != null && posicion.equals(destino); }

    /**
     * Calcula la ruta con el radar de largo alcance.
     * PRE: destino != null y dentro del universo
     * POST: si devuelve true, hay viaje en curso hacia destino; si devuelve false, no hay
     *       viaje en curso (no existe camino) y el motivo queda en la bitácora
     * @throws IllegalArgumentException si se viola la PRE
     */
    public boolean iniciarViaje(Coordenada destino) {
        if (destino == null || !universo.contiene(destino))
            throw new IllegalArgumentException("El destino debe estar dentro del universo");
        List<Coordenada> nueva = radarLargo.calcularRuta(universo, posicion, destino);
        if (nueva.isEmpty()) {
            this.destino = null;
            this.ruta = null;
            asistente.registrar("Sin ruta hacia " + destino);
            return false;
        }
        this.destino = destino;
        this.ruta = nueva;
        asistente.registrar("Ruta calculada hacia " + destino + ": " + (nueva.size() - 1) + " pasos");
        return true;
    }

    /**
     * Escaneo + planificación + movimiento de un turno (no inspecciona la casilla actual
     * ni mueve el tablero: eso lo hace la misión, con Destino).
     * PRE: hay viaje en curso, la nave no llegó y no está destruida
     * POST: la nave avanzó por la ruta (SALTO o SALTO_WARP) y consumió su costo, o se quedó
     *       en su lugar sin consumir (SIN_RUTA); el enfriamiento del motor avanzó un turno
     *       (salvo en el turno en que se hizo el warp)
     * @throws IllegalStateException si se viola la PRE
     */
    public ResultadoAvance avanzarUnTurno() {
        if (destino == null)
            throw new IllegalStateException("No hay un viaje en curso");
        if (haLlegado())
            throw new IllegalStateException("La nave ya llegó al destino");
        if (asistente.estaDestruida())
            throw new IllegalStateException("La nave está destruida");

        List<ElementoDelUniverso> cercanos = radarCorto.escanear(universo, posicion);

        if (hayObstaculoEn(cercanos, ruta.get(1)) && !recalcularRuta()) {
            enfriarUnTurno();
            return ResultadoAvance.SIN_RUTA;
        }

        if (puedeWarp(cercanos)) {
            saltarWarp();
            return ResultadoAvance.SALTO_WARP;   // en este turno no corre el enfriamiento
        }
        saltar();
        enfriarUnTurno();
        return ResultadoAvance.SALTO;
    }

    // ---------- Decisión ----------

    /**
     * Warp solo si: el motor está Disponible, faltan al menos ALCANCE_WARP pasos, las
     * ALCANCE_WARP casillas siguientes están libres (no se saltea una estación ni se aterriza
     * sobre un peligro) y la nave sobrevive al costo.
     */
    private boolean puedeWarp(List<ElementoDelUniverso> cercanos) {
        if (!asistente.estaDisponible()) return false;
        if (ruta.size() - 1 < ALCANCE_WARP) return false;
        boolean sobrevive = asistente.getCombustible() > WARP_COMBUSTIBLE
                && asistente.getEnergia() > WARP_ENERGIA
                && asistente.getDesgaste() + WARP_DESGASTE < Recursos.DESGASTE_MAXIMO;
        if (!sobrevive) return false;
        for (int i = 1; i <= ALCANCE_WARP; i++)
            if (hayElementoEn(cercanos, ruta.get(i))) return false;
        return true;
    }

    // ---------- Movimiento ----------

    private void saltar() {
        asistente.consumir(SALTO_COMBUSTIBLE, SALTO_ENERGIA, SALTO_DESGASTE);
        avanzarEnRuta(1);
        asistente.registrar("Salto a " + posicion);
    }

    private void saltarWarp() {
        if (!asistente.prepararSalto() || !asistente.saltar())
            throw new IllegalStateException("El motor no pudo entrar en warp");
        asistente.consumir(WARP_COMBUSTIBLE, WARP_ENERGIA, WARP_DESGASTE);
        avanzarEnRuta(ALCANCE_WARP);
        asistente.registrar("Salto warp a " + posicion);
        if (!asistente.iniciarEnfriamiento())
            throw new IllegalStateException("El motor no pudo iniciar el enfriamiento");
        turnosEnfriamiento = TURNOS_ENFRIAMIENTO;
    }

    private void enfriarUnTurno() {
        if (turnosEnfriamiento > 0) {
            turnosEnfriamiento--;
            if (turnosEnfriamiento == 0)
                asistente.estarDisponible();
        }
    }

    private void avanzarEnRuta(int pasos) {
        ruta = new ArrayList<>(ruta.subList(pasos, ruta.size()));
        posicion = ruta.get(0);
    }

    private boolean recalcularRuta() {
        List<Coordenada> nueva = radarLargo.calcularRuta(universo, posicion, destino);
        if (nueva.isEmpty()) {
            asistente.registrar("Sin ruta desde " + posicion + " hacia " + destino);
            return false;
        }
        ruta = nueva;
        asistente.registrar("Ruta recalculada desde " + posicion + ": " + (nueva.size() - 1) + " pasos");
        return true;
    }

    // ---------- Consultas sobre lo que vio el radar ----------

    private boolean hayElementoEn(List<ElementoDelUniverso> elementos, Coordenada c) {
        for (ElementoDelUniverso e : elementos)
            if (e.getPosicion().equals(c)) return true;
        return false;
    }

    private boolean hayObstaculoEn(List<ElementoDelUniverso> elementos, Coordenada c) {
        for (ElementoDelUniverso e : elementos)
            if (e.esObstaculo() && e.getPosicion().equals(c)) return true;
        return false;
    }
}