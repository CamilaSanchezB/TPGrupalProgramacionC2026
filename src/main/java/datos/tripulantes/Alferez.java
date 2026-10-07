package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

public class Alferez extends Tripulante {

    public Alferez(String nombre, int antiguedad) {
        super(nombre, antiguedad, 200, 0.02, "Alferez");
    }

    public Alferez(String nombre){
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
