package datos.tripulantes;

public class Prueba {

    public static void main(String[] args) {

        Tripulante capitan = new Capitan("CAMI", 3);
        capitan = new Terricola(capitan);

        mostrar(capitan);

        Tripulante teniente = new Teniente("VALEN", 5);
        teniente = new Vulcano(teniente);

        mostrar(teniente);

        Tripulante consejero = new Consejero("TINI", 5, 3);
        consejero = new Marciano(consejero);
        mostrar(consejero);

        Tripulante alferez = new Alferez("TEST",1);
        mostrar(alferez);
    }

    private static void mostrar(Tripulante tripulante) {
        System.out.println(tripulante.getCargo() + " " + tripulante.getNombre()
                + " (" + tripulante.getOrigen() + ", " + tripulante.getAntiguedad() + " anios)");
        System.out.print(tripulante.getConceptos());
        System.out.println("Su sueldo es: " + tripulante.calcularSueldo() + " PG");
        System.out.println();
    }
}
