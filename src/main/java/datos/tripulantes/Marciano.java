package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;


public class Marciano extends Decorator {

    public Marciano(Tripulante tripulante) {
        super(tripulante);
        origen = "Marciano"; //origen de marciano? o tripulante.origen = marciano?
    }

    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + 18;
    }

    @Override
    public List<ConceptoHaber> getListaConceptos() {
        List<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 18));
        return conceptos;
    }
}
