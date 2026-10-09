package datos.tripulantes;

/**
 * Concepto remunerativo que forma parte del haber mensual de un tripulante.
 * Permite mantener identificable el aporte de cada item de la liquidacion.
 * <b>INV</b>
 * - descripcion != null && descripcion no vacia
 * - importe >= 0 y finito
 */
public class ConceptoHaber {

    private String descripcion;
    private double importe;

    /**
     * <b>PRE</b>
     * - descripcion != null && descripcion no vacia
     * - importe >= 0 y finito
     * <b>POST</b>
     * - crea el concepto con la descripcion e importe indicados
     */
    public ConceptoHaber(String descripcion, double importe) {
        this.descripcion = validarDescripcion(descripcion);
        this.importe = validarImporte(importe);
    }

    /**
     * <b>POST</b>
     * - devuelve la descripcion; no modifica nada
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * <b>PRE</b>
     * - descripcion != null && descripcion no vacia
     * <b>POST</b>
     * - la descripcion pasa a ser descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = validarDescripcion(descripcion);
    }

    private static String validarDescripcion(String descripcion) {
        assert descripcion != null && !descripcion.trim().isEmpty()
                : "La descripcion del concepto no puede estar vacia.";
        return descripcion;
    }

    /**
     * <b>POST</b>
     * - devuelve el importe (>= 0); no modifica nada
     */
    public double getImporte() {
        return importe;
    }

    /**
     * <b>PRE</b>
     * - importe >= 0 y finito
     * <b>POST</b>
     * - el importe pasa a ser importe
     */
    public void setImporte(double importe) {
        this.importe = validarImporte(importe);
    }

    private static double validarImporte(double importe) {
        assert Double.isFinite(importe) && importe >= 0
                : "Importe invalido para el concepto: " + importe;
        return importe;
    }

    @Override
    public String toString() {
        return descripcion + ": " + importe + " PG";
    }
}
