package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resumen inmutable de una misión terminada, listo para mostrar. */
public final class ResumenMisionVista {
    public enum Estado { EXITOSA, RECHAZADA, FALLIDA }

    public final Estado estado;
    public final String nombre, observaciones;
    public final int combustibleFinal, variacionCombustible;
    public final int energiaFinal, variacionEnergia;
    public final int desgasteFinal, variacionDesgaste;
    public final int gemasTotales;
    public final List<String> eventos;
    public final boolean naveDestruida;

    public ResumenMisionVista(Estado estado, String nombre, String observaciones,
                              int combustibleFinal, int variacionCombustible,
                              int energiaFinal, int variacionEnergia,
                              int desgasteFinal, int variacionDesgaste,
                              int gemasTotales, List<String> eventos, boolean naveDestruida) {
        this.estado = estado;
        this.nombre = nombre;
        this.observaciones = observaciones;
        this.combustibleFinal = combustibleFinal;
        this.variacionCombustible = variacionCombustible;
        this.energiaFinal = energiaFinal;
        this.variacionEnergia = variacionEnergia;
        this.desgasteFinal = desgasteFinal;
        this.variacionDesgaste = variacionDesgaste;
        this.gemasTotales = gemasTotales;
        this.eventos = Collections.unmodifiableList(new ArrayList<String>(eventos));
        this.naveDestruida = naveDestruida;
    }
}