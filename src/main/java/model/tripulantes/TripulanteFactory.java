package model.tripulantes;

public class TripulanteFactory {

    /**
     * <b>PRE</b>
     * - nombre != null && nombre no vacio
     * - antiguedad >= 0
     * - cargo != null
     * - origen != null
     * - cantidadConsejos >= 0
     * <b>POST</b>
     * - devuelve un tripulante del cargo indicado, decorado con su origen si tiene uno;
     *   si el origen no es conocido (por ejemplo SIN_ORIGEN) queda sin decorar
     * - cantidadConsejos solo lo usa el Consejero; los demas cargos lo ignoran
     * @throws IllegalArgumentException si el cargo no es conocido
     */
    public Tripulante crearTripulante(String nombre, int antiguedad, String cargo, String origen,
            int cantidadConsejos) {
        return decorarPorOrigen(crearPorCargo(nombre, antiguedad, cargo, cantidadConsejos), origen);
    }

    /**
     * <b>POST</b>
     * - devuelve el tripulante base del cargo indicado, sin origen
     * @throws IllegalArgumentException si el cargo no es conocido
     */
    private Tripulante crearPorCargo(String nombre, int antiguedad, String cargo, int cantidadConsejos) {
        switch (cargo) {
        case "Capitan":
            return new Capitan(nombre, antiguedad);
        case "Alferez":
            return new Alferez(nombre, antiguedad);
        case "Consejero":
            return new Consejero(nombre, antiguedad, cantidadConsejos);
        case "Teniente":
            return new Teniente(nombre, antiguedad);
        default:
            throw new IllegalArgumentException("Cargo desconocido: " + cargo);
        }
    }

    /**
     * <b>POST</b>
     * - devuelve el tripulante envuelto en el decorador de su origen, o el mismo
     *   tripulante si el origen no corresponde a ninguno
     */
    private Tripulante decorarPorOrigen(Tripulante tripulante, String origen) {
        switch (origen) {
        case "Terricola":
            return new Terricola(tripulante);
        case "Marciano":
            return new Marciano(tripulante);
        case "Vulcano":
            return new Vulcano(tripulante);
        default:
            return tripulante;
        }
    }
}
