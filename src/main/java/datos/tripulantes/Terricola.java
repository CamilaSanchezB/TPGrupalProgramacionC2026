package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;


public class Terricola extends Decorator {

    public Terricola(Tripulante tripulante) {
        super(tripulante);
        origen = "Terricola";
    }

    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + 20;
    }

    @Override
    public List<ConceptoHaber> getListaConceptos() {
        List<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 20));
        return conceptos;
    }
}
