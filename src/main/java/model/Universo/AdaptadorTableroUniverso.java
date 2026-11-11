package model.Universo;

import model.Coordenada;
import model.destinoytablero.Tablero;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador que conecta el Tablero provisto por la cátedra con la interfaz Universo.
 */
public class AdaptadorTableroUniverso implements Universo {
    private final Tablero tablero;

    public AdaptadorTableroUniverso(Tablero tablero) {
        if (tablero == null) {
            throw new IllegalArgumentException("El tablero no puede ser nulo");
        }
        this.tablero = tablero;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    @Override
    public boolean contiene(Coordenada c) {
        if (c == null) {
            return false;
        }
        return c.getX() >= 0 && c.getX() < tablero.getX()
                && c.getY() >= 0 && c.getY() < tablero.getY();
    }

    @Override
    public List<ElementoDelUniverso> elementosEn(Coordenada c) {
        List<ElementoDelUniverso> resultado = new ArrayList<>();
        if (!contiene(c)) {
            return resultado;
        }

        ElementoDelUniverso[][] matriz = tablero.verTablero();
        ElementoDelUniverso elemento = matriz[c.getX()][c.getY()];

        if (elemento != null) {
            resultado.add(elemento);
        }

        return resultado;
    }
}