package controller;

import java.util.ArrayList;
import java.util.function.Consumer;

import model.Coordenada;
import model.Mision.InformeMision;
import model.Mision.Mision;
import model.Mision.MisionIntercepcion;
import model.Mision.MisionRecoleccion;
import model.Mision.MisionRetorno;
import model.Universo.AdaptadorTableroUniverso;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import model.Universo.NaveEnemiga;
import model.destinoytablero.CantidadDeElementosSuperaCasillaException;
import model.destinoytablero.DimensionesDeTableroInconsistentesException;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;
import model.nave.NaveFactory;
import view.TableroUniversoPanel;

/** Arma el tablero y la misión, la muestra en la vista y la ejecuta fuera del hilo de Swing. */
public class ControladorPartida {
    private Tablero tablero;
    private AdaptadorTableroUniverso universo;
    private Posicion destinoActual;
    private String tipoMision;
    private String titulo;
    private Mision misionActual;

    /** Descarta el tablero para que la próxima misión arme uno nuevo (al cambiar de nave). */
    public void reiniciarTablero() {
        tablero = null;
        universo = null;
    }

    /** POST: queda lista una misión del tipo pedido (INTERCEPCION, RECOLECCION o RETORNO) */
    public void preparar(String tipo, AsistenteDeComando asistente) {
        misionActual = crearMision(tipo, asistente);
    }

    /** Muestra el tablero y el título de la misión preparada. */
    public void mostrarEn(TableroUniversoPanel vista, ControladorNave nave) {
        vista.setTitulo(titulo);
        vista.mostrarTablero(
                universo.getTablero(),
                nave.getTipo(),
                nave.getNave(),
                destinoActual,
                tipoMision,
                nave.getAsistente().getBitacora(),
                universo.getReveladas());
    }

    /**
     * Ejecuta la misión preparada en un hilo aparte (la misión duerme entre turnos).
     * alTerminar se invoca desde ese hilo: quien lo pase debe volver al hilo de la interfaz.
     */
    public void ejecutarEnSegundoPlano(final ControladorNave nave, final Consumer<ResumenMisionVista> alTerminar) {
        final Mision mision = misionActual;
        new Thread(new Runnable() {
            public void run() {
                InformeMision informe;
                try {
                    informe = nave.getAsistente().encomendarMision(mision);
                } catch (RuntimeException ex) {
                    informe = mision.getInforme();
                }
                nave.actualizarOperativa();
                alTerminar.accept(nave.resumen(informe));
            }
        }, "mision").start();
    }

    public Mision crearMision(String tipo, AsistenteDeComando asistente) {
        asegurarTablero();
        tipoMision = tipo;
        Coordenada origen = universo.getPosicionNave();

        if ("RETORNO".equals(tipo)) {
            int bx = tablero.getX() / 2;
            int by = tablero.getY() / 2;
            destinoActual = new Posicion(bx, by);
            Coordenada base = new Coordenada(bx, by);
            titulo = "M-03 Retorno seguro -> Base " + base;
            return new MisionRetorno(asistente, universo, origen, base);
        }

        destinoActual = tablero.obtenerObjetivo();
        Coordenada destino = new Coordenada(destinoActual.getX(), destinoActual.getY());

        if ("INTERCEPCION".equals(tipo)) {
            titulo = "M-01 Intercepción -> Auxilio en " + destino;
            return new MisionIntercepcion(asistente, universo, origen, destino, 20, 20);
        }
        titulo = "M-02 Recolección -> Zona en " + destino;
        return new MisionRecoleccion(asistente, universo, origen, destino);
    }

    private void asegurarTablero() {
        if (tablero != null) return;
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<ElementoDelUniverso>();
        for (int i = 0; i < 10; i++) {
            elementos.add(new Asteroide(new Coordenada(0, 0)));
        }
        NaveFactory fabrica = new NaveFactory();
        for (int i = 0; i < 5; i++) {
            for (String t : new String[] {"EXPLORADORA", "CARGUERO", "COMBATE"}) {
                elementos.add(new NaveEnemiga(fabrica.crearNave(t), new Coordenada(0, 0)));
            }
        }
        for (int i = 0; i < 4; i++) {
            elementos.add(new EstacionDeCarga(new Coordenada(0, 0)));
        }
        try {
            tablero = new Tablero(20, 20, elementos);
        } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException ex) {
            throw new IllegalStateException("No se pudo crear el tablero de misión", ex);
        }
        universo = new AdaptadorTableroUniverso(tablero);
    }
}