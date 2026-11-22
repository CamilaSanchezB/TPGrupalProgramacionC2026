package pruebas;

import controller.AsistenteDeComando;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.Coordenada;
import model.Mision.EtapaMision;
import model.Mision.InformeMision;
import model.Mision.MisionIntercepcion;
import model.Mision.MisionRecoleccion;
import model.Mision.MisionRetorno;
import model.Mision.ResultadoMision;
import model.Universo.AdaptadorTableroUniverso;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import model.destinoytablero.CantidadDeElementosSuperaCasillaException;
import model.destinoytablero.DimensionesDeTableroInconsistentesException;
import model.destinoytablero.Tablero;
import model.nave.Nave;
import model.nave.NaveFactory;

import java.util.ArrayList;

/**
 * Prueba de integración (vista hardcodeada): tablero de la cátedra + adaptador + misiones.
 * Cada chequeo imprime OK / FALLA y al final se muestra el resumen.
 * Nota: cada turno de travesía espera 800 ms, así que tarda unos segundos.
 */
public class PruebaMisiones {
    private static int ok = 0;
    private static int falla = 0;

    private static void verificar(boolean condicion, String descripcion) {
        if (condicion) { ok++; System.out.println("   [OK]    " + descripcion); }
        else           { falla++; System.out.println("   [FALLA] " + descripcion); }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" SIMULACIÓN INTEGRAL DE MISIONES (MVC)");
        System.out.println("=================================================\n");
        try {
            escenarioCompleto();
            escenarioRechazo();
            escenarioCasosBorde();
        } catch (DimensionesDeTableroInconsistentesException e) {
            System.err.println("Error en dimensiones de tablero: " + e.getMessage());
            falla++;
        } catch (CantidadDeElementosSuperaCasillaException e) {
            System.err.println("Error en cantidad de elementos: " + e.getMessage());
            falla++;
        }
        System.out.println("\n=================================================");
        System.out.println(" RESUMEN: " + ok + " verificaciones OK, " + falla + " con FALLA");
        System.out.println("=================================================");
    }

    // ---------------------------------------------------------------------
    // Escenario 1: M-01 -> M-02 -> M-03 seguidas sobre un tablero con obstáculos
    // ---------------------------------------------------------------------
    private static void escenarioCompleto() throws DimensionesDeTableroInconsistentesException,
            CantidadDeElementosSuperaCasillaException {
        System.out.println("##### ESCENARIO 1: tres misiones seguidas #####\n");

        // Los elementos nacen en (0,0): el tablero los distribuye al azar y el adaptador
        // les corrige la posición.
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<ElementoDelUniverso>();
        for (int i = 0; i < 8; i++) elementos.add(new Asteroide(new Coordenada(0, 0)));
        for (int i = 0; i < 3; i++) elementos.add(new EstacionDeCarga(new Coordenada(0, 0)));

        Tablero tablero = new Tablero(10, 10, elementos);
        tablero.obtenerObjetivo();                                   // el tablero elige el objetivo
        AdaptadorTableroUniverso universo = new AdaptadorTableroUniverso(tablero);

        Nave nave = new NaveFactory().crearNave("CARGUERO");
        Bitacora bitacora = new Bitacora();
        AsistenteDeComando asistente = new AsistenteDeComando(nave, bitacora);

        Coordenada base = universo.getPosicionNave();                // la nave arranca en el centro
        Coordenada objetivo = universo.getObjetivo();
        Coordenada punto = casillaLibre(universo, base, objetivo);   // segundo punto de recolección
        System.out.println("Base: " + base + " | Objetivo: " + objetivo + " | Punto de recolección: " + punto);
        System.out.println("Estado inicial | Comb: " + asistente.getCombustible()
                + " | Energía: " + asistente.getEnergia() + " | Desgaste: " + asistente.getDesgaste() + "\n");

        verificar(universo.contiene(base) && universo.contiene(objetivo) && universo.contiene(punto),
                "base, objetivo y punto están dentro del universo");
        verificar(!base.equals(objetivo) && !objetivo.equals(punto), "los puntos son distintos");

        // ----- M-01 -----
        System.out.println("--- M-01 Intercepción: base -> objetivo ---");
        MisionIntercepcion m1 = new MisionIntercepcion(asistente, universo, base, objetivo, 10, 10);
        InformeMision i1 = asistente.encomendarMision(m1);
        imprimirInforme(i1);
        verificarMisionCerrada(m1.getEtapa(), m1.getResultado(), i1, asistente, "M-01");
        if (nave.estaDestruida()) { avisoDestruida(); return; }
        verificar(i1.getResultado() == ResultadoMision.EXITOSA, "M-01 EXITOSA");

        // ----- M-02 -----
        System.out.println("--- M-02 Recolección: objetivo -> punto ---");
        int gemasAntes = nave.getGemas();
        MisionRecoleccion m2 = new MisionRecoleccion(asistente, universo, objetivo, punto);
        InformeMision i2 = asistente.encomendarMision(m2);
        imprimirInforme(i2);
        verificarMisionCerrada(m2.getEtapa(), m2.getResultado(), i2, asistente, "M-02");
        if (nave.estaDestruida()) { avisoDestruida(); return; }
        verificar(i2.getResultado() == ResultadoMision.EXITOSA, "M-02 EXITOSA");
        int ganadas = nave.getGemas() - gemasAntes;
        verificar(ganadas >= 20 && ganadas <= 100, "M-02 entregó entre 20 y 100 gemas (" + ganadas + ")");

        // ----- M-03 -----
        System.out.println("--- M-03 Retorno seguro: punto -> base ---");
        MisionRetorno m3 = new MisionRetorno(asistente, universo, punto, base);
        InformeMision i3 = asistente.encomendarMision(m3);
        imprimirInforme(i3);
        verificarMisionCerrada(m3.getEtapa(), m3.getResultado(), i3, asistente, "M-03");
        if (nave.estaDestruida()) { avisoDestruida(); return; }
        verificar(i3.getResultado() == ResultadoMision.EXITOSA, "M-03 EXITOSA");
        verificar(asistente.getCombustible() == 100 && asistente.getEnergia() == 100,
                "M-03 recargó combustible y energía al 100%");
        verificar(asistente.getDesgaste() == 0, "M-03 dejó el desgaste en 0");
        verificar(universo.getPosicionNave().equals(base), "la nave terminó en la base según el tablero");

        // ----- Historial e informes -----
        System.out.println("--- Historial ---");
        verificar(asistente.getHistorialMisiones().size() == 3, "el historial tiene las 3 misiones");
        verificar(i1.getEventos().size() > 0 && i2.getEventos().size() > 0 && i3.getEventos().size() > 0,
                "cada informe tiene eventos");
        int totalEventos = bitacora.getEventos().size();
        verificar(i1.getEventos().size() < totalEventos,
                "el informe de M-01 NO contiene los eventos de las otras misiones");
        verificar(ultimo(i1).startsWith("Mision cerrada: M-01"), "el informe de M-01 termina con su propio cierre");
        verificar(ultimo(i3).startsWith("Mision cerrada: M-03"), "el informe de M-03 termina con su propio cierre");
        int sumados = i1.getEventos().size() + i2.getEventos().size() + i3.getEventos().size();
        verificar(sumados == totalEventos, "los eventos de los 3 informes suman toda la bitácora (" + sumados + ")");

        int orden = 1;
        for (InformeMision inf : asistente.getHistorialMisiones()) {
            System.out.println("   " + orden++ + ". " + inf.getNombreMision() + " | " + inf.getResultado()
                    + " | Comb: " + inf.getCombustibleConsumido() + " | Energ: " + inf.getEnergiaConsumida()
                    + " | Desg: " + inf.getDesgasteGenerado());
        }
        System.out.println("\n--- Bitácora completa ---");
        asistente.mostrarReporteBitacora();
        System.out.println();
    }

    // ---------------------------------------------------------------------
    // Escenario 2: misión rechazada por recursos insuficientes (la nave no se toca)
    // ---------------------------------------------------------------------
    private static void escenarioRechazo() throws DimensionesDeTableroInconsistentesException,
            CantidadDeElementosSuperaCasillaException {
        System.out.println("##### ESCENARIO 2: misión rechazada por recursos insuficientes #####\n");

        Tablero tablero = new Tablero(10, 10, new ArrayList<ElementoDelUniverso>());
        tablero.obtenerObjetivo();
        AdaptadorTableroUniverso universo = new AdaptadorTableroUniverso(tablero);
        Nave nave = new NaveFactory().crearNave("CARGUERO");
        AsistenteDeComando asistente = new AsistenteDeComando(nave, new Bitacora());

        asistente.consumir(0, 55, 0);        // queda con 5 de energía y M-02 necesita 10
        int comb = asistente.getCombustible(), ener = asistente.getEnergia(), desg = asistente.getDesgaste();

        MisionRecoleccion m = new MisionRecoleccion(asistente, universo,
                universo.getPosicionNave(), universo.getObjetivo());
        InformeMision informe = asistente.encomendarMision(m);
        imprimirInforme(informe);

        verificar(m.getResultado() == ResultadoMision.RECHAZADA, "resultado RECHAZADA");
        verificar(m.getEtapa() == EtapaMision.CERRADA, "etapa CERRADA");
        verificar(informe.getObservaciones().startsWith("Recursos insuficientes"), "motivo: Recursos insuficientes");
        verificar(asistente.getCombustible() == comb && asistente.getEnergia() == ener
                && asistente.getDesgaste() == desg, "la nave no fue modificada");
        verificar(asistente.estaDisponible(), "el motor sigue Disponible");
        verificar(informe.getCombustibleConsumido() == 0 && informe.getEnergiaConsumida() == 0
                && informe.getDesgasteGenerado() == 0, "el balance del informe es 0");
        verificar(universo.getPosicionNave().equals(new Coordenada(5, 5)), "el tablero no movió la nave");
    }

    // ---------------------------------------------------------------------
    // Escenario 3: casos borde (destino fuera del tablero, realizar() repetido, motor ocupado)
    // ---------------------------------------------------------------------
    private static void escenarioCasosBorde() throws DimensionesDeTableroInconsistentesException,
            CantidadDeElementosSuperaCasillaException {
        System.out.println("##### ESCENARIO 3: casos borde #####\n");

        Tablero tablero = new Tablero(10, 10, new ArrayList<ElementoDelUniverso>());
        tablero.obtenerObjetivo();
        AdaptadorTableroUniverso universo = new AdaptadorTableroUniverso(tablero);
        Nave nave = new NaveFactory().crearNave("CARGUERO");
        AsistenteDeComando asistente = new AsistenteDeComando(nave, new Bitacora());
        int comb = asistente.getCombustible(), ener = asistente.getEnergia(), desg = asistente.getDesgaste();

        // a) destino fuera del tablero: se rechaza (antes lanzaba IllegalArgumentException)
        MisionRetorno fuera = new MisionRetorno(asistente, universo, universo.getPosicionNave(),
                new Coordenada(50, 50));
        InformeMision inf = asistente.encomendarMision(fuera);
        verificar(fuera.getResultado() == ResultadoMision.RECHAZADA, "destino fuera del tablero -> RECHAZADA");
        verificar(fuera.getEtapa() == EtapaMision.CERRADA, "destino fuera del tablero -> etapa CERRADA");
        verificar(inf.getObservaciones().startsWith("Coordenadas"), "motivo: coordenadas fuera del universo");
        verificar(asistente.getCombustible() == comb && asistente.getEnergia() == ener
                && asistente.getDesgaste() == desg, "la nave no fue modificada");

        // b) una misión no se puede realizar dos veces y la 2da llamada no pisa su informe
        int eventosInforme = inf.getEventos().size();
        boolean lanzo = false;
        try { fuera.realizar(); } catch (IllegalStateException e) { lanzo = true; }
        verificar(lanzo, "realizar() dos veces -> IllegalStateException");
        verificar(fuera.getInforme().getEventos().size() == eventosInforme, "el informe no cambió");

        // c) motor ocupado (Preparando): se rechaza y NO se reinicia el motor
        nave.saltarWarp();   // Disponible -> Preparando salto
        MisionRetorno ocupada = new MisionRetorno(asistente, universo, universo.getPosicionNave(),
                new Coordenada(0, 0));
        asistente.encomendarMision(ocupada);
        verificar(ocupada.getResultado() == ResultadoMision.RECHAZADA, "motor ocupado -> RECHAZADA");
        verificar(!asistente.estaDisponible(), "la rechazada no reinició el motor");
        System.out.println();
    }

    // ---------------------------------------------------------------------
    // Auxiliares
    // ---------------------------------------------------------------------

    /** Chequeos comunes a toda misión que se encomendó (exitosa o no). */
    private static void verificarMisionCerrada(EtapaMision etapa, ResultadoMision resultado,
                                               InformeMision informe, AsistenteDeComando asistente, String nombre) {
        verificar(etapa == EtapaMision.CERRADA, nombre + " quedó CERRADA");
        verificar(resultado != null && resultado != ResultadoMision.RECHAZADA, nombre + " no fue rechazada");
        verificar(informe != null && informe.getResultado() == resultado, nombre + " tiene informe coherente");
        verificar(asistente.estaDestruida() || asistente.estaDisponible(), nombre + " dejó el motor Disponible");
        verificar(informe.getDesgasteGenerado() >= 0, nombre + " desgaste generado >= 0");
    }

    /** Primera casilla libre a distancia >= 4 de los puntos que se quieren evitar. */
    private static Coordenada casillaLibre(AdaptadorTableroUniverso universo, Coordenada a, Coordenada b) {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                Coordenada c = new Coordenada(i, j);
                if (universo.elementosEn(c).isEmpty()
                        && c.distanciaManhattan(a) >= 4 && c.distanciaManhattan(b) >= 4)
                    return c;
            }
        throw new IllegalStateException("No hay casilla libre para el punto de recolección");
    }

    private static String ultimo(InformeMision informe) {
        RegitroBitacora r = informe.getEventos().get(informe.getEventos().size() - 1);
        return r.getDescripcion();
    }

    private static void avisoDestruida() {
        System.out.println("   [AVISO] La nave fue destruida en el camino (asteroides o desgaste): es un "
                + "resultado válido del juego, se corta el escenario.\n");
    }

    private static void imprimirInforme(InformeMision informe) {
        System.out.println("-> Misión: " + informe.getNombreMision() + " | Resultado: " + informe.getResultado());
        System.out.println("-> Balance del viaje | Comb: " + informe.getCombustibleConsumido()
                + " | Energ: " + informe.getEnergiaConsumida() + " | Desgaste: " + informe.getDesgasteGenerado());
        System.out.println("-> Estado final | Comb: " + informe.getCombustibleFinal()
                + " | Energ: " + informe.getEnergiaFinal() + " | Desg: " + informe.getDesgasteFinal());
        System.out.println("-> Observaciones: " + (informe.getObservaciones().isEmpty() ? "Ninguna" : informe.getObservaciones()));
        System.out.println();
    }
}