package model.Universo;

import model.Coordenada;

/**
 * Universo que además de consultarse "cambia": tiene una nave ubicada, un objetivo y un turno de
 * Destino que mueve los elementos. Es lo que necesita la misión; la vista de solo lectura
 * (contiene, elementosEn) sigue siendo Universo, que usan los radares.
 *
 * INV: la posición de la nave está siempre dentro del universo
 * INV: si el objetivo ya fue elegido, está dentro del universo y su casilla está libre
 * INV: cada casilla tiene a lo sumo un elemento
 * INV: la posición que cada elemento declara (getPosicion) coincide con su casilla real
 *      cada vez que se consulta el universo
 */
public interface UniversoDinamico extends Universo {

    /**
     * Ubica la nave en la casilla indicada.
     * PRE: c != null y contiene(c)
     * POST: getPosicionNave().equals(c); los elementos no cambian de casilla
     * @throws IllegalArgumentException si se viola la PRE (la nave no se mueve)
     */
    void ubicarNave(Coordenada c);

    /**
     * POST: es una consulta; devuelve la casilla actual de la nave (nunca null, siempre contenida)
     */
    Coordenada getPosicionNave();

    /**
     * POST: es una consulta; devuelve el objetivo del universo, o null si todavía no se eligió
     */
    Coordenada getObjetivo();

    /**
     * POST: es una consulta; devuelve el elemento que está en la casilla de la nave,
     *       o null si la casilla está libre
     */
    ElementoDelUniverso inspeccionarNave();

    /**
     * Turno de Destino: mueve al azar la mitad de los elementos a una casilla vecina libre.
     * POST: cada elemento sigue en una casilla distinta y dentro del universo, ninguno quedó
     *       sobre el objetivo, la nave y el objetivo no se movieron, y la posición de cada
     *       elemento coincide con su casilla real
     */
    void turnoDestino();
}