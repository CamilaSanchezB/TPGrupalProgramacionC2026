package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Capitan extends Tripulante {

    public Capitan(String nombre, int antiguedad) {
        super(nombre, antiguedad, 1000, 0.10, "Capitan");
    }
    public Capitan(String nombre){
        this(nombre, 0);
    }
    @Override
    public double calcularSueldo() {
        return remuneracion + remuneracion * porcentajeAntiguedad * antiguedad;
    }

}
