package model.Universo;

import java.util.List;
import model.Coordenada;

/**
 * Lo mínimo que los radares necesitan saber del tablero.
 * Hoy lo implementa una grilla de prueba; con Destino, un adaptador.
 */
public interface Universo {

    /** @return true si c es una coordenada válida del tablero (false si c es null) */
    boolean contiene(Coordenada c);

    /**
     * PRE: contiene(c) == true
     * @return lista nueva con los elementos que están en c (vacía si no hay ninguno)
     */
    List<ElementoDelUniverso> elementosEn(Coordenada c);
}