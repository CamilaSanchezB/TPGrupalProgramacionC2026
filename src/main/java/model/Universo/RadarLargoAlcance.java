package model.Universo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.Coordenada;

/**
 * Calcula el camino completo hacia el objetivo (búsqueda en anchura sobre la grilla).
 * Se mueve en 4 direcciones y rodea los obstáculos que ve en el momento del cálculo.
 */
public class RadarLargoAlcance {
    private static final int[][] DIRECCIONES = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

    /**
     * PRE: universo, origen y destino != null; ambas coordenadas dentro del universo
     * @return la ruta más corta, desde origen hasta destino, ambos incluidos
     *         (si origen == destino, la ruta es solo [origen]);
     *         lista vacía si no existe camino.
     *         El origen y el destino se consideran transitables aunque tengan un obstáculo.
     * POST: es una consulta; no modifica nada
     * @throws IllegalArgumentException si se viola la PRE
     */
    public List<Coordenada> calcularRuta(Universo universo, Coordenada origen, Coordenada destino) {
        if (universo == null || origen == null || destino == null)
            throw new IllegalArgumentException("El radar necesita universo, origen y destino");
        if (!universo.contiene(origen) || !universo.contiene(destino))
            throw new IllegalArgumentException("Origen y destino deben estar dentro del universo");

        Map<Coordenada, Coordenada> padre = new HashMap<>();   // también hace de "visitados"
        Queue<Coordenada> pendientes = new ArrayDeque<>();
        padre.put(origen, null);
        pendientes.add(origen);

        while (!pendientes.isEmpty()) {
            Coordenada actual = pendientes.remove();
            if (actual.equals(destino))
                return reconstruir(padre, destino);

            for (int[] d : DIRECCIONES) {
                Coordenada siguiente = actual.desplazar(d[0], d[1]);
                if (!universo.contiene(siguiente) || padre.containsKey(siguiente))
                    continue;
                if (!siguiente.equals(destino) && hayObstaculo(universo, siguiente))
                    continue;
                padre.put(siguiente, actual);
                pendientes.add(siguiente);
            }
        }
        return Collections.emptyList();   // no hay camino
    }

    private boolean hayObstaculo(Universo universo, Coordenada c) {
        for (ElementoDelUniverso e : universo.elementosEn(c))
            if (e.esObstaculo()) return true;
        return false;
    }

    private List<Coordenada> reconstruir(Map<Coordenada, Coordenada> padre, Coordenada destino) {
        LinkedList<Coordenada> ruta = new LinkedList<>();
        for (Coordenada c = destino; c != null; c = padre.get(c))
            ruta.addFirst(c);
        return new ArrayList<>(ruta);
    }
}