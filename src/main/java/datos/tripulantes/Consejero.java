package datos.tripulantes;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>INV</b>
 * - cantidadConsejos >= 0
 * - importePorConsejo >= 0
 */
public class Consejero extends Tripulante {

    private int cantidadConsejos;
    private double importePorConsejo;

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * - antiguedad >= 0
     * - cantidadConsejos >= 0
     * <b>POST</b>
     * - crea un Consejero con remuneracion 600, porcentaje por antiguedad 0.05
     *   e importe por consejo 2
     */
    public Consejero(String nombre, int antiguedad, int cantidadConsejos) {
        super(nombre, antiguedad, 600, 0.05, "Consejero");
        this.cantidadConsejos = validarCantidadConsejos(cantidadConsejos);
        this.importePorConsejo = 2;
    }
    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * - antiguedad >= 0
     * <b>POST</b>
     * - crea un Consejero sin consejos
     */
    public Consejero(String nombre, int antiguedad) {
        this(nombre, antiguedad, 0);
    }
    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * <b>POST</b>
     * - crea un Consejero con antiguedad 0 y sin consejos
     */
    public Consejero(String nombre){
        this(nombre, 0);
    }

    /**
     * <b>POST</b>
     * - devuelve la cantidad de consejos (>= 0); no modifica nada
     */
    public int getCantidadConsejos() {
        return cantidadConsejos;
    }

    /**
     * <b>PRE</b>
     * - cantidadConsejos >= 0
     * <b>POST</b>
     * - la cantidad de consejos pasa a ser cantidadConsejos
     */
    public void setCantidadConsejos(int cantidadConsejos) {
        this.cantidadConsejos = validarCantidadConsejos(cantidadConsejos);
    }

    private static int validarCantidadConsejos(int cantidadConsejos) {
        assert cantidadConsejos >= 0
                : "La cantidad de consejos no puede ser negativa: " + cantidadConsejos;
        return cantidadConsejos;
    }

    /**
     * <b>POST</b>
     * - devuelve el importe por consejo; no modifica nada
     */
    public double getImportePorConsejo() {
        return importePorConsejo;
    }

    /**
     * <b>POST</b>
     * - devuelve remuneracion + remuneracion * porcentajeAntiguedad * antiguedad
     *   + cantidadConsejos * importePorConsejo
     */
    @Override
    public double calcularSueldo() {
        return remuneracion + remuneracion * porcentajeAntiguedad * antiguedad
                + cantidadConsejos * importePorConsejo;
    }

    /**
     * <b>POST</b>
     * - devuelve los conceptos de Tripulante y, si cantidadConsejos > 0,
     *   el adicional por consejos (cantidadConsejos * importePorConsejo); no modifica nada
     */
    @Override
    public ArrayList<ConceptoHaber> getListaConceptos() {
        ArrayList<ConceptoHaber> conceptos = super.getListaConceptos();
        if (cantidadConsejos > 0) {
            conceptos.add(new ConceptoHaber("Adicional por consejos (" + cantidadConsejos + ")",
                    cantidadConsejos * importePorConsejo));
        }
        return conceptos;
    }

    /**
     * <b>POST</b>
     * - cantidadConsejos == 0
     */
    @Override
    public void reiniciarPeriodo() {
        cantidadConsejos = 0;
    }

}
