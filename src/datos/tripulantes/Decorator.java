package datos.tripulantes;

import java.util.List;


public abstract class Decorator extends Tripulante {

    // Instancia de la clase Tripulante que el decorador encapsula
    protected Tripulante tripulante;
    protected double subsidio;

    // Constructor que recibe el tripulante que encapsulara el decorador
    /**
     * <b>PRE</b>
     * - triuplante != null
     * - subsidio >= 0
     * @param tripulante tripulante encapsulado
     * @param subsidio subsidio mensual por origen, en PG
     */
    public Decorator(Tripulante tripulante, double subsidio) {
        assert tripulante != null
                : "El tripulante a decorar no puede ser nulo.";
        validarOrigenNoAsignado(tripulante);
        this.tripulante = tripulante;
        assert subsidio >= 0
                : "Subsidio invalido por origen: " + subsidio;
        this.subsidio = subsidio;
    }
    public double getSubsidio() {
        return subsidio;
    }

    public void setSubsidio(double subsidio) {
        assert subsidio >= 0
                : "Subsidio invalido por origen: " + subsidio;
        this.subsidio = subsidio;
    }

    // Los datos de identidad se delegan en el objeto encapsulado
    @Override
    public String getNombre() {
        return tripulante.getNombre();
    }

    @Override
    public void setNombre(String nombre) {
        tripulante.setNombre(nombre);
    }

    @Override
    public String getCargo() {
        return tripulante.getCargo();
    }

    @Override
    public String getOrigen() {
        return tripulante.getOrigen();
    }

    @Override
    public int getAntiguedad() {
        return tripulante.getAntiguedad();
    }

    @Override
    public void setAntiguedad(int antiguedad) {
        tripulante.setAntiguedad(antiguedad);
    }

    protected static void validarOrigenNoAsignado(Tripulante tripulante) {
        assert SIN_ORIGEN.equals(tripulante.getOrigen())
                : "El tripulante ya tiene origen asignado: " + tripulante.getOrigen();
    }

    @Override
    public abstract double calcularSueldo();

    @Override
    public abstract List<ConceptoHaber> getListaConceptos();
}
