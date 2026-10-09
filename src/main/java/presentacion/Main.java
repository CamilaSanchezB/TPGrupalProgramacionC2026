package presentacion;

import datos.*;
import datos.ClasesBitacora.Bitacora;
import datos.nave.*;
import datos.tripulantes.*;
import negocio.*;

public class Main {
    public static void main(String[] args){
        escenarioC();
        escenarioD();
    }


    // --- ESCENARIOS ---

    public static void escenarioC() {
        System.out.println("ESCENARIO C:");
        Bitacora miBitacora = new Bitacora();
        Nave miNave = NaveFactory.crearNave("combate");
        AsistenteDeComando asistente = new AsistenteDeComando(miNave, miBitacora);

        // 1. Recorrer secuancia valida
        System.out.println("1. Inento secuancia valida:");
        System.out.println();

        System.out.println("Secuancia posible 1 (con enfriamiento)");
        if (asistente.prepararSalto())
            System.out.println("Preparando salto");
        if (asistente.saltar())
            System.out.println("Saltando");
        if (asistente.iniciarEnfriamiento())
            System.out.println("En enfriamineto");
        if (asistente.estarDisponible())
            System.out.println("La nave está disponible");
        System.out.println();

        System.out.println("Secuancia posible 2 (sin enfriamiento)");
        if (asistente.prepararSalto())
            System.out.println("Preparando salto");
        if (asistente.saltar())
            System.out.println("Saltando");
        if (asistente.terminarSalto())
            System.out.println("Terminando salto");
        if (asistente.estaDisponible())
            System.out.println("La nave está disponible");
        System.out.println();

        // 2. Recorrer secuancia invalida
        System.out.println("2. Inento secuancia invalida:");
        System.out.println("Se intentara saltar sin haber preparado la nave");

        if(!asistente.saltar())
            System.out.println("Accion rechazada correctamente por el asistente");
        System.out.println();

        // 3. Verificamos registro en la bitacoora
        System.out.println("Verificamos que exista la accion rechazada en la bitacora");
        asistente.mostrarReporteBitacora();
    }


    public static void escenarioD() {
        System.out.println("ESCENARIO D:");
        Bitacora miBitacora = new Bitacora();
        Nave miNave = NaveFactory.crearNave("Carguero");
        AsistenteDeComando asistente = new AsistenteDeComando(miNave, miBitacora);

        // 1. Mostrar estado inicial
        System.out.println("1. Estado inicial de la nave:");
        System.out.println("Combustible: " + asistente.getCombustible());
        System.out.println("Energia: " + asistente.getEnergia());
        System.out.println("Desgaste: " + asistente.getDesgaste());
        System.out.println();

        // 2. Intentar carga que exceda la capacidad maxima
        System.out.println("2. Intentando cargar 50 unidades extra de combustible");
        try {
            asistente.cargarCombustible(50);
            System.out.println("Carga realizada");
        } catch (OperacionRechazadaException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // 3. Intentar una carga con valor negativo
        System.out.println("\n3. Intentando cargar una cantidad negativa (-10 unidades)...");
        try {
            asistente.cargarCombustible(-10);
            System.out.println("Carga realizada");
        } catch (OperacionRechazadaException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // 4. Verificar que el estado anterior se conservó (invariante)
        System.out.println("4. Verificando conservación del estado anterior:");
        int combustibleFinal = asistente.getCombustible();
        System.out.println("   Combustible final: " + combustibleFinal);
        if (combustibleFinal == 100) {
            System.out.println("CORRECTO: El estado anterior se conservó.");
        } else {
            System.out.println("ERROR: El combustible se modificó.");
        }

        // 5. Verificar el registro en la Bitácora
        System.out.println("5. Verificamos los rechazos en la bitácora:");
        asistente.mostrarReporteBitacora();
    }
}
