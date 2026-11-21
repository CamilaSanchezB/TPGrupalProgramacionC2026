package pruebas;

import controller.AsistenteDeComando;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.Coordenada;
import model.Mision.InformeMision;
import model.Mision.MisionRetorno;
import model.Mision.ResultadoMision;
import model.Universo.NaveEnemiga;
import model.Universo.RadarLargoAlcance;
import model.nave.Nave;
import model.nave.NaveFactory;

import java.util.List;

/**
 * Naves enemigas como elementos del tablero: la batalla (Double Dispatch) con sus consecuencias,
 * su integración con el asistente y con una misión completa.
 * Los escenarios de misión esperan 1 s por turno, así que tarda unos segundos.
 */
public class PruebaNavesEnemigas {
    private static final NaveFactory fabrica = new NaveFactory();
    private static final Coordenada POS = new Coordenada(1, 1);
    private static int fallos = 0;

    private enum Esperado { GANA, PIERDE, PACTO }

    public static void main(String[] args) {
        tablaDeBatallasConConsecuencias();
        enemigaDerrotadaNoPeleaDeNuevo();
        nuncaEsObstaculo();
        conLaBitacoraDelAsistente();
        errores();
        misionConEnemigaQueGana();
        misionConEnemigaQuePierde();
        misionConPactoDePaz();
        System.out.println(fallos == 0 ? "\nTODOS LOS CASOS OK" : "\nFALLOS: " + fallos);
    }

    // ---------- Unitarias ----------

    private static void tablaDeBatallasConConsecuencias() {
        System.out.println("-- las 9 combinaciones y sus consecuencias");
        // propia vs enemiga -> qué le pasa a la propia (tabla del enunciado)
        probar("EXPLORADORA", "EXPLORADORA", Esperado.PACTO);
        probar("EXPLORADORA", "CARGUERO",    Esperado.GANA);
        probar("EXPLORADORA", "COMBATE",     Esperado.PIERDE);
        probar("CARGUERO",    "EXPLORADORA", Esperado.PIERDE);
        probar("CARGUERO",    "CARGUERO",    Esperado.PACTO);
        probar("CARGUERO",    "COMBATE",     Esperado.GANA);
        probar("COMBATE",     "EXPLORADORA", Esperado.GANA);
        probar("COMBATE",     "CARGUERO",    Esperado.PIERDE);
        probar("COMBATE",     "COMBATE",     Esperado.PACTO);
    }

    private static void probar(String tipoPropia, String tipoEnemiga, Esperado esperado) {
        Nave propia = fabrica.crearNave(tipoPropia);
        NaveEnemiga enemiga = new NaveEnemiga(fabrica.crearNave(tipoEnemiga), POS);
        int comb = propia.getCombustible(), ener = propia.getEnergia(), desg = propia.getDesgaste();

        String msg = enemiga.interactuar(propia);

        boolean propiaOk = (esperado == Esperado.PIERDE) == propia.estaDestruida();
        boolean enemigaOk = (esperado == Esperado.GANA) == enemiga.estaDerrotada();
        boolean recursosOk = propia.getCombustible() == comb && propia.getEnergia() == ener
                && propia.getDesgaste() == desg;          // la batalla no toca los recursos
        boolean mensajeOk = msg != null && !msg.isEmpty();
        verificar(tipoPropia + " vs " + tipoEnemiga + " -> " + esperado,
                propiaOk && enemigaOk && recursosOk && mensajeOk);
    }

    private static void enemigaDerrotadaNoPeleaDeNuevo() {
        System.out.println("-- una enemiga derrotada ya no pelea");
        NaveEnemiga enemiga = new NaveEnemiga(fabrica.crearNave("CARGUERO"), POS);
        enemiga.interactuar(fabrica.crearNave("EXPLORADORA"));        // la derrota
        verificar("quedó derrotada", enemiga.estaDerrotada());

        Nave otra = fabrica.crearNave("COMBATE");                      // contra Carguero perdería
        String msg = enemiga.interactuar(otra);
        verificar("la derrotada no destruye a quien pasa por encima", !otra.estaDestruida());
        verificar("el mensaje habla de restos", msg.contains("Restos"));
    }

    private static void nuncaEsObstaculo() {
        System.out.println("-- no es un obstáculo para la ruta");
        NaveEnemiga enemiga = new NaveEnemiga(fabrica.crearNave("COMBATE"), new Coordenada(1, 0));
        verificar("esObstaculo() == false", !enemiga.esObstaculo());
        GrillaDePrueba g = new GrillaDePrueba(3, 1);
        g.agregar(enemiga);
        List<Coordenada> ruta = new RadarLargoAlcance().calcularRuta(g, new Coordenada(0, 0), new Coordenada(2, 0));
        verificar("el radar largo traza la ruta por la casilla de la enemiga", ruta.size() == 3);
    }

