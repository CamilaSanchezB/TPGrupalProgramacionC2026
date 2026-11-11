package pruebas;

import controller.AsistenteDeComando;
import model.destinoytablero.CantidadDeElementosSuperaCasillaException;
import model.destinoytablero.DimensionesDeTableroInconsistentesException;
import model.destinoytablero.Tablero;
import model.ClasesBitacora.Bitacora;
import model.Coordenada;
import model.Mision.InformeMision;
import model.Mision.MisionIntercepcion;
import model.Mision.MisionRecoleccion;
import model.Mision.MisionRetorno;
import model.Universo.AdaptadorTableroUniverso;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import model.nave.Nave;
import model.nave.NaveFactory;

import java.util.ArrayList;

/**
 * Prueba de integración que actúa como Vista Hardcodeada.
 * Invoca las operaciones a través del controlador AsistenteDeComando.
 */
public class PruebaMisiones {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" INICIANDO SIMULACIÓN INTEGRAL DE MISIONES (MVC)");
        System.out.println("=================================================\n");

        try {
            // 1. MODELO: Configuración del Tablero de la cátedra y Adaptador
            ArrayList<ElementoDelUniverso> elementosIniciales = new ArrayList<ElementoDelUniverso>();
            elementosIniciales.add(new Asteroide(new Coordenada(2, 3)));
            elementosIniciales.add(new EstacionDeCarga(new Coordenada(4, 4)));

            Tablero tableroCatedra = new Tablero(10, 10, elementosIniciales);
            AdaptadorTableroUniverso universoAdaptado = new AdaptadorTableroUniverso(tableroCatedra);

            // 2. MODELO: Creación de la Nave utilizando NaveFactory (1 de los 3 tipos disponibles)
            NaveFactory naveFactory = new NaveFactory();
            Nave nave = naveFactory.crearNave("CARGUERO");
            Bitacora bitacora = new Bitacora();

            // 3. CONTROLADOR: AsistenteDeComando es la fachada y controlador del sistema
            AsistenteDeComando asistente = new AsistenteDeComando(nave, bitacora);

            System.out.println("-> Nave creada mediante NaveFactory.");
            System.out.println("-> Controlador AsistenteDeComando listo.");
            System.out.println("-> Estado inicial | Combustible: " + asistente.getCombustible()
                    + " | Energía: " + asistente.getEnergia()
                    + " | Desgaste: " + asistente.getDesgaste() + "\n");

            // -----------------------------------------------------------------
            // VISTA HARDCODEADA: EJECUCIÓN DE MISIÓN M-01 (INTERCEPCIÓN)
            // -----------------------------------------------------------------
            System.out.println("--- VISTA: Solicitando Misión M-01 (Intercepción) ---");
            Coordenada origenM1 = new Coordenada(0, 0);
            Coordenada destinoM1 = new Coordenada(2, 2);
            MisionIntercepcion m1 = new MisionIntercepcion(asistente, universoAdaptado, origenM1, destinoM1, 10, 10);

            // El Controlador ejecuta y registra internamente el informe en su GestorHistorial
            InformeMision informeM1 = asistente.encomendarMision(m1);
            imprimirResumenInforme(informeM1);

            // -----------------------------------------------------------------
            // VISTA HARDCODEADA: EJECUCIÓN DE MISIÓN M-02 (RECOLECCIÓN)
            // -----------------------------------------------------------------
            System.out.println("--- VISTA: Solicitando Misión M-02 (Recolección) ---");
            Coordenada origenM2 = destinoM1;
            Coordenada destinoM2 = new Coordenada(5, 5);
            MisionRecoleccion m2 = new MisionRecoleccion(asistente, universoAdaptado, origenM2, destinoM2);

            InformeMision informeM2 = asistente.encomendarMision(m2);
            imprimirResumenInforme(informeM2);

            // -----------------------------------------------------------------
            // VISTA HARDCODEADA: EJECUCIÓN DE MISIÓN M-03 (RETORNO SEGURO)
            // -----------------------------------------------------------------
            System.out.println("--- VISTA: Solicitando Misión M-03 (Retorno Seguro) ---");
            Coordenada origenM3 = destinoM2;
            Coordenada destinoBase = new Coordenada(0, 0);
            MisionRetorno m3 = new MisionRetorno(asistente, universoAdaptado, origenM3, destinoBase);

            InformeMision informeM3 = asistente.encomendarMision(m3);
            imprimirResumenInforme(informeM3);

            // -----------------------------------------------------------------
            // VISTA: MOSTRAR RESULTADOS DESDE EL CONTROLADOR
            // -----------------------------------------------------------------
            System.out.println("=================================================");
            System.out.println(" VISTA: HISTORIAL OBTENIDO DESDE EL CONTROLADOR");
            System.out.println("=================================================");
            System.out.println("Total misiones en el Gestor del Asistente: " + asistente.getHistorialMisiones().size());

            int orden = 1;
            for (InformeMision inf : asistente.getHistorialMisiones()) {
                System.out.println(orden + ". " + inf.getNombreMision()
                        + " | Resultado: " + inf.getResultado()
                        + " | Comb. Consumido: " + inf.getCombustibleConsumido()
                        + " | Desgaste Generado: " + inf.getDesgasteGenerado());
                orden++;
            }

            System.out.println("\n=================================================");
            System.out.println(" VISTA: BITÁCORA COMPLETA ACUMULADA");
            System.out.println("=================================================");
            asistente.mostrarReporteBitacora();

        } catch (DimensionesDeTableroInconsistentesException e) {
            System.err.println("Error en dimensiones de tablero: " + e.getMessage());
        } catch (CantidadDeElementosSuperaCasillaException e) {
            System.err.println("Error en cantidad de elementos: " + e.getMessage());
        }
    }

    private static void imprimirResumenInforme(InformeMision informe) {
        System.out.println("-> Misión: " + informe.getNombreMision());
        System.out.println("-> Resultado: " + informe.getResultado());
        System.out.println("-> Balance Recursos | Comb. Consumido: " + informe.getCombustibleConsumido()
                + " | Energ. Consumida: " + informe.getEnergiaConsumida()
                + " | Desgaste Generado: " + informe.getDesgasteGenerado());
        System.out.println("-> Estado Final Nave | Comb: " + informe.getCombustibleFinal()
                + " | Energ: " + informe.getEnergiaFinal()
                + " | Desg: " + informe.getDesgasteFinal());
        System.out.println("-> Observaciones: " + (informe.getObservaciones().isEmpty() ? "Ninguna" : informe.getObservaciones()));
        System.out.println("-------------------------------------------------\n");
    }
}