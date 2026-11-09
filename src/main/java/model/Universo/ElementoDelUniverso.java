package model.Universo;
import model.Coordenada;
import model.nave.Nave;

/**
 * Todo lo que puede estar en una coordenada del tablero.
 * INV: la posición nunca es null
 */
public interface ElementoDelUniverso {

    Coordenada getPosicion();

    /** PRE: nueva != null. Lo usa Destino para mover el elemento. */
    void setPosicion(Coordenada nueva);

    /**
     * La nave propia coincide en coordenada con este elemento.
     * PRE: propia != null
     * POST: aplica las consecuencias sobre la nave (recursos o destrucción)
     * @return descripción de lo ocurrido, para la bitácora
     * @throws IllegalArgumentException si propia es null
     */
    String interactuar(Nave propia);

    /** @return true si la ruta debe rodear este elemento (los asteroides) */
    default boolean esObstaculo() { return false; }
}