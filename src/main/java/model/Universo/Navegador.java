package model.Universo;

import java.util.ArrayList;
import java.util.List;

import controller.AsistenteDeComando;
import model.ClasesMotorWarp.MotorWarp;
import model.Coordenada;
import model.Recursos;

public class Navegador {
    public static final int ALCANCE_WARP = 2;      // casilleros que avanza un warp
    public static final int SALTO_DESGASTE = 1;    // desgaste por salto (el combustible lo cobra el motor)

    private final AsistenteDeComando asistente;
    private final Universo universo;
    private final RadarLargoAlcance radarLargo = new RadarLargoAlcance();
    private final RadarCortoAlcance radarCorto = new RadarCortoAlcance(ALCANCE_WARP);
    private Coordenada posicion;
    private Coordenada destino;
    private List<Coordenada> ruta;
    private boolean warpEnCurso = false;   // el motor está preparando / listo para saltar
    private boolean enfriando = false;     // falta el turno de enfriamiento

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

    public boolean iniciarViaje(Coordenada destino) {
        if (destino == null || !universo.contiene(destino))
            throw new IllegalArgumentException("El destino debe estar dentro del universo");
        List<Coordenada> nueva = radarLargo.calcularRuta(universo, posicion, destino, false);
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

    public ResultadoAvance avanzarUnTurno() {
        if (destino == null) throw new IllegalStateException("No hay un viaje en curso");
        if (haLlegado()) throw new IllegalStateException("La nave ya llegó al destino");
        if (asistente.estaDestruida()) throw new IllegalStateException("La nave está destruida");

        if (enfriando) {                       // Enfriamiento -> Disponible (+3 de desgaste)
            asistente.saltar();
            enfriando = false;
            return ResultadoAvance.ESPERA;
        }
        if (warpEnCurso) return avanzarWarp(); // sigue la secuencia del motor

        List<ElementoDelUniverso> cercanos = radarCorto.escanear(universo, posicion);
        if (hayObstaculoEn(cercanos, ruta.get(1))) {
            if (puedeWarp(cercanos)) {         // obstáculo adelante: warp para esquivarlo
                warpEnCurso = true;
                return avanzarWarp();
            }
            if (!recalcularRuta()) return ResultadoAvance.SIN_RUTA;
        }
        return saltar() ? ResultadoAvance.SALTO : ResultadoAvance.ESPERA;
    }

    /** Un turno de la secuencia warp: 2 turnos preparando, 1 saltando (devuelve 2 casilleros). */
    private ResultadoAvance avanzarWarp() {
        int casilleros = asistente.saltarWarp();
        if (casilleros > 0) {
            avanzarEnRuta(Math.min(casilleros, ruta.size() - 1));
            warpEnCurso = false;
            enfriando = true;
            return ResultadoAvance.SALTO_WARP;
        }
        return ResultadoAvance.ESPERA;
    }

    private boolean puedeWarp(List<ElementoDelUniverso> cercanos) {
        if (!asistente.estaDisponible() || ruta.size() - 1 < ALCANCE_WARP) return false;
        if (asistente.getEnergia() <= MotorWarp.COSTO_ENERGIA_WARP) return false;
        if (asistente.getDesgaste() + MotorWarp.DESGASTE_ENFRIAMIENTO + SALTO_DESGASTE
                >= Recursos.DESGASTE_MAXIMO) return false;
        return !hayObstaculoEn(cercanos, ruta.get(ALCANCE_WARP));   // no aterrizar sobre otro obstáculo
    }

    private boolean saltar() {
        asistente.consumir(0, 0, SALTO_DESGASTE);
        int casilleros = asistente.saltar();   // el motor cobra el combustible
        if (casilleros > 0) {
            avanzarEnRuta(casilleros);
            asistente.registrar("Salto a " + posicion);
        }
        return casilleros > 0;
    }

    private void avanzarEnRuta(int pasos) {
        ruta = new ArrayList<>(ruta.subList(pasos, ruta.size()));
        posicion = ruta.get(0);
    }

    private boolean recalcularRuta() {
        List<Coordenada> nueva = radarLargo.calcularRuta(universo, posicion, destino);   // esta sí esquiva
        if (nueva.isEmpty()) {
            asistente.registrar("Sin ruta desde " + posicion + " hacia " + destino);
            return false;
        }
        ruta = nueva;
        asistente.registrar("Ruta recalculada desde " + posicion + ": " + (nueva.size() - 1) + " pasos");
        return true;
    }

    private boolean hayObstaculoEn(List<ElementoDelUniverso> elementos, Coordenada c) {
        for (ElementoDelUniverso e : elementos)
            if (e.esObstaculo() && e.getPosicion().equals(c)) return true;
        return false;
    }
}