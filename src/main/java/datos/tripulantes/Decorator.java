package datos.tripulantes;


public abstract class Decorator extends Tripulante {

    // Instancia de la clase Tripulante que el decorador encapsula
    protected Tripulante tripulante;

    // Constructor que recibe el tripulante que encapsulara el decorador
    /**
     * <b>PRE</b>
     * - tripulante != null
     * El tripulante no debe tener un origen ya asignado
     * @param tripulante tripulante encapsulado
     */

    public Decorator(Tripulante tripulante) {
        assert tripulante != null
                : "El tripulante a decorar no puede ser nulo.";
        assert SIN_ORIGEN.equals(tripulante.getOrigen())
                : "El tripulante ya tiene origen asignado: " + tripulante.getOrigen();
        this.tripulante = tripulante;
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
    public int getAntiguedad() {
        return tripulante.getAntiguedad();
    }

    @Override
    public void setAntiguedad(int antiguedad) {
        tripulante.setAntiguedad(antiguedad);
    }

}
