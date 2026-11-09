package pruebas;

import model.Coordenada;
import model.nave.Nave;
import model.nave.NaveFactory;
import model.Universo.Asteroide;
import model.Universo.EstacionDeCarga;

public class PruebaUniverso {
    private static final NaveFactory fabrica = new NaveFactory();
    private static final Coordenada POS = new Coordenada(1, 1);
    private static int fallos = 0;

    public static void main(String[] args) {
        pruebasEstaDestruida();
        pruebasAsteroide();
        pruebasEstacion();
        System.out.println(fallos == 0 ? "\nTODOS LOS CASOS OK" : "\nFALLOS: " + fallos);
    }

    private static void pruebasEstaDestruida() {
        Nave n = fabrica.crearNave("EXPLORADORA");
        verificar("nave nueva no está destruida", !n.estaDestruida());

        n = fabrica.crearNave("EXPLORADORA");
        n.consumir(0, n.getEnergia(), 0);
        verificar("energía 0 -> destruida", n.estaDestruida());

        n = fabrica.crearNave("EXPLORADORA");
        n.consumir(n.getCombustible(), 0, 0);
        verificar("combustible 0 -> destruida", n.estaDestruida());

        n = fabrica.crearNave("EXPLORADORA");
        n.consumir(0, 0, 99);
        verificar("desgaste 99 -> no destruida", !n.estaDestruida());
        n.consumir(0, 0, 1);
        verificar("desgaste 100 -> destruida", n.estaDestruida());

        n = fabrica.crearNave("EXPLORADORA");
        n.destruir();
        verificar("destruir() -> destruida", n.estaDestruida());
    }

    private static void pruebasAsteroide() {
        Asteroide a = new Asteroide(POS);

        Nave n = fabrica.crearNave("EXPLORADORA");            // energía 80
        int comb = n.getCombustible(), desg = n.getDesgaste();
        String msg = a.interactuar(n);
        verificar("asteroide: resta 20 de energía", n.getEnergia() == 80 - Asteroide.DANIO_ENERGIA);
        verificar("asteroide: no destruye con energía de sobra", !n.estaDestruida());
        verificar("asteroide: combustible y desgaste intactos", n.getCombustible() == comb && n.getDesgaste() == desg);
        verificar("asteroide: devuelve descripción", msg != null && !msg.isEmpty());

        n = fabrica.crearNave("EXPLORADORA");
        n.consumir(0, n.getEnergia() - Asteroide.DANIO_ENERGIA, 0);   // queda justo en 20
        a.interactuar(n);
        verificar("asteroide: energía justa -> llega a 0 y destruye", n.getEnergia() == 0 && n.estaDestruida());

        n = fabrica.crearNave("EXPLORADORA");
        n.consumir(0, n.getEnergia() - 5, 0);                          // queda en 5
        a.interactuar(n);
        verificar("asteroide: con menos energía que el daño queda en 0 (sin negativos)",
                n.getEnergia() == 0 && n.estaDestruida());

        verificarExcepcion("asteroide: nave null -> IllegalArgumentException", () -> a.interactuar(null));
        verificarExcepcion("asteroide: posición null -> IllegalArgumentException", () -> new Asteroide(null));
    }

    private static void pruebasEstacion() {
        EstacionDeCarga e = new EstacionDeCarga(POS);

        Nave n = fabrica.crearNave("EXPLORADORA");            // comb 60, energía 80
        int desg = n.getDesgaste();
        e.interactuar(n);
        verificar("estación: combustible +20", n.getCombustible() == 60 + EstacionDeCarga.RECARGA_COMBUSTIBLE);
        verificar("estación: energía +20", n.getEnergia() == 80 + EstacionDeCarga.RECARGA_ENERGIA);
        verificar("estación: desgaste intacto", n.getDesgaste() == desg);

        n = fabrica.crearNave("CARGUERO");                    // comb 100 (lleno), energía 60
        e.interactuar(n);
        verificar("estación: combustible lleno no cambia ni falla", n.getCombustible() == 100);
        verificar("estación: energía sí sube", n.getEnergia() == 80);

        n = fabrica.crearNave("COMBATE");                     // comb 80, energía 100
        n.consumir(0, 15, 0);                                  // energía 85
        e.interactuar(n);
        verificar("estación: recorta la energía para no pasar de 100", n.getEnergia() == 100);

        e.interactuar(n);                                      // ya está todo lleno
        verificar("estación: con todo lleno no pasa nada", n.getCombustible() == 100 && n.getEnergia() == 100);

        verificarExcepcion("estación: nave null -> IllegalArgumentException", () -> e.interactuar(null));
        verificarExcepcion("estación: posición null -> IllegalArgumentException", () -> new EstacionDeCarga(null));
    }

    // ---------- auxiliares ----------
    private static void verificar(String descripcion, boolean ok) {
        if (!ok) fallos++;
        System.out.println((ok ? "OK    " : "FALLO ") + descripcion);
    }

    private static void verificarExcepcion(String descripcion, Runnable accion) {
        try {
            accion.run();
            verificar(descripcion + " (no lanzó)", false);
        } catch (IllegalArgumentException ex) {
            verificar(descripcion, true);
        }
    }
}