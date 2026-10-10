package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Teniente extends Tripulante {

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * - antiguedad >= 0
     * <b>POST</b>
     * - crea un Teniente con remuneracion 400 y porcentaje por antiguedad 0.03
     */
    public Teniente(String nombre, int antiguedad) {
        super(nombre, antiguedad, 400, 0.03, "Teniente");
    }
    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * <b>POST</b>
     * - crea un Teniente con antiguedad 0
     */
    public Teniente(String nombre) {
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
