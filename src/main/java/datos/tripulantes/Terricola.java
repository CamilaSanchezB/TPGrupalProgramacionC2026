package datos.tripulantes;
import java.util.ArrayList;


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
    public ArrayList<ConceptoHaber> getListaConceptos() {
        ArrayList<ConceptoHaber>conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 20));
        return conceptos;
    }
}
