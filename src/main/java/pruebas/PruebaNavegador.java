package pruebas;

import controller.AsistenteDeComando;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.Coordenada;
import model.nave.Nave;
import model.nave.NaveFactory;
import model.Universo.Asteroide;
import model.Universo.EstacionDeCarga;
import model.Universo.Navegador;
import model.Universo.ResultadoAvance;

public class PruebaNavegador {
    private static final NaveFactory fabrica = new NaveFactory();
    private static int fallos = 0;

    // Cada escenario arma su propia nave, bitácora y navegador
    private static Nave nave;
    private static Bitacora bitacora;
    private static Navegador nav;

    public static void main(String[] args) {
        secuenciaWarpYEnfriamiento();
        pocoRecorridoNoUsaWarp();
        estacionEnElCaminoNoSeSaltea();
        recursosJustosParaWarp();
        obstaculoNuevoReplanifica();
        sinRutaDuranteElViaje();
        viajeImposible();
        errores();
        System.out.println(fallos == 0 ? "\nTODOS LOS CASOS OK" : "\nFALLOS: " + fallos);
    }

    private static Coordenada c(int x, int y) { return new Coordenada(x, y); }

    private static void preparar(GrillaDePrueba g, Coordenada origen) {
        nave = fabrica.crearNave("EXPLORADORA");   // combustible 60, energía 80, desgaste 0
        bitacora = new Bitacora();
        nav = new Navegador(new AsistenteDeComando(nave, bitacora), g, origen);
    }

    // ---------- Escenarios ----------

    private static void secuenciaWarpYEnfriamiento() {
        System.out.println("-- warp y enfriamiento");
        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        verificar("inicia viaje en pista libre", nav.iniciarViaje(c(9, 0)));

        esperar(ResultadoAvance.SALTO_WARP, c(3, 0));
        verificar("tras el warp el motor no está disponible", !nave.estaDisponible());
        esperar(ResultadoAvance.SALTO, c(4, 0));
        verificar("durante el enfriamiento sigue sin estar disponible", !nave.estaDisponible());
        esperar(ResultadoAvance.SALTO, c(5, 0));
        verificar("a los 2 turnos el motor vuelve a Disponible", nave.estaDisponible());
        esperar(ResultadoAvance.SALTO_WARP, c(8, 0));
        esperar(ResultadoAvance.SALTO, c(9, 0));
        verificar("llegó al destino", nav.haLlegado());
        verificar("consumo: 2 warps + 3 saltos = 11 de combustible", nave.getCombustible() == 60 - 11);
        verificar("consumo: 2 warps = 4 de energía", nave.getEnergia() == 80 - 4);
        verificar("consumo: 5 saltos = 5 de desgaste", nave.getDesgaste() == 5);
        verificar("la nave llegó viva", !nave.estaDestruida());
        verificar("bitácora registra el warp", bitacoraContiene("Salto warp a (3, 0)"));
        verificar("bitácora registra el salto simple", bitacoraContiene("Salto a (4, 0)"));
    }

    private static void pocoRecorridoNoUsaWarp() {
        System.out.println("-- recorrido corto");
        preparar(new GrillaDePrueba(3, 1), c(0, 0));
        nav.iniciarViaje(c(2, 0));
        esperar(ResultadoAvance.SALTO, c(1, 0));
        esperar(ResultadoAvance.SALTO, c(2, 0));
        verificar("llegó sin usar warp", nav.haLlegado() && nave.estaDisponible());
    }

