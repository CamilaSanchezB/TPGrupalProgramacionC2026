package model.Universo;

import model.Coordenada;
import model.destinoytablero.Destino;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adaptador (patrón Adapter): conecta el Tablero provisto por la cátedra con UniversoDinamico.
 * Guarda una copia (foto) de la matriz del tablero para no reconstruirla en cada consulta, y la
 * refresca cuando Destino mueve elementos.
 *
 * INV: tablero, destino y foto != null
 * INV: foto tiene las dimensiones del tablero (getX() filas por getY() columnas)
 * INV: para todo elemento e que está en foto[i][j]: e.getPosicion() equivale a (i, j)
 * INV: reveladas contiene exactamente la casilla de la nave y sus vecinas dentro del tablero
 *      (es lo único que la vista muestra del contenido del tablero)
 * INV: foto refleja el estado del tablero, salvo en el instante entre un cambio de elementos
 *      en el tablero y el refrescar() siguiente (solo Destino mueve elementos, y el adaptador
 *      refresca justo después de cada turno)
 */
public class AdaptadorTableroUniverso implements UniversoDinamico {
    private final Tablero tablero;
    private final Destino destino;
    private ElementoDelUniverso[][] foto;
    private final Set<Posicion> reveladas = Collections.synchronizedSet(new HashSet<Posicion>());

    /**
     * PRE: tablero != null
     * POST: la foto está sincronizada con el tablero y cada elemento conoce su posición real
     * @throws IllegalArgumentException si tablero es null
     */
    public AdaptadorTableroUniverso(Tablero tablero) {
        if (tablero == null) {
            throw new IllegalArgumentException("El tablero no puede ser nulo");
        }
        this.tablero = tablero;
        this.destino = new Destino(tablero);
        refrescar();
        revelarAlrededor(tablero.getPosicionNave().getX(), tablero.getPosicionNave().getY());
    }

    /**
     * POST: es una consulta; devuelve el tablero de la cátedra (para que la vista se suscriba
     *       como Observador); no modifica nada
     */
    public Tablero getTablero() {
        return this.tablero;
    }

    /**
     * POST: es una consulta; devuelve el conjunto vivo de casillas exploradas (la vista lo lee)
     */
    public Set<Posicion> getReveladas() {
        return reveladas;
    }

    /**
     * Revela la casilla (x, y) y sus vecinas.
     * POST: reveladas == esas casillas, las que caen dentro del tablero; se arma de forma atómica
     *       para que la vista nunca vea el conjunto a medio armar
     */
    private void revelarAlrededor(int x, int y) {
        synchronized (reveladas) {
            reveladas.clear();
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (contiene(new Coordenada(x + dx, y + dy)))
                        reveladas.add(new Posicion(x + dx, y + dy));
        }
    }

    /**
     * POST: es una consulta; true si y solo si c != null, 0 <= x < filas y 0 <= y < columnas
     */
    @Override
    public boolean contiene(Coordenada c) {
        if (c == null) {
            return false;
        }
        return c.getX() >= 0 && c.getX() < tablero.getX()
                && c.getY() >= 0 && c.getY() < tablero.getY();
    }

    /**
     * PRE: contiene(c)
     * POST: es una consulta; devuelve una lista nueva con el elemento de la casilla c, o vacía
     *       si está libre o si c no está dentro del universo (no lanza excepción)
     */
    @Override
    public List<ElementoDelUniverso> elementosEn(Coordenada c) {
        List<ElementoDelUniverso> resultado = new ArrayList<>();
        if (!contiene(c)) return resultado;
        ElementoDelUniverso e = foto[c.getX()][c.getY()];
        if (e != null) resultado.add(e);
        return resultado;
    }

    /**
     * Destino mueve los elementos en el tablero pero no actualiza su setPosicion: acá se sincroniza.
     * POST: foto == estado actual del tablero y cada elemento declara su casilla real
     */
    private void refrescar() {
        foto = tablero.verTablero();
        for (int i = 0; i < foto.length; i++)
            for (int j = 0; j < foto[i].length; j++)
                if (foto[i][j] != null) foto[i][j].setPosicion(new Coordenada(i, j));
    }

    /**
     * PRE: c != null y contiene(c)
     * POST: reveladas se actualiza ANTES de mover (así cuando el tablero notifica a la vista ya
     *       conoce las casillas nuevas); el tablero ubica la nave en c y avisa a sus observadores;
     *       los elementos no cambian
     * @throws IllegalArgumentException si se viola la PRE (la nave no se mueve)
     */
    @Override
    public void ubicarNave(Coordenada c) {
        if (!contiene(c))
            throw new IllegalArgumentException("La nave debe ubicarse dentro del universo");
        revelarAlrededor(c.getX(), c.getY());
        tablero.moverNave(new Posicion(c.getX(), c.getY()));
    }

    /**
     * POST: es una consulta; devuelve la casilla actual de la nave según el tablero
     */
    @Override
    public Coordenada getPosicionNave() {
        Posicion p = tablero.getPosicionNave();
        return new Coordenada(p.getX(), p.getY());
    }

    /**
     * POST: es una consulta; devuelve el objetivo del tablero, o null si todavía no se eligió
     *       (el tablero lo elige con obtenerObjetivo())
     */
    @Override
    public Coordenada getObjetivo() {
        Posicion p = tablero.getObjetivo();
        return p == null ? null : new Coordenada(p.getX(), p.getY());
    }

    /**
     * POST: es una consulta; devuelve el elemento que hay en la casilla de la nave según el
     *       tablero, o null si está libre
     */
    @Override
    public ElementoDelUniverso inspeccionarNave() {
        return tablero.inspeccionarPosicion();
    }

    /**
     * POST: Destino movió la mitad de los elementos (nunca sobre el objetivo), los observadores
     *       fueron notificados, y la foto y las posiciones de los elementos están actualizadas
     */
    @Override
    public void turnoDestino() {
        destino.turnoDestino();
        refrescar();
    }
}