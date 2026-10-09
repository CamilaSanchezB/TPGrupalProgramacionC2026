package presentacion;

import datos.*;
import datos.ClasesBitacora.Bitacora;
import datos.nave.*;
import datos.tripulantes.*;
import negocio.*;

public class Main {
    public static void main(String[] args){
     //escenarioC();
     //escenarioD();
     escenarioB();
     //escenarioA();
    }


    // --- ESCENARIOS ---

    public static void escenarioC() {
        System.out.println("ESCENARIO C:");
        Bitacora miBitacora = new Bitacora();
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("combate");
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
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("carguero");
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
     
    public static void escenarioA() {
    System.out.println("ESCENARIO A: ejecución de las 3 misiones");

    for (int i = 1; i <= 3; i++) {
        System.out.println("========================================");
        System.out.println("MISIÓN " + i);

        Bitacora bitacora = new Bitacora();
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("combate");
        AsistenteDeComando asistente = new AsistenteDeComando(miNave, bitacora);

        // dejá la nave con recursos suficientes para poder ejecutar cualquier misión

        Mision mision;

        switch (i) {
            case 1:
                mision = new MisionIntercepcion(asistente);
                break;
            case 2:
                mision = new MisionRecoleccion(asistente);
                break;
            default:
                mision = new MisionRetorno(asistente);
                break;
        }

        mision.realizar();

        System.out.println("Resultado: " + mision.getResultado());
        System.out.println("Etapa: " + mision.getEtapa());

        System.out.println("Recursos finales:");
        System.out.println("  Combustible: " + asistente.getCombustible());
        System.out.println("  Energia: " + asistente.getEnergia());
        System.out.println("  Desgaste: " + asistente.getDesgaste());

        if (mision.getInforme() != null) {
            InformeMision informe = mision.getInforme();
            System.out.println("Informe:");
            System.out.println("  Nombre: " + informe.getNombreMision());
            System.out.println("  Resultado: " + informe.getResultado());
            System.out.println("  Combustible consumido: " + informe.getCombustibleConsumido());
            System.out.println("  Energia consumida: " + informe.getEnergiaConsumida());
            System.out.println("  Desgaste generado: " + informe.getDesgasteGenerado());
            System.out.println("  Combustible final: " + informe.getCombustibleFinal());
            System.out.println("  Energia final: " + informe.getEnergiaFinal());
            System.out.println("  Desgaste final: " + informe.getDesgasteFinal());
            System.out.println("  Observaciones: " + informe.getObservaciones());
        }

        System.out.println("Bitácora:");
        asistente.mostrarReporteBitacora();

        System.out.println("========================================");
        System.out.println();
    }
 }

    public static void escenarioB() {
    System.out.println("escenario b: la nave quiere hacer una misión pero se queda sin energia o nafta");

    Bitacora bitacora = new Bitacora();
    
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("exploradora");

    AsistenteDeComando asistente = new AsistenteDeComando(miNave, bitacora);

    // Dejo la nave sin recursos para provocar el rechazo
    miNave.consumir(miNave.getCombustible(), miNave.getEnergia(), 0);

    System.out.println("Recursos antes de la misión:");
    System.out.println("  Combustible: " + asistente.getCombustible());
    System.out.println("  Energia: " + asistente.getEnergia());
    System.out.println("  Desgaste: " + asistente.getDesgaste());

    Mision mision = new MisionIntercepcion(asistente);
    mision.realizar();

    System.out.println("Resultado final: " + mision.getResultado());
    System.out.println("Etapa final: " + mision.getEtapa());

    if (mision.getInforme() != null) {
        System.out.println("Observaciones: " + mision.getInforme().getObservaciones());
    }

    System.out.println("Recursos finales:");
    System.out.println("  Combustible: " + asistente.getCombustible());
    System.out.println("  Energia: " + asistente.getEnergia());
    System.out.println("  Desgaste: " + asistente.getDesgaste());

    System.out.println("Bitacora:");
    asistente.mostrarReporteBitacora();
}
}