    private static void estacionEnElCaminoNoSeSaltea() {
        System.out.println("-- estación en el camino");
        GrillaDePrueba g = new GrillaDePrueba(10, 1);
        g.agregar(new EstacionDeCarga(c(2, 0)));
        preparar(g, c(0, 0));
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO, c(1, 0));        // la estación está dentro del alcance del warp
        esperar(ResultadoAvance.SALTO, c(2, 0));
        esperar(ResultadoAvance.SALTO_WARP, c(5, 0));   // ya la dejó atrás: puede usar warp
    }

    private static void recursosJustosParaWarp() {
        System.out.println("-- recursos en el límite del warp");
        // combustible 5 > costo 4: puede, y sobrevive con 1
        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        nave.consumir(55, 0, 0);
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO_WARP, c(3, 0));
        verificar("warp con combustible 5: la nave sobrevive con 1", nave.getCombustible() == 1 && !nave.estaDestruida());

        // combustible 4 == costo: el warp la dejaría en 0 (destruida) -> no lo usa
        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        nave.consumir(56, 0, 0);
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO, c(1, 0));

        // energía 2 == costo: tampoco
        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        nave.consumir(0, 78, 0);
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO, c(1, 0));

        // desgaste 98 + 1 = 99: puede; desgaste 99 + 1 = 100: no
        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        nave.consumir(0, 0, 98);
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO_WARP, c(3, 0));

        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        nave.consumir(0, 0, 99);
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO, c(1, 0));
    }

    private static void obstaculoNuevoReplanifica() {
        System.out.println("-- obstáculo que aparece después de calcular la ruta");
        GrillaDePrueba g = new GrillaDePrueba(5, 2);
        preparar(g, c(0, 0));
        nav.iniciarViaje(c(4, 0));
        g.agregar(new Asteroide(c(1, 0)));              // aparece sobre la ruta ya calculada

        boolean pisoElAsteroide = false;
        int turnos = 0;
        while (!nav.haLlegado() && turnos < 20) {
            nav.avanzarUnTurno();
            turnos++;
            if (nav.getPosicion().equals(c(1, 0))) pisoElAsteroide = true;
        }
        verificar("replanificó y llegó al destino", nav.haLlegado());
        verificar("nunca pasó por el asteroide", !pisoElAsteroide);
        verificar("la bitácora registra el recálculo", bitacoraContiene("Ruta recalculada"));
    }

    private static void sinRutaDuranteElViaje() {
        System.out.println("-- el camino se cierra durante el viaje");
        GrillaDePrueba g = new GrillaDePrueba(3, 1);
        preparar(g, c(0, 0));
        nav.iniciarViaje(c(2, 0));
        g.agregar(new Asteroide(c(1, 0)));
        int comb = nave.getCombustible(), ener = nave.getEnergia(), desg = nave.getDesgaste();

        ResultadoAvance r = nav.avanzarUnTurno();
        verificar("devuelve SIN_RUTA", r == ResultadoAvance.SIN_RUTA);
        verificar("no se movió", nav.getPosicion().equals(c(0, 0)));
        verificar("no consumió recursos",
                nave.getCombustible() == comb && nave.getEnergia() == ener && nave.getDesgaste() == desg);
        verificar("la bitácora registra el motivo", bitacoraContiene("Sin ruta desde"));
    }

    private static void viajeImposible() {
        System.out.println("-- viaje imposible desde el inicio");
        GrillaDePrueba g = new GrillaDePrueba(3, 3);
        for (int y = 0; y < 3; y++) g.agregar(new Asteroide(c(1, y)));
        preparar(g, c(0, 1));
        verificar("iniciarViaje devuelve false sin camino", !nav.iniciarViaje(c(2, 1)));
        verificarExcepcion("y no hay viaje en curso", IllegalStateException.class, () -> nav.avanzarUnTurno());
    }

    private static void errores() {
        System.out.println("-- errores");
        GrillaDePrueba g = new GrillaDePrueba(5, 5);
        AsistenteDeComando a = new AsistenteDeComando(fabrica.crearNave("EXPLORADORA"), new Bitacora());

        verificarExcepcion("asistente null", IllegalArgumentException.class, () -> new Navegador(null, g, c(0, 0)));
        verificarExcepcion("universo null", IllegalArgumentException.class, () -> new Navegador(a, null, c(0, 0)));
        verificarExcepcion("origen null", IllegalArgumentException.class, () -> new Navegador(a, g, null));
        verificarExcepcion("origen fuera del universo", IllegalArgumentException.class, () -> new Navegador(a, g, c(9, 9)));

        preparar(g, c(0, 0));
        verificarExcepcion("destino null", IllegalArgumentException.class, () -> nav.iniciarViaje(null));
        verificarExcepcion("destino fuera del universo", IllegalArgumentException.class, () -> nav.iniciarViaje(c(9, 9)));
        verificarExcepcion("avanzar sin viaje en curso", IllegalStateException.class, () -> nav.avanzarUnTurno());

        preparar(g, c(2, 2));
        nav.iniciarViaje(c(2, 2));
        verificar("origen == destino: ya llegó", nav.haLlegado());
        verificarExcepcion("avanzar estando ya en el destino", IllegalStateException.class, () -> nav.avanzarUnTurno());

        preparar(g, c(0, 0));
        nav.iniciarViaje(c(4, 4));
        nave.destruir();
        verificarExcepcion("avanzar con la nave destruida", IllegalStateException.class, () -> nav.avanzarUnTurno());
    }

    // ---------- auxiliares ----------

    private static void esperar(ResultadoAvance esperado, Coordenada posEsperada) {
        ResultadoAvance r = nav.avanzarUnTurno();
        verificar(esperado + " -> " + posEsperada + " (obtuvo " + r + " en " + nav.getPosicion() + ")",
                r == esperado && nav.getPosicion().equals(posEsperada));
    }

    private static boolean bitacoraContiene(String texto) {
        for (RegitroBitacora e : bitacora.getEventos())
            if (e.getDescripcion().contains(texto)) return true;
        return false;
    }

    private static void verificar(String descripcion, boolean ok) {
        if (!ok) fallos++;
        System.out.println((ok ? "OK    " : "FALLO ") + descripcion);
    }

    private static void verificarExcepcion(String descripcion, Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
            verificar(descripcion + " (no lanzó)", false);
        } catch (RuntimeException ex) {
            verificar(descripcion + " -> " + tipo.getSimpleName(), tipo.isInstance(ex));
        }
    }
}