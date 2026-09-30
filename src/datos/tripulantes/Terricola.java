package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;


public class Terricola extends Decorator {

    public Terricola(Tripulante tripulante, double subsidio) {
        super(tripulante, subsidio);
        validarOrigenNoAsignado(tripulante);
        origen = "Terricola"; //origen de terricola? o tripulante.origen = marciano?
    }

    @Override
    public String getOrigen() {
        return origen;
    }

    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + subsidio;
    }

    @Override
    public List<ConceptoHaber> getListaConceptos() {
        List<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", subsidio));
        return conceptos;
    }
}
