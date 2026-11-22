package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Foto inmutable de lo que el panel principal necesita mostrar. La vista no toca el modelo. */
public final class EstadoNaveVista {
    public final String tipo;
    public final boolean destruida;
    public final boolean requiereMantenimiento;
    public final int combustible, energia, desgaste, gemas;
    public final String motor;
    public final boolean alcanzaM01, alcanzaM02, alcanzaM03;
    public final List<String> tripulantes;
    public final List<String> historial;
    public final int misionesExitosas, totalMisiones;

    public EstadoNaveVista(String tipo, boolean destruida, boolean requiereMantenimiento,
                           int combustible, int energia, int desgaste, int gemas, String motor,
                           boolean alcanzaM01, boolean alcanzaM02, boolean alcanzaM03,
                           List<String> tripulantes, List<String> historial,
                           int misionesExitosas, int totalMisiones) {
        this.tipo = tipo;
        this.destruida = destruida;
        this.requiereMantenimiento = requiereMantenimiento;
        this.combustible = combustible;
        this.energia = energia;
        this.desgaste = desgaste;
        this.gemas = gemas;
        this.motor = motor;
        this.alcanzaM01 = alcanzaM01;
        this.alcanzaM02 = alcanzaM02;
        this.alcanzaM03 = alcanzaM03;
        this.tripulantes = Collections.unmodifiableList(new ArrayList<String>(tripulantes));
        this.historial = Collections.unmodifiableList(new ArrayList<String>(historial));
        this.misionesExitosas = misionesExitosas;
        this.totalMisiones = totalMisiones;
    }
}