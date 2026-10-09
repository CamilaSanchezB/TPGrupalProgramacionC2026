package datos.tripulantes;

import java.util.ArrayList;


public class Vulcano extends Decorator {

    /**
     * <b>PRE</b>
     * - tripulante != null
     * - tripulante no tiene origen asignado
     * <b>POST</b>
     * - el tripulante queda decorado con origen Vulcano
     */
    public Vulcano(Tripulante tripulante) {
        super(tripulante);
        origen ="Vulcano";
    }

    /**
     * <b>POST</b>
     * - devuelve el sueldo del tripulante encapsulado + 30
     */
    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + 30;
    }

    /**
     * <b>POST</b>
     * - devuelve los conceptos del tripulante encapsulado mas el subsidio por origen (30)
     */
    @Override
    public ArrayList<ConceptoHaber> getListaConceptos() {
        ArrayList<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 30));
        return conceptos;
    }
}
