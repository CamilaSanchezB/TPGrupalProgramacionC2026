package datos.tripulantes;

import java.util.ArrayList;


public class Marciano extends Decorator {

    /**
     * <b>PRE</b>
     * - tripulante != null
     * - tripulante no tiene origen asignado
     * <b>POST</b>
     * - el tripulante queda decorado con origen Marciano
     */
    public Marciano(Tripulante tripulante) {
        super(tripulante);
        origen = "Marciano"; //origen de marciano? o tripulante.origen = marciano?
    }

    /**
     * <b>POST</b>
     * - devuelve el sueldo del tripulante encapsulado + 18
     */
    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + 18;
    }

    /**
     * <b>POST</b>
     * - devuelve los conceptos del tripulante encapsulado mas el subsidio por origen (18)
     */
    @Override
    public ArrayList<ConceptoHaber> getListaConceptos() {
        ArrayList<ConceptoHaber> conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 18));
        return conceptos;
    }
}
