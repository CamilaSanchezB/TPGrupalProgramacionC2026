package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Teniente extends Tripulante {

    public Teniente(String nombre, int antiguedad) {
        super(nombre, antiguedad, 400, 0.03, "Teniente");
    }
    public Teniente(String nombre) {
        this(nombre, 0);
    }

    @Override
    public double calcularSueldo() {
        return remuneracion + remuneracion * porcentajeAntiguedad * antiguedad;
    }


}
