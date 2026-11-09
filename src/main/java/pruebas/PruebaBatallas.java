package pruebas;

import model.nave.Nave;
import model.nave.NaveFactory;
import model.nave.ResultadoBatalla;

public class PruebaBatallas {
    private static final NaveFactory fabrica = new NaveFactory();
    private static int fallos = 0;

    public static void main(String[] args) {
        // propia vs enemiga -> esperado (tabla del enunciado)
        probar("EXPLORADORA", "EXPLORADORA", ResultadoBatalla.PACTO_DE_PAZ);
        probar("EXPLORADORA", "CARGUERO",    ResultadoBatalla.GANA);
        probar("EXPLORADORA", "COMBATE",     ResultadoBatalla.PIERDE);

        probar("CARGUERO", "EXPLORADORA", ResultadoBatalla.PIERDE);
        probar("CARGUERO", "CARGUERO",    ResultadoBatalla.PACTO_DE_PAZ);
        probar("CARGUERO", "COMBATE",     ResultadoBatalla.GANA);

        probar("COMBATE", "EXPLORADORA", ResultadoBatalla.GANA);
        probar("COMBATE", "CARGUERO",    ResultadoBatalla.PIERDE);
        probar("COMBATE", "COMBATE",     ResultadoBatalla.PACTO_DE_PAZ);

        probarEnemigaNula();

        System.out.println(fallos == 0 ? "\nTODOS LOS CASOS OK" : "\nFALLOS: " + fallos);
    }

    private static void probar(String tipoPropia, String tipoEnemiga, ResultadoBatalla esperado) {
        Nave propia = fabrica.crearNave(tipoPropia);
        Nave enemiga = fabrica.crearNave(tipoEnemiga);
        ResultadoBatalla obtenido = propia.batallar(enemiga);
        boolean ok = obtenido == esperado;
        if (!ok) fallos++;
        System.out.println((ok ? "OK    " : "FALLO ") + tipoPropia + " vs " + tipoEnemiga
                + " -> " + obtenido + " (esperado " + esperado + ")");
    }

    private static void probarEnemigaNula() {
        Nave propia = fabrica.crearNave("EXPLORADORA");
        try {
            propia.batallar(null);
            fallos++;
            System.out.println("FALLO enemiga nula: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("OK    enemiga nula -> IllegalArgumentException");
        }
    }
}