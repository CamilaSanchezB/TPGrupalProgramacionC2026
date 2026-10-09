package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Consejero extends Tripulante {

    private int cantidadConsejos;
    private double importePorConsejo;

    public Consejero(String nombre, int antiguedad, int cantidadConsejos) {
        super(nombre, antiguedad, 600, 0.05, "Consejero");
        this.cantidadConsejos = cantidadConsejos;
        this.importePorConsejo = 2;
    }
    public Consejero(String nombre, int antiguedad) {
        this(nombre, antiguedad, 0);
    }
    public Consejero(String nombre){
        this(nombre, 0);
    }

    public int getCantidadConsejos() {
        return cantidadConsejos;
    }

    public void setCantidadConsejos(int cantidadConsejos) {
        this.cantidadConsejos = validarCantidadConsejos(cantidadConsejos);
    }

    private static int validarCantidadConsejos(int cantidadConsejos) {
        assert cantidadConsejos >= 0
                : "La cantidad de consejos no puede ser negativa: " + cantidadConsejos;
        return cantidadConsejos;
    }

    public double getImportePorConsejo() {
        return importePorConsejo;
    }

    @Override
    public double calcularSueldo() {
        return remuneracion + remuneracion * porcentajeAntiguedad * antiguedad
                + cantidadConsejos * importePorConsejo;
    }


}
