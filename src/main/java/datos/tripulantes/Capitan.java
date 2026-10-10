package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Capitan extends Tripulante {

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * - antiguedad >= 0
     * <b>POST</b>
     * - crea un Capitan con remuneracion 1000 y porcentaje por antiguedad 0.10
     */
    public Capitan(String nombre, int antiguedad) {
        super(nombre, antiguedad, 1000, 0.20, "Capitan");
    }
    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * <b>POST</b>
     * - crea un Capitan con antiguedad 0
     */
    public Capitan(String nombre){
        this(nombre, 0);
    }
    /**
     * <b>POST</b>
     * - devuelve remuneracion + remuneracion * porcentajeAntiguedad * antiguedad
     */
    @Override
    public double calcularSueldo() {
        return remuneracion + remuneracion * porcentajeAntiguedad * antiguedad;
    }

}
