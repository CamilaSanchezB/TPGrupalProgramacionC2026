package negocio;

import java.util.Iterator;
import java.util.List;

import datos.nave.Nave;
import datos.tripulantes.Tripulante;

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
     * - tripulante != null
     * <b>POST</b>
     * - imprime por pantalla liquidar(tripulante) seguido de una linea en blanco
     * - el periodo del tripulante queda reiniciado
     */
    public void imprimirLiquidacion(Tripulante tripulante) {
        System.out.println(liquidar(tripulante));
    }

    /**
     * <b>PRE</b>
     * - tripulantes != null y no contiene elementos null
     * <b>POST</b>
     * - imprime por pantalla el recibo de cada tripulante, en el orden de la lista,
     *   y al final el total a pagar (suma de los sueldos liquidados)
     * - el periodo de cada tripulante queda reiniciado
     */
    public void imprimirLiquidacion(List<Tripulante> tripulantes) {
        assert tripulantes != null : "La lista de tripulantes no puede ser nula.";
        System.out.println("=== LIQUIDACION DE HABERES ===");
        double total = 0;
        Iterator<Tripulante> iterador = tripulantes.iterator();
        while (iterador.hasNext()) {
            Tripulante tripulante = iterador.next();
            assert tripulante != null : "La lista no puede contener tripulantes nulos.";
            total = total + tripulante.calcularSueldo();
            imprimirLiquidacion(tripulante);
        }
        System.out.println("Total a pagar: " + total + " PG");
    }

    /**
     * <b>PRE</b>
     * - nave != null
     * <b>POST</b>
     * - imprime por pantalla la liquidacion de toda la tripulacion de la nave
     *   (imprimirLiquidacion(nave.getTripulantes()))
     * - el periodo de cada tripulante de la nave queda reiniciado
     */
    public void imprimirLiquidacion(Nave nave) {
        assert nave != null : "La nave no puede ser nula.";
        imprimirLiquidacion(nave.getTripulantes());
    }
}
