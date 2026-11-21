package model.Universo;
import java.util.ArrayList;
import java.util.List;
import controller.AsistenteDeComando;
import model.Coordenada;
import model.ClasesMotorWarp.MotorWarp;

/**
 * Lleva la nave hacia un destino, un turno por vez: escanea con el radar corto, replanifica
 * si hay un obstáculo adelante y decide entre saltar (una casilla) y saltarWarp (varias).
 * Mueve la nave siempre a través del AsistenteDeComando, así todo queda en la bitacora.
 * INV: asistente, universo y posicion != null
 * INV: hay viaje en curso si y solo si destino != null; en ese caso ruta no está vacía,
 *      ruta.get(0) es la posición actual y ruta termina en el destino
 * INV: warpEnCurso == true solo entre el primer saltarWarp() y el aterrizaje del warp
 */
public class Navegador {
    // Decisiones del grupo (se informan en el informe)
    public static final int CASILLAS_WARP = 2;        // lo que avanza un warp completo

    private final AsistenteDeComando asistente;
    private final Universo universo;
    private final RadarLargoAlcance radarLargo = new RadarLargoAlcance();
    // el radio cubre todas las casillas que un warp podría saltear
    private final RadarCortoAlcance radarCorto = new RadarCortoAlcance(CASILLAS_WARP);

    private Coordenada posicion;
    private Coordenada destino;
    private List<Coordenada> ruta;
    private boolean warpEnCurso = false;   // true mientras la secuencia de warp no terminó

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
     * Un turno del viaje (cada llamada es un turno del motor). Escanea, replanifica si hay un
     * obstáculo adelante y decide entre saltar (1 casilla) o iniciar/continuar un warp
     * (preparación de 2 turnos, salto de 2 casillas y enfriamiento). Los costos los cobra el
     * propio motor; el Navegador solo avanza en la ruta lo que el motor devolvió.
     * No inspecciona la casilla ni mueve el tablero: eso lo hace la misión.
     * PRE: hay viaje en curso, la nave no llegó y no está destruida
     * POST: la posición avanzó 0, 1 o 2 casillas por la ruta (lo que devolvió el motor);
     *       SALTO_WARP si el turno fue parte de un warp, SIN_RUTA si no hay camino
     *       (la nave queda quieta), SALTO en los demás casos
     * @throws IllegalStateException si se viola la PRE
     */
    public ResultadoAvance avanzarUnTurno() {
        if (destino == null)
            throw new IllegalStateException("No hay un viaje en curso");
        if (haLlegado())
            throw new IllegalStateException("La nave ya llegó al destino");
        if (asistente.estaDestruida())
            throw new IllegalStateException("La nave está destruida");

        // En medio de un warp el motor no se puede frenar: no se replanifica
        if (!warpEnCurso) {
            List<ElementoDelUniverso> cercanos = radarCorto.escanear(universo, posicion);
            if (hayObstaculoEn(cercanos, ruta.get(1))) {
                if (puedeEsquivarConWarp(cercanos)) warpEnCurso = true;   // Anexo: obstáculo -> warp
                else if (!recalcularRuta()) return ResultadoAvance.SIN_RUTA;
            }
        }

        if (warpEnCurso) {
            int casillas = asistente.saltarWarp();
            if (casillas > 0) {                 // el warp aterrizó
                warpEnCurso = false;
                avanzarEnRuta(Math.min(casillas, ruta.size() - 1));
            }
            return ResultadoAvance.SALTO_WARP;
        }

        int casillas = asistente.saltar();      // 0 si el motor enfría o no hay combustible
        if (casillas > 0) avanzarEnRuta(casillas);
        return ResultadoAvance.SALTO;
    }

    // ---------- Decisión ----------

    /**
     * Anexo: si el radar detecta un obstáculo en la siguiente posición, se intenta el warp
     * (avanza 2 casilleros y lo esquiva). Solo con motor Disponible, al menos 2 pasos de ruta,
     * casilla de aterrizaje libre y energía para la preparación y el salto.
     */
    private boolean puedeEsquivarConWarp(List<ElementoDelUniverso> cercanos) {
        return asistente.estaDisponible()
                && ruta.size() - 1 >= CASILLAS_WARP
                && !hayElementoEn(cercanos, ruta.get(CASILLAS_WARP))
                && asistente.getEnergia() >= 3 * MotorWarp.COSTO_ENERGIA_WARP;
    }

    // ---------- Movimiento ----------

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