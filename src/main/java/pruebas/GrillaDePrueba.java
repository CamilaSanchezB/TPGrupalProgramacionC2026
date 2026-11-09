package pruebas;

import java.util.ArrayList;
import java.util.List;

import model.Coordenada;
import model.Universo.ElementoDelUniverso;
import model.Universo.Universo;

public class GrillaDePrueba implements Universo {
    private final int ancho;
    private final int alto;
    private final List<ElementoDelUniverso> elementos = new ArrayList<>();

    public GrillaDePrueba(int ancho, int alto) {
        if (ancho <= 0 || alto <= 0)
            throw new IllegalArgumentException("La grilla necesita dimensiones positivas");
        this.ancho = ancho;
        this.alto = alto;
    }

    public void agregar(ElementoDelUniverso e) {
        if (e == null || !contiene(e.getPosicion()))
            throw new IllegalArgumentException("Elemento nulo o fuera de la grilla");
        elementos.add(e);
    }

    @Override
    public boolean contiene(Coordenada c) {
        return c != null && c.getX() >= 0 && c.getX() < ancho && c.getY() >= 0 && c.getY() < alto;
    }

    @Override
    public List<ElementoDelUniverso> elementosEn(Coordenada c) {
        List<ElementoDelUniverso> resultado = new ArrayList<>();
        for (ElementoDelUniverso e : elementos)
            if (e.getPosicion().equals(c)) resultado.add(e);
        return resultado;
    }
}