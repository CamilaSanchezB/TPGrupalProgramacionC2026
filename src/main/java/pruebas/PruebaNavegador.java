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

/**
 * Prueba del Navegador con el motor warp nuevo (cada llamada a saltar/saltarWarp es un turno):
 *  - saltar():     solo con motor Disponible; avanza 1 casillero y cuesta 2 de combustible.
 *  - saltarWarp(): secuencia de 3 turnos (preparar, preparar, saltar). Recién el tercero avanza
 *                  2 casilleros y cuesta 5 de energía; después hay 1 turno de enfriamiento (+3 de desgaste).
 *  - El Navegador usa el warp solo para esquivar un obstáculo en la siguiente casilla (Anexo).
 * Requiere el EnWarp corregido (si la condición de energía está invertida, el warp nunca aterriza).
 */
public class PruebaNavegador {
    private static final NaveFactory fabrica = new NaveFactory();
    private static int fallos = 0;

    // Cada escenario arma su propia nave, bitácora y navegador
    private static Nave nave;
    private static Bitacora bitacora;
    private static Navegador nav;

    public static void main(String[] args) {
        saltoNormalEnPistaLibre();
        estacionNoDisparaWarp();
        warpEsquivaAsteroide();
        aterrizajeOcupadoNoUsaWarp();
        energiaEnElLimiteDelWarp();
        obstaculoNuevoReplanifica();
        sinRutaDuranteElViaje();
        sinCombustibleNoAvanza();
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

    private static void saltoNormalEnPistaLibre() {
        System.out.println("-- salto normal en pista libre");
        preparar(new GrillaDePrueba(10, 1), c(0, 0));
        verificar("inicia viaje en pista libre", nav.iniciarViaje(c(9, 0)));
        for (int x = 1; x <= 9; x++) esperar(ResultadoAvance.SALTO, c(x, 0));
        verificar("llegó al destino", nav.haLlegado());
        verificar("9 saltos = 18 de combustible", nave.getCombustible() == 60 - 18);
        verificar("el salto normal no gasta energía ni desgaste", nave.getEnergia() == 80 && nave.getDesgaste() == 0);
        verificar("el motor sigue Disponible", nave.estaDisponible());
        verificar("bitácora registra el salto", bitacoraContiene("Salto normal: avanza 1 casillero(s)"));
    }

    private static void estacionNoDisparaWarp() {
        System.out.println("-- una estación en el camino no es obstáculo: no hay warp");
        GrillaDePrueba g = new GrillaDePrueba(10, 1);
        g.agregar(new EstacionDeCarga(c(2, 0)));
        preparar(g, c(0, 0));
        nav.iniciarViaje(c(9, 0));
        esperar(ResultadoAvance.SALTO, c(1, 0));
        esperar(ResultadoAvance.SALTO, c(2, 0));   // la pisa: la interacción la hace la misión
        esperar(ResultadoAvance.SALTO, c(3, 0));
        verificar("nunca entró en warp", nave.estaDisponible());
    }

    private static void warpEsquivaAsteroide() {
        System.out.println("-- warp: secuencia completa para esquivar un asteroide");
        GrillaDePrueba g = new GrillaDePrueba(10, 1);
        preparar(g, c(0, 0));
        nav.iniciarViaje(c(9, 0));
        g.agregar(new Asteroide(c(1, 0)));                 // aparece sobre la ruta ya calculada

        esperar(ResultadoAvance.SALTO_WARP, c(0, 0));      // turno 1 de preparación: no avanza
        verificar("preparando: el motor no está disponible", !nave.estaDisponible());
        esperar(ResultadoAvance.SALTO_WARP, c(0, 0));      // turno 2 de preparación: queda En warp
        esperar(ResultadoAvance.SALTO_WARP, c(2, 0));      // turno 3: salta 2 casilleros y esquiva el asteroide
        verificar("tras el salto cobró 5 de energía", nave.getEnergia() == 80 - 5);
        verificar("el warp no gasta combustible", nave.getCombustible() == 60);
        verificar("enfriando: el motor no está disponible", !nave.estaDisponible());
        esperar(ResultadoAvance.SALTO, c(2, 0));           // turno de enfriamiento: no avanza
        verificar("el enfriamiento suma 3 de desgaste", nave.getDesgaste() == 3);
        verificar("terminó el enfriamiento: motor Disponible", nave.estaDisponible());

        for (int x = 3; x <= 9; x++) esperar(ResultadoAvance.SALTO, c(x, 0));
        verificar("llegó al destino", nav.haLlegado());
        verificar("7 saltos normales = 14 de combustible", nave.getCombustible() == 60 - 14);
        verificar("la nave llegó viva", !nave.estaDestruida());
        verificar("bitácora registra el warp", bitacoraContiene("Salto warp"));
    }

    private static void aterrizajeOcupadoNoUsaWarp() {
        System.out.println("-- warp: la casilla de aterrizaje está ocupada");
        GrillaDePrueba g = new GrillaDePrueba(10, 1);
        preparar(g, c(0, 0));
        nav.iniciarViaje(c(9, 0));
        g.agregar(new Asteroide(c(1, 0)));
        g.agregar(new Asteroide(c(2, 0)));                 // donde caería el warp
        int comb = nave.getCombustible(), ener = nave.getEnergia(), desg = nave.getDesgaste();

        ResultadoAvance r = nav.avanzarUnTurno();
        verificar("no usa warp: no hay camino y devuelve SIN_RUTA", r == ResultadoAvance.SIN_RUTA);
        verificar("no se movió ni consumió",
                nav.getPosicion().equals(c(0, 0)) && nave.getCombustible() == comb
                        && nave.getEnergia() == ener && nave.getDesgaste() == desg);
        verificar("el motor sigue Disponible", nave.estaDisponible());
    }

    private static void energiaEnElLimiteDelWarp() {
        System.out.println("-- energía en el límite del warp (se exige 15 = 3 usos de 5)");
        // energía 15: alcanza -> usa warp
        GrillaDePrueba g1 = new GrillaDePrueba(10, 2);
        preparar(g1, c(0, 0));
        nave.consumir(0, 65, 0);
        nav.iniciarViaje(c(9, 0));
        g1.agregar(new Asteroide(c(1, 0)));
        esperar(ResultadoAvance.SALTO_WARP, c(0, 0));

        // energía 14: no alcanza -> no usa warp, rodea el asteroide
        GrillaDePrueba g2 = new GrillaDePrueba(10, 2);
        preparar(g2, c(0, 0));
        nave.consumir(0, 66, 0);
        nav.iniciarViaje(c(9, 0));
        g2.agregar(new Asteroide(c(1, 0)));
        verificar("con 14 de energía no usa warp", nav.avanzarUnTurno() == ResultadoAvance.SALTO);
        verificar("sigue Disponible", nave.estaDisponible());
    }

    private static void obstaculoNuevoReplanifica() {
        System.out.println("-- obstáculo nuevo y sin energía para warp: replanifica");
        GrillaDePrueba g = new GrillaDePrueba(5, 2);
        preparar(g, c(0, 0));
        nave.consumir(0, 70, 0);                           // energía 10: no puede esquivar con warp
        nav.iniciarViaje(c(4, 0));
        g.agregar(new Asteroide(c(1, 0)));                 // aparece sobre la ruta ya calculada

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
        System.out.println("-- el camino se cierra durante el viaje (sin energía para warp)");
        GrillaDePrueba g = new GrillaDePrueba(3, 1);
        preparar(g, c(0, 0));
        nave.consumir(0, 70, 0);                           // energía 10
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

    private static void sinCombustibleNoAvanza() {
        System.out.println("-- combustible insuficiente para un salto (cuesta 2)");
        preparar(new GrillaDePrueba(5, 1), c(0, 0));
        nave.consumir(59, 0, 0);                           // combustible 1
        nav.iniciarViaje(c(4, 0));
        ResultadoAvance r = nav.avanzarUnTurno();
        verificar("el motor no salta: la nave se queda en el lugar", nav.getPosicion().equals(c(0, 0)));
        verificar("devuelve SALTO sin haber avanzado", r == ResultadoAvance.SALTO);
        verificar("no consumió combustible", nave.getCombustible() == 1);
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