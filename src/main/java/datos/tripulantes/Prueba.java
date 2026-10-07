package datos.tripulantes;

public class Prueba {

    public static void main(String[] args) {

        Tripulante capitan = new Capitan("TEST", 3);
        mostrar(capitan);

        capitan = new Terricola(capitan);
        mostrar(capitan);

        Tripulante alferez = new Alferez("B", 5);
        mostrar(alferez);

        alferez = new Marciano(alferez);
        mostrar(alferez);

        Consejero consejero = new Consejero("C", 5);
        consejero.setCantidadConsejos(7);
        mostrar(consejero);

        Tripulante consejeroVulcano = new Vulcano(consejero);
        mostrar(consejeroVulcano);

        Tripulante otroCapitan = new Capitan("D", 10);
        mostrar(otroCapitan);
    }

    private static void mostrar(Tripulante tripulante) {
        System.out.println(tripulante.getCargo() + " " + tripulante.getNombre()
                + " (" + tripulante.getOrigen() + ", " + tripulante.getAntiguedad() + " anios)");
        System.out.print(tripulante.getConceptos());
        System.out.println("Su sueldo es: " + tripulante.calcularSueldo() + " PG");
        System.out.println();
    }
}
