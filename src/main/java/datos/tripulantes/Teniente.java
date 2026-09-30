package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Teniente extends Tripulante {

    public Teniente(String nombre, int antiguedad) {
        super(nombre, antiguedad, 400, 0.03);
        cargo = "Teniente";
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
