package controller;

import java.util.Iterator;
import java.util.List;

import model.nave.Nave;
import model.tripulantes.Tripulante;

/**
 * Liquida los haberes de los tripulantes: arma el recibo de cada uno
 * (datos, conceptos y sueldo) y el total a pagar de una tripulacion.
 * Liquidar cierra el periodo de cada tripulante (reiniciarPeriodo()): los consejos del consejero vuelven a 0.
 */
public class LiquidadorHaberes {

    /**
     * <b>PRE</b>
     * - tripulante != null
     * <b>POST</b>
     * - devuelve el recibo del tripulante: cargo, nombre, origen y antiguedad,
     *   un concepto por linea y el sueldo calculados antes de reiniciar el periodo
     * - el periodo del tripulante queda reiniciado (reiniciarPeriodo()); en el caso
     *   del consejero, cantidadConsejos == 0
     */
    public String liquidar(Tripulante tripulante) {
        assert tripulante != null : "El tripulante a liquidar no puede ser nulo.";
        String recibo = tripulante.getCargo() + " " + tripulante.getNombre()
                + " (" + tripulante.getOrigen() + ", " + tripulante.getAntiguedad() + " anios)\n"
                + tripulante.getConceptos()
                + "Su sueldo es: " + tripulante.calcularSueldo() + " PG\n";
        tripulante.reiniciarPeriodo();
        return recibo;
    }

    /**
     * <b>PRE</b>
     * - tripulantes != null y no contiene elementos null
     * <b>POST</b>
     * - devuelve la suma de calcularSueldo() de todos los tripulantes
     *   (0 si la lista esta vacia); no modifica nada (sirve para consultar el total
     *   antes de liquidar)
     */
    public double calcularTotal(List<Tripulante> tripulantes) {
        assert tripulantes != null : "La lista de tripulantes no puede ser nula.";
        double total = 0;
        Iterator<Tripulante> iterador = tripulantes.iterator();
        while (iterador.hasNext()) {
            Tripulante tripulante = iterador.next();
            assert tripulante != null : "La lista no puede contener tripulantes nulos.";
            total = total + tripulante.calcularSueldo();
        }
        return total;
    }

    /**
     * <b>PRE</b>
     * - tripulantes != null y no contiene elementos null
     * <b>POST</b>
     * - devuelve el encabezado, el recibo de cada tripulante en el orden de la lista
     *   separados por una linea en blanco, y al final el total a pagar
     *   (suma de los sueldos liquidados antes de reiniciar cada periodo)
     * - el periodo de cada tripulante queda reiniciado
     */
    public String liquidar(List<Tripulante> tripulantes) {
        assert tripulantes != null : "La lista de tripulantes no puede ser nula.";
        StringBuilder detalle = new StringBuilder("=== LIQUIDACION DE HABERES ===\n");
        double total = 0;
        Iterator<Tripulante> iterador = tripulantes.iterator();
        while (iterador.hasNext()) {
            Tripulante tripulante = iterador.next();
            assert tripulante != null : "La lista no puede contener tripulantes nulos.";
            // El sueldo se suma antes de liquidar, porque liquidar reinicia el periodo.
            total = total + tripulante.calcularSueldo();
            detalle.append(liquidar(tripulante)).append("\n");
        }
        detalle.append("Total a pagar: ").append(total).append(" PG\n");
        return detalle.toString();
    }

    /**
     * <b>PRE</b>
     * - nave != null
     * <b>POST</b>
     * - devuelve la liquidacion de toda la tripulacion de la nave
     *   (liquidar(nave.getTripulantes()))
     * - el periodo de cada tripulante de la nave queda reiniciado
     */
    public String liquidar(Nave nave) {
        assert nave != null : "La nave no puede ser nula.";
        return liquidar(nave.getTripulantes());
    }

}
