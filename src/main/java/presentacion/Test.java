package presentacion;

import datos.OperacionRechazadaException;
import datos.ClasesBitacora.Bitacora;
import datos.Mision.InformeMision;
import datos.Mision.Mision;
import datos.Mision.MisionIntercepcion;
import datos.Mision.MisionRecoleccion;
import datos.Mision.MisionRetorno;
import datos.nave.Nave;
import datos.nave.NaveFactory;
import datos.tripulantes.Consejero;
import datos.tripulantes.Terricola;
import datos.tripulantes.Tripulante;
import datos.tripulantes.Vulcano;
import negocio.AsistenteDeComando;
import negocio.LiquidadorHaberes;

public class Test {
    public static void ejecutarEscenarioA(){
        escenarioA();
    }

     public static void ejecutarEscenarioB(){
        escenarioB();
    }

    public static void ejecutarEscenarioC(){
        escenarioC();
    }

    public static void ejecutarEscenarioD(){
        escenarioD();
    }

    public static void ejecutarLiquidacion(){
        testLiquidacionHaberes();
    }
     
    
    public static void escenarioD() {
        System.out.println("ESCENARIO D:");
        Bitacora miBitacora = new Bitacora();
        NaveFactory factory = new NaveFactory();
        Nave pruebaNave = factory.crearNave("exploradora");
        Nave pruebaNave2= factory.crearNave("combate");
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
    
    public static void escenarioB() {
    System.out.println("escenario b: la nave quiere hacer una misión pero se queda sin pila o nafta");

    Bitacora bitacora = new Bitacora();
    
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("exploradora");

    AsistenteDeComando asistente = new AsistenteDeComando(miNave, bitacora);

    // aca consume todos los recursos q tiene usando los gets
    miNave.consumir(miNave.getCombustible(), miNave.getEnergia(), 0);

    System.out.println("recursos antes");
    System.out.println("  combustible: " + asistente.getCombustible());
    System.out.println("  energia: " + asistente.getEnergia());
    System.out.println("  desgaste: " + asistente.getDesgaste());

    Mision mision = new MisionIntercepcion(asistente);
    mision.realizar();

    System.out.println("resultado final: " + mision.getResultado());
    System.out.println("etapa final: " + mision.getEtapa());

    if (mision.getInforme() != null) {
        System.out.println("observaciones: " + mision.getInforme().getObservaciones());
    }

    System.out.println("recursos finales:");
    System.out.println("  combustible: " + asistente.getCombustible());
    System.out.println("  energia: " + asistente.getEnergia());
    System.out.println("  desgaste: " + asistente.getDesgaste());

    System.out.println("bitacora:");
    asistente.mostrarReporteBitacora();
}

    public static void escenarioA() {
    System.out.println(" probamos las 3 misiones");

    Bitacora bitacora = new Bitacora();
    NaveFactory factory = new NaveFactory();
    Nave miNave = factory.crearNave("combate");
    AsistenteDeComando asistente = new AsistenteDeComando(miNave, bitacora);

    for (int i = 1; i <= 3; i++) {
        System.out.println("========================================");
        System.out.println("MISSION " + i);


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

        System.out.println("resultado: " + mision.getResultado());
        System.out.println("etapa: " + mision.getEtapa());

        System.out.println("recursos finales:");
        System.out.println("  combustible: " + asistente.getCombustible());
        System.out.println("  energia: " + asistente.getEnergia());
        System.out.println("  desgaste: " + asistente.getDesgaste());

        if (mision.getInforme() != null) {
            InformeMision informe = mision.getInforme();
            System.out.println("Informe:");
            System.out.println("  nombre: " + informe.getNombreMision());
            System.out.println("  resultado: " + informe.getResultado());
            System.out.println("  combustible consumido: " + informe.getCombustibleConsumido());
            System.out.println("  energia consumida: " + informe.getEnergiaConsumida());
            System.out.println("  desgaste generado: " + informe.getDesgasteGenerado());
            System.out.println("  combustible final: " + informe.getCombustibleFinal());
            System.out.println("  energia final: " + informe.getEnergiaFinal());
            System.out.println("  desgaste final: " + informe.getDesgasteFinal());
            System.out.println("  observaciones: " + informe.getObservaciones());
        }

        System.out.println("bitacora:");
        asistente.mostrarReporteBitacora();
    }
 }

    public static void testLiquidacionHaberes(){
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("combate");
        LiquidadorHaberes liquidador = new LiquidadorHaberes();
        // la nave se crea con la tripulacion minima, asi que hay al menos un capitan
        Tripulante capitan = miNave.getTripulantesPorCargo("CAPITAN").get(0);
        capitan.setAntiguedad(10);
        capitan.setNombre("Test capitan");
        capitan = new Vulcano(capitan);
        miNave.editarTripulante("Test capitan", capitan);
        miNave.asignarTripulante(new Terricola(new Consejero("Test consejero", 5,10)));
        liquidador.imprimirLiquidacion(miNave);

    }

}
