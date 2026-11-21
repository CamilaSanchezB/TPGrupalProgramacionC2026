package model.Universo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Coordenada;
import model.destinoytablero.Destino;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

public class AdaptadorTableroUniverso implements Universo {
    private final Tablero tablero;
    private final Destino destino;
    private final Set<Posicion> reveladas = Collections.synchronizedSet(new HashSet<Posicion>());

    public AdaptadorTableroUniverso(Tablero tablero) {
        if (tablero == null) throw new IllegalArgumentException("El tablero no puede ser nulo");
        this.tablero = tablero;
        this.destino = new Destino(tablero);
        revelarAlrededor(tablero.getPosicionNave().getX(), tablero.getPosicionNave().getY());
    }

    public Tablero getTablero() { return tablero; }

    /** Casillas ya exploradas (la vista solo muestra el contenido de estas). */
    public Set<Posicion> getReveladas() { return reveladas; }

    /** Turno del tablero: Destino mueve los elementos al azar. */
    public void turnoDestino() { destino.turnoDestino(); }

    /** Revela las vecinas ANTES de mover, así cuando el tablero notifica la vista ya las conoce. */
    public void actualizarPosicionNave(Coordenada p) {
        revelarAlrededor(p.getX(), p.getY());
        tablero.moverNave(new Posicion(p.getX(), p.getY()));
    }

    /** Equivalente público de Tablero.vecinasDe (que es private), incluyendo la casilla propia. */
    private void revelarAlrededor(int x, int y) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                if (contiene(new Coordenada(x + dx, y + dy)))
                    reveladas.add(new Posicion(x + dx, y + dy));
    }

    @Override
    public boolean contiene(Coordenada c) {
        return c != null && c.getX() >= 0 && c.getX() < tablero.getX()
                && c.getY() >= 0 && c.getY() < tablero.getY();
    }

    @Override
    public List<ElementoDelUniverso> elementosEn(Coordenada c) {
        List<ElementoDelUniverso> resultado = new ArrayList<>();
        if (!contiene(c)) return resultado;
        ElementoDelUniverso e = tablero.getElementoEn(new Posicion(c.getX(), c.getY()));
        if (e != null) {
            e.setPosicion(c);          // FIX: el elemento pasa a conocer su posición real en el tablero
            resultado.add(e);
        }
        return resultado;
    }
}