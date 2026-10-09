package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Alferez extends Tripulante {

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * - antiguedad >= 0
     * <b>POST</b>
     * - crea un Alferez con remuneracion 200 y porcentaje por antiguedad 0.02
     */
    public Alferez(String nombre, int antiguedad) {
        super(nombre, antiguedad, 200, 0.02, "Alferez");
    }

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * <b>POST</b>
     * - crea un Alferez con antiguedad 0
     */
    public Alferez(String nombre){
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
