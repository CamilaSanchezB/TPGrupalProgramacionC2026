package datos.tripulantes;

/**
 * Concepto remunerativo que forma parte del haber mensual de un tripulante.
 * Permite mantener identificable el aporte de cada item de la liquidacion.
 */
public class ConceptoHaber {

    private String descripcion;
    private double importe;

    public ConceptoHaber(String descripcion, double importe) {
        this.descripcion = validarDescripcion(descripcion);
        this.importe = validarImporte(importe);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = validarDescripcion(descripcion);
    }

    private static String validarDescripcion(String descripcion) {
        assert descripcion != null && !descripcion.trim().isEmpty()
                : "La descripcion del concepto no puede estar vacia.";
        return descripcion;
    }

    public double getImporte() {
        return importe;
    }

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
