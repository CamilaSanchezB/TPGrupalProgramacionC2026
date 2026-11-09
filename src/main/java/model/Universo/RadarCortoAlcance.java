package model.Universo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Coordenada;

/**
 * Evalúa el entorno inmediato de la nave. No ve su propia casilla (esa se inspecciona aparte).
 * INV: radio > 0 y no cambia
 */
public class RadarCortoAlcance {
    public static final int RADIO_POR_DEFECTO = 2;

    private final int radio;

    public RadarCortoAlcance() { this(RADIO_POR_DEFECTO); }

    /** @throws IllegalArgumentException si radio <= 0 */
    public RadarCortoAlcance(int radio) {
        if (radio <= 0)
            throw new IllegalArgumentException("El radio del radar debe ser positivo");
        this.radio = radio;
    }

    public int getRadio() { return radio; }

    /**
     * PRE: universo y posicion != null; posicion dentro del universo
     * @return elementos a distancia (en pasos de grilla) entre 1 y radio, del más cercano
     *         al más lejano; vacía si no hay ninguno. Ignora las coordenadas fuera del universo.
     * POST: es una consulta; no modifica nada
     * @throws IllegalArgumentException si se viola la PRE
     */
    public List<ElementoDelUniverso> escanear(Universo universo, Coordenada posicion) {
        if (universo == null || posicion == null)
            throw new IllegalArgumentException("El radar necesita universo y posición");
        if (!universo.contiene(posicion))
            throw new IllegalArgumentException("La posición debe estar dentro del universo");

        List<ElementoDelUniverso> detectados = new ArrayList<>();
        for (int dx = -radio; dx <= radio; dx++) {
            for (int dy = -radio; dy <= radio; dy++) {
                if ((dx == 0 && dy == 0) || Math.abs(dx) + Math.abs(dy) > radio)
                    continue;
                Coordenada c = posicion.desplazar(dx, dy);
                if (universo.contiene(c))
                    detectados.addAll(universo.elementosEn(c));
            }
        }
        detectados.sort(Comparator.comparingInt(
                (ElementoDelUniverso e) -> e.getPosicion().distanciaManhattan(posicion)));
        return detectados;
    }
}