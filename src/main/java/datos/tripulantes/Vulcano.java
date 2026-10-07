package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;


public class Vulcano extends Decorator {

    public Vulcano(Tripulante tripulante) {
        super(tripulante);
        origen ="Vulcano";
    }

    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + 30;
    }

    @Override
    public List<ConceptoHaber> getListaConceptos() {
        List<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 30));
        return conceptos;
    }
}
