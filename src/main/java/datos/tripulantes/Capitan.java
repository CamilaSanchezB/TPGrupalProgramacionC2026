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

    @Override
    public List<ConceptoHaber> getListaConceptos() {
        List<ConceptoHaber> conceptos = new ArrayList<ConceptoHaber>();
        conceptos.add(new ConceptoHaber("Remuneracion por cargo (" + cargo + ")", remuneracion));
        if (antiguedad > 0) {
            conceptos.add(new ConceptoHaber(
                    "Adicional por antiguedad (" + antiguedad + " anios)",
                    remuneracion * porcentajeAntiguedad * antiguedad));
        }
        return conceptos;
    }
}