    private static void conLaBitacoraDelAsistente() {
        System.out.println("-- con AsistenteDeComando");
        Bitacora bitacora = new Bitacora();
        Nave propia = fabrica.crearNave("EXPLORADORA");
        AsistenteDeComando asistente = new AsistenteDeComando(propia, bitacora);
        asistente.interactuarCon(new NaveEnemiga(fabrica.crearNave("COMBATE"), POS));
        verificar("la derrota destruye la nave y el asistente lo refleja", asistente.estaDestruida());
        verificar("la batalla queda en la bitácora", bitacoraContiene(bitacora, "nave enemiga Combate"));
    }

    private static void errores() {
        System.out.println("-- errores");
        Nave n = fabrica.crearNave("EXPLORADORA");
        NaveEnemiga e = new NaveEnemiga(fabrica.crearNave("CARGUERO"), POS);
        verificarExcepcion("nave null", () -> new NaveEnemiga(null, POS));
        verificarExcepcion("posición null", () -> new NaveEnemiga(n, null));
        verificarExcepcion("setPosicion(null)", () -> e.setPosicion(null));
        verificarExcepcion("interactuar(null)", () -> e.interactuar(null));
        e.setPosicion(new Coordenada(4, 4));
        verificar("setPosicion cambia la posición", e.getPosicion().equals(new Coordenada(4, 4)));
        verificar("toString describe la nave", e.toString().contains("Carguero"));
    }

    // ---------- Misiones completas (escenario determinista) ----------

    private static void misionConEnemigaQueGana() {
        System.out.println("-- misión: la enemiga destruye a la nave propia");
        Escenario s = escenario("EXPLORADORA", "COMBATE");
        verificar("la misión falló", s.informe.getResultado() == ResultadoMision.FALLIDA);
        verificar("la nave quedó destruida", s.asistente.estaDestruida());
        verificar("no llegó al destino", !s.destinoAlcanzado);
        verificar("la bitácora cuenta la derrota", bitacoraContiene(s.bitacora, "fue destruida por una nave enemiga"));
    }

    private static void misionConEnemigaQuePierde() {
        System.out.println("-- misión: la nave propia vence a la enemiga y sigue");
        Escenario s = escenario("EXPLORADORA", "CARGUERO");
        verificar("la misión fue exitosa", s.informe.getResultado() == ResultadoMision.EXITOSA);
        verificar("la nave llegó viva", !s.asistente.estaDestruida() && s.destinoAlcanzado);
        verificar("la enemiga quedó derrotada", s.enemiga.estaDerrotada());
        verificar("la bitácora cuenta la victoria", bitacoraContiene(s.bitacora, "venció a una nave enemiga"));
    }

    private static void misionConPactoDePaz() {
        System.out.println("-- misión: pacto de paz");
        Escenario s = escenario("COMBATE", "COMBATE");
        verificar("la misión fue exitosa", s.informe.getResultado() == ResultadoMision.EXITOSA);
        verificar("la enemiga sigue viva", !s.enemiga.estaDerrotada());
        verificar("la bitácora cuenta el pacto", bitacoraContiene(s.bitacora, "Pacto de paz"));
    }

    /** Pasillo de 1 x 6: la nave va de (0,0) a (5,0) y la enemiga está en medio, en (3,0). */
    private static Escenario escenario(String tipoPropia, String tipoEnemiga) {
        Escenario s = new Escenario();
        Coordenada origen = new Coordenada(0, 0), destino = new Coordenada(5, 0);
        GrillaDinamicaDePrueba universo = new GrillaDinamicaDePrueba(6, 1, origen, destino);
        s.enemiga = new NaveEnemiga(fabrica.crearNave(tipoEnemiga), new Coordenada(3, 0));
        universo.agregar(s.enemiga);

        s.bitacora = new Bitacora();
        s.asistente = new AsistenteDeComando(fabrica.crearNave(tipoPropia), s.bitacora);
        MisionRetorno mision = new MisionRetorno(s.asistente, universo, origen, destino);
        s.informe = s.asistente.encomendarMision(mision);
        s.destinoAlcanzado = mision.getNavegador() != null && mision.getNavegador().haLlegado();
        return s;
    }

    private static class Escenario {
        NaveEnemiga enemiga;
        Bitacora bitacora;
        AsistenteDeComando asistente;
        InformeMision informe;
        boolean destinoAlcanzado;
    }

    // ---------- Auxiliares ----------

    private static boolean bitacoraContiene(Bitacora b, String texto) {
        for (RegitroBitacora e : b.getEventos())
            if (e.getDescripcion().contains(texto)) return true;
        return false;
    }

    private static void verificar(String descripcion, boolean ok) {
        if (!ok) fallos++;
        System.out.println((ok ? "OK    " : "FALLO ") + descripcion);
    }

    private static void verificarExcepcion(String descripcion, Runnable accion) {
        try {
            accion.run();
            verificar(descripcion + " (no lanzó)", false);
        } catch (IllegalArgumentException ex) {
            verificar(descripcion + " -> IllegalArgumentException", true);
        }
    }
}