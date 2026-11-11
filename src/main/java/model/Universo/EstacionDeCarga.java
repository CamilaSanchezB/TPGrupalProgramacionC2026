package model.Universo;

import model.Coordenada;
import model.Recursos;
import model.nave.Nave;

/**
 * Ayuda: recarga un monto fijo de combustible y de energía.
 * INV: posicion != null
 */
public class EstacionDeCarga implements ElementoDelUniverso {
    public static final int RECARGA_COMBUSTIBLE = 20;   // decisión del grupo
    public static final int RECARGA_ENERGIA = 20;

    private Coordenada posicion;

    public EstacionDeCarga(Coordenada posicion) {
        if (posicion == null)
            throw new IllegalArgumentException("La estación necesita una posición");
        this.posicion = posicion;
    }

    @Override
    public Coordenada getPosicion() { return posicion; }

    @Override
    public void setPosicion(Coordenada nueva) {
        if (nueva == null)
            throw new IllegalArgumentException("La posición no puede ser nula");
        this.posicion = nueva;
    }

    /**
     * POST: combustible y energía suben el monto fijo, recortado para no superar
     *       CAPACIDAD_MAXIMA; si ya están llenos no pasa nada; el desgaste no cambia
     */
    @Override
    public String interactuar(Nave propia) {
        if (propia == null)
            throw new IllegalArgumentException("La nave no puede ser nula");
        int comb = Math.min(RECARGA_COMBUSTIBLE, Recursos.CAPACIDAD_MAXIMA - propia.getCombustible());
        int ener = Math.min(RECARGA_ENERGIA, Recursos.CAPACIDAD_MAXIMA - propia.getEnergia());
        if (comb > 0) propia.cargarCombustible(comb);   // la nave exige cantidad > 0
        if (ener > 0) propia.cargarEnergia(ener);
        return "Estación de carga en " + posicion + ": +" + comb + " combustible, +" + ener + " energía";
    }
}