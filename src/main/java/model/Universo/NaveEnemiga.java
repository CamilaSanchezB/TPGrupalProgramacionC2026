package model.Universo;

import model.Coordenada;
import model.nave.Nave;
import model.nave.ResultadoBatalla;

/**
 * Nave que ocupa una casilla del tablero: cuando la nave propia coincide con ella se enfrentan.
 * El resultado lo decide el Double Dispatch de Nave (solo depende del tipo de cada nave).
 * Envuelve una Nave (composición) en lugar de heredar el contrato del tablero en Nave, así la
 * nave del jugador no necesita posición ni conocer el universo.
 *
 * INV: nave y posicion != null
 * INV: una vez derrotada (nave.estaDestruida()) no vuelve a pelear
 * INV: nunca es un obstáculo: las naves se encuentran, no se rodean
 */
public class NaveEnemiga implements ElementoDelUniverso {
    private final Nave nave;
    private Coordenada posicion;

    /**
     * PRE: nave != null y posicion != null
     * POST: la nave enemiga queda en posicion, sin cambios en nave
     * @throws IllegalArgumentException si se viola la PRE
     */
    public NaveEnemiga(Nave nave, Coordenada posicion) {
        if (nave == null)
            throw new IllegalArgumentException("La nave enemiga necesita una nave");
        if (posicion == null)
            throw new IllegalArgumentException("La nave enemiga necesita una posición");
        this.nave = nave;
        this.posicion = posicion;
    }

    /** POST: es una consulta; devuelve la nave que representa */
    public Nave getNave() { return nave; }

    /** POST: es una consulta; true si ya fue derrotada en una batalla */
    public boolean estaDerrotada() { return nave.estaDestruida(); }

    @Override
    public Coordenada getPosicion() { return posicion; }

    @Override
    public void setPosicion(Coordenada nueva) {
        if (nueva == null)
            throw new IllegalArgumentException("La posición no puede ser nula");
        this.posicion = nueva;
    }

    /**
     * Batalla entre la nave propia y esta nave.
     * PRE: propia != null
     * POST: si la propia gana, esta nave queda destruida; si la propia pierde, la propia queda
     *       destruida (propia.estaDestruida() == true); si hay pacto de paz, ninguna cambia.
     *       Una nave enemiga ya derrotada no pelea: no modifica a la propia.
     * @return descripción de lo ocurrido, para la bitácora
     * @throws IllegalArgumentException si propia es null
     */
    @Override
    public String interactuar(Nave propia) {
        if (propia == null)
            throw new IllegalArgumentException("La nave no puede ser nula");
        if (estaDerrotada())
            return "Restos de una nave enemiga " + tipo() + " en " + posicion + ": no hay nada que hacer";

        ResultadoBatalla resultado = propia.batallar(nave);
        switch (resultado) {
            case GANA:
                nave.destruir();
                return "Batalla en " + posicion + ": nuestra nave venció a una nave enemiga " + tipo();
            case PIERDE:
                propia.destruir();
                return "Batalla en " + posicion + ": nuestra nave fue destruida por una nave enemiga " + tipo();
            default:
                return "Pacto de paz en " + posicion + " con una nave enemiga " + tipo();
        }
    }

    /** @return "Exploradora", "Carguero" o "Combate" (nombre de la clase sin el prefijo Nave) */
    private String tipo() {
        return nave.getClass().getSimpleName().replaceFirst("^Nave", "");
    }

    @Override
    public String toString() {
        return "Nave enemiga " + tipo() + (estaDerrotada() ? " (derrotada)" : "");
    }
}