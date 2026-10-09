package datos.tripulantes;

import java.util.ArrayList;


public class Terricola extends Decorator {

    /**
     * <b>PRE</b>
     * - tripulante != null
     * - tripulante no tiene origen asignado
     * <b>POST</b>
     * - el tripulante queda decorado con origen Terricola
     */
    public Terricola(Tripulante tripulante) {
        super(tripulante);
        origen = "Terricola";
    }

    /**
     * <b>POST</b>
     * - devuelve el sueldo del tripulante encapsulado + 20
     */
    @Override
    public double calcularSueldo() {
        return tripulante.calcularSueldo() + 20;
    }

    /**
     * <b>POST</b>
     * - devuelve los conceptos del tripulante encapsulado mas el subsidio por origen (20)
     */
    @Override
    public ArrayList<ConceptoHaber> getListaConceptos() {
        ArrayList<ConceptoHaber>conceptos = new ArrayList<>(tripulante.getListaConceptos());
        conceptos.add(new ConceptoHaber("Subsidio por origen (" + origen + ")", 20));
        return conceptos;
    }
}
