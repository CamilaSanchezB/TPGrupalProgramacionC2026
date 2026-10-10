package datos.tripulantes;
import java.util.ArrayList;


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
    public ArrayList<ConceptoHaber> getListaConceptos() {
        ArrayList<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 30));
        return conceptos;
    }
}
