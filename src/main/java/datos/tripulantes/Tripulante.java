package datos.tripulantes;

import java.util.Iterator;
import java.util.List;

public abstract class Tripulante implements Liquidable {

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
     */
    protected Tripulante() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarNombre(nombre);
    }

    private static String validarNombre(String nombre) {
        assert nombre != null && !nombre.trim().isEmpty()
                : "El nombre del tripulante no puede estar vacio.";
        return nombre;
    }

    public double getRemuneracion() {
        return remuneracion;
    }

    private static double validarRemuneracion(double remuneracion) {
        assert Double.isFinite(remuneracion) && remuneracion >= 0
                : "Remuneracion invalida para el cargo: " + remuneracion;
        return remuneracion;
    }

    public double getPorcentajeAntiguedad() {
        return porcentajeAntiguedad;
    }

    private static double validarPorcentajeAntiguedad(double porcentajeAntiguedad) {
        assert Double.isFinite(porcentajeAntiguedad) && porcentajeAntiguedad >= 0
                : "Porcentaje por antiguedad invalido: " + porcentajeAntiguedad;
        return porcentajeAntiguedad;
    }

    public String getCargo() {
        return cargo;
    }

    public String getOrigen() {
        return origen;
    }

    public int getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(int antiguedad) {
        this.antiguedad = validarAntiguedad(antiguedad);
    }

    private static int validarAntiguedad(int antiguedad) {
        assert antiguedad >= 0
                : "La antiguedad no puede ser negativa: " + antiguedad;
        return antiguedad;
    }

    public String getConceptos() {
        String detalle = "";
        Iterator<ConceptoHaber> iterador = getListaConceptos().iterator();
        while (iterador.hasNext()) {
            ConceptoHaber concepto = iterador.next();
            detalle = detalle + concepto.toString() + "\n";
        }
        return detalle;
    }


}
