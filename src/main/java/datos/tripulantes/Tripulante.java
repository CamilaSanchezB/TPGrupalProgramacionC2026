package datos.tripulantes;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <b>INV</b>
 * - nombre != null && nombre no vacio
 * - antiguedad >= 0
 * - remuneracion >= 0
 * - porcentajeAntiguedad >= 0 y finito
 * - cargo != null, origen != null
 */
public abstract class Tripulante {

    public static String SIN_CARGO = "Sin cargo asignado";
    public static String SIN_ORIGEN = "Sin origen asignado";

    protected String nombre;
    protected String cargo = SIN_CARGO;
    protected String origen = SIN_ORIGEN;
    protected double remuneracion;
    protected double porcentajeAntiguedad;
    protected int antiguedad;

    /**
     * <B>PRE</B>
     * - nombre != "" && nombre != null
     * - antiguedad >= 0
     * - remuneracion >= 0
     * - porcentajeAntiguedad >= 0
     * <b>POST</b>
     * - el tripulante queda con los datos indicados y origen = SIN_ORIGEN
     */
    public Tripulante(String nombre, int antiguedad, double remuneracion, double porcentajeAntiguedad, String cargo) {
        this.nombre = validarNombre(nombre);
        this.antiguedad = validarAntiguedad(antiguedad);
        this.remuneracion = validarRemuneracion(remuneracion);
        this.porcentajeAntiguedad = validarPorcentajeAntiguedad(porcentajeAntiguedad);
        this.cargo = cargo;
    }

    /**
     * Constructor de los decoradores. No reciben los datos del tripulante:
     * los delegan en el objeto que encapsulan.
     * <b>POST</b>
     * - el tripulante queda con cargo = SIN_CARGO y origen = SIN_ORIGEN
     */
    protected Tripulante() {
    }

    /**
     * <b>POST</b>
     * - devuelve el nombre del tripulante; no modifica nada
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * <b>POST</b>
     * - el nombre del tripulante pasa a ser nombre
     */
    public void setNombre(String nombre) {
        this.nombre = validarNombre(nombre);
    }

    private static String validarNombre(String nombre) {
        assert nombre != null && !nombre.trim().isEmpty()
                : "El nombre del tripulante no puede estar vacio.";
        return nombre;
    }

    /**
     * <b>POST</b>
     * - devuelve la remuneracion base del cargo (>= 0); no modifica nada
     */
    public double getRemuneracion() {
        return remuneracion;
    }

    private static double validarRemuneracion(double remuneracion) {
        assert  remuneracion >= 0
                : "Remuneracion invalida para el cargo: " + remuneracion;
        return remuneracion;
    }

    /**
     * <b>POST</b>
     * - devuelve el porcentaje por antiguedad (>= 0); no modifica nada
     */
    public double getPorcentajeAntiguedad() {
        return porcentajeAntiguedad;
    }

    private static double validarPorcentajeAntiguedad(double porcentajeAntiguedad) {
        assert Double.isFinite(porcentajeAntiguedad) && porcentajeAntiguedad >= 0
                : "Porcentaje por antiguedad invalido: " + porcentajeAntiguedad;
        return porcentajeAntiguedad;
    }

    /**
     * <b>POST</b>
     * - devuelve el cargo del tripulante (SIN_CARGO si no tiene); no modifica nada
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * <b>POST</b>
     * - devuelve el origen del tripulante (SIN_ORIGEN si no tiene); no modifica nada
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * <b>POST</b>
     * - devuelve la antiguedad en anios (>= 0); no modifica nada
     */
    public int getAntiguedad() {
        return antiguedad;
    }

    /**
     * <b>PRE</b>
     * - antiguedad >= 0
     * <b>POST</b>
     * - la antiguedad del tripulante pasa a ser antiguedad
     */
    public void setAntiguedad(int antiguedad) {
        this.antiguedad = validarAntiguedad(antiguedad);
    }

    private static int validarAntiguedad(int antiguedad) {
        assert antiguedad >= 0
                : "La antiguedad no puede ser negativa: " + antiguedad;
        return antiguedad;
    }

    /**
     * <b>POST</b>
     * - devuelve el sueldo del tripulante (>= 0); no modifica nada
     */
    public abstract double calcularSueldo();

    /**
     * <b>POST</b>
     * - devuelve una lista nueva con la remuneracion por cargo y, si antiguedad > 0,
     *   el adicional por antiguedad; no modifica nada
     */
    public ArrayList<ConceptoHaber>getListaConceptos() {
        ArrayList<ConceptoHaber> conceptos = new ArrayList<ConceptoHaber>();
        conceptos.add(new ConceptoHaber("Remuneracion por cargo (" + cargo + ")", remuneracion));
        if (antiguedad > 0) {
            conceptos.add(new ConceptoHaber(
                    "Adicional por antiguedad (" + antiguedad + " anios)",
                    remuneracion * porcentajeAntiguedad * antiguedad));
        }
        return conceptos;
    }

    /**
     * <b>POST</b>
     * - devuelve el detalle de getListaConceptos(), un concepto por linea; no modifica nada
     */
    public String getConceptos() {
        String detalle = "";
        Iterator<ConceptoHaber> iterador = getListaConceptos().iterator();
        while (iterador.hasNext()) {
            ConceptoHaber concepto = iterador.next();
            detalle = detalle + concepto.toString() + "\n";
        }
        return detalle;
    }

    /**
     * Se invoca al liquidar el haber del periodo. Por defecto no hay nada que
     * reiniciar; lo redefinen los cargos con conceptos variables.
     * <b>POST</b>
     * - los conceptos variables del periodo vuelven a su valor inicial
     */
    public void reiniciarPeriodo() {
    }

}
