package pruebas;

import java.util.List;

import model.Coordenada;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import model.Universo.RadarCortoAlcance;
import model.Universo.RadarLargoAlcance;

public class PruebaRadares {
    private static int fallos = 0;

    public static void main(String[] args) {
        pruebasRadarLargo();
        pruebasRadarCorto();
        System.out.println(fallos == 0 ? "\nTODOS LOS CASOS OK" : "\nFALLOS: " + fallos);
    }

    private static Coordenada c(int x, int y) { return new Coordenada(x, y); }

    // ---------- Radar de largo alcance ----------
    private static void pruebasRadarLargo() {
        RadarLargoAlcance radar = new RadarLargoAlcance();

        GrillaDePrueba libre = new GrillaDePrueba(5, 5);
        List<Coordenada> r = radar.calcularRuta(libre, c(0, 0), c(4, 4));
        verificar("largo: ruta libre es la más corta (9 casillas)", r.size() == 9);
        verificar("largo: empieza en el origen y termina en el destino",
                r.get(0).equals(c(0, 0)) && r.get(r.size() - 1).equals(c(4, 4)));
        verificar("largo: cada paso avanza una sola casilla", pasosContiguos(r));

        r = radar.calcularRuta(libre, c(2, 2), c(2, 2));
        verificar("largo: origen == destino -> [origen]", r.size() == 1 && r.get(0).equals(c(2, 2)));

        GrillaDePrueba conAsteroide = new GrillaDePrueba(3, 3);
        conAsteroide.agregar(new Asteroide(c(1, 0)));
        r = radar.calcularRuta(conAsteroide, c(0, 0), c(2, 0));
        verificar("largo: rodea el asteroide (5 casillas)", r.size() == 5);
        verificar("largo: la ruta no pasa por el asteroide", !r.contains(c(1, 0)));
        verificar("largo: rodeando sigue siendo contigua", pasosContiguos(r));

        GrillaDePrueba muro = new GrillaDePrueba(3, 3);
        for (int y = 0; y < 3; y++) muro.agregar(new Asteroide(c(1, y)));
        verificar("largo: muro de asteroides -> sin ruta (lista vacía)",
                radar.calcularRuta(muro, c(0, 1), c(2, 1)).isEmpty());

        GrillaDePrueba destinoBloqueado = new GrillaDePrueba(5, 1);
        destinoBloqueado.agregar(new Asteroide(c(4, 0)));
        r = radar.calcularRuta(destinoBloqueado, c(0, 0), c(4, 0));
        verificar("largo: el destino con asteroide es alcanzable", r.size() == 5);

        GrillaDePrueba conEstacion = new GrillaDePrueba(3, 1);
        conEstacion.agregar(new EstacionDeCarga(c(1, 0)));
        verificar("largo: una estación no bloquea el camino",
                radar.calcularRuta(conEstacion, c(0, 0), c(2, 0)).size() == 3);

        verificarExcepcion("largo: universo null", () -> radar.calcularRuta(null, c(0, 0), c(1, 1)));
        verificarExcepcion("largo: origen null", () -> radar.calcularRuta(libre, null, c(1, 1)));
        verificarExcepcion("largo: destino null", () -> radar.calcularRuta(libre, c(0, 0), null));
        verificarExcepcion("largo: destino fuera de la grilla", () -> radar.calcularRuta(libre, c(0, 0), c(9, 9)));
        verificarExcepcion("largo: origen fuera de la grilla", () -> radar.calcularRuta(libre, c(-1, 0), c(1, 1)));
    }

    private static boolean pasosContiguos(List<Coordenada> ruta) {
        for (int i = 1; i < ruta.size(); i++)
            if (ruta.get(i - 1).distanciaManhattan(ruta.get(i)) != 1) return false;
        return true;
    }

    // ---------- Radar de corto alcance ----------
    private static void pruebasRadarCorto() {
        GrillaDePrueba g = new GrillaDePrueba(5, 5);
        Asteroide cerca = new Asteroide(c(2, 3));          // distancia 1 desde (2,2)
        EstacionDeCarga media = new EstacionDeCarga(c(3, 3)); // distancia 2
        Asteroide lejos = new Asteroide(c(4, 4));          // distancia 4
        Asteroide propia = new Asteroide(c(2, 2));         // en la casilla de la nave
        g.agregar(lejos); g.agregar(media); g.agregar(cerca); g.agregar(propia);

        List<ElementoDelUniverso> d = new RadarCortoAlcance(1).escanear(g, c(2, 2));
        verificar("corto: radio 1 ve solo lo adyacente", d.size() == 1 && d.get(0) == cerca);

        d = new RadarCortoAlcance(2).escanear(g, c(2, 2));
        verificar("corto: radio 2 ve dos elementos", d.size() == 2);
        verificar("corto: ordenados del más cercano al más lejano", d.get(0) == cerca && d.get(1) == media);

        d = new RadarCortoAlcance(4).escanear(g, c(2, 2));
        verificar("corto: radio 4 ve los tres y no su propia casilla", d.size() == 3 && !d.contains(propia));

        GrillaDePrueba esquina = new GrillaDePrueba(5, 5);
        esquina.agregar(new Asteroide(c(0, 1)));
        verificar("corto: en el borde ignora lo que queda fuera de la grilla",
                new RadarCortoAlcance(2).escanear(esquina, c(0, 0)).size() == 1);

        verificar("corto: radio por defecto", new RadarCortoAlcance().getRadio() == RadarCortoAlcance.RADIO_POR_DEFECTO);
        verificarExcepcion("corto: radio 0", () -> new RadarCortoAlcance(0));
        verificarExcepcion("corto: universo null", () -> new RadarCortoAlcance().escanear(null, c(0, 0)));
        verificarExcepcion("corto: posición null", () -> new RadarCortoAlcance().escanear(g, null));
        verificarExcepcion("corto: posición fuera de la grilla", () -> new RadarCortoAlcance().escanear(g, c(9, 9)));
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