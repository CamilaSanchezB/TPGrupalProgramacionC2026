package view;

import java.util.ArrayList;
import javax.swing.SwingUtilities;

import controller.AsistenteDeComando;
import controller.ControladorVisor;
import controller.LiquidadorHaberes;
import controller.LoginController;
import controller.TripulantesController;
import model.ClasesBitacora.Bitacora;
import model.Concurrencia.UniversoParalelo;
import model.Coordenada;
import model.Mision.InformeMision;
import model.Mision.Mision;
import model.Mision.MisionIntercepcion;
import model.Mision.MisionRecoleccion;
import model.Mision.MisionRetorno;
import model.Mision.ResultadoMision;
import model.Universo.AdaptadorTableroUniverso;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import model.Universo.NaveEnemiga;
import model.destinoytablero.CantidadDeElementosSuperaCasillaException;
import model.destinoytablero.DimensionesDeTableroInconsistentesException;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;
import model.nave.Nave;
import model.nave.NaveFactory;
import persistencia.ServicioUsuarios;
import persistencia.dao.UsuarioDAOSqlite;

public class Main {
    private static Ventana v;
    private static MainPanel mainPanel;
    private static TableroUniversoPanel partida;
    private static ResultadoPanel resultado;
    private static Nave nave;
    private static AsistenteDeComando asistente;
    private static boolean naveOperativa;
    private static Tablero tableroDeMision;
    private static String tipoNaveSeleccionada = null;
    private static boolean partidaEnCurso;
    private static VistaNaves visor;

    private static AdaptadorTableroUniverso universoDeMision;
    private static Posicion destinoActual;
    private static String tipoMisionActual;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                armar();
            }
        });
    }

    private static void armar() {
        ConfigView.aplicar();
        v = new Ventana();

        LoginView login = new LoginView();
        new LoginController(crearServicioUsuarios()).setVista(login);
        login.setAlIngresar(() -> v.mostrar(Ventana.SELECTOR));

        SelectorNavePanel selector = new SelectorNavePanel();
        selector.addElegirListener(e -> elegirNave(e.getActionCommand()));

        mainPanel = new MainPanel();
        mainPanel.addTripulantesListener(e -> v.mostrar(Ventana.TRIPULANTES));
        mainPanel.addIntercepcionListener(e -> iniciarPartida("INTERCEPCION"));
        mainPanel.addRecoleccionListener(e -> iniciarPartida("RECOLECCION"));
        mainPanel.addRetornoListener(e -> iniciarPartida("RETORNO"));

        partida = new TableroUniversoPanel();
        partida.addUniversoListener(e -> abrirUniversoParalelo());
        resultado = new ResultadoPanel();
        resultado.addAccionListener(e -> {
            if (naveOperativa) {
                irAlCentroDeComando();
            } else {
                v.mostrar(Ventana.SELECTOR);
            }
        });

        v.agregarPantalla(Ventana.INICIAR_SESION, login);
        v.agregarPantalla(Ventana.SELECTOR, selector);
        v.agregarPantalla(Ventana.MAIN, mainPanel);
        v.agregarPantalla(Ventana.TABLERO, partida);
        v.agregarPantalla(Ventana.RESULTADO, resultado);
        v.mostrar(Ventana.INICIAR_SESION);
        v.setVisible(true);
        login.arranca();
    }

    private static void irAlCentroDeComando() {
        mainPanel.actualizarEstado(nave, asistente, tipoNaveSeleccionada);
        v.mostrar(Ventana.MAIN);
    }

    /** Crea la nave y su asistente, y arma la pantalla de tripulantes de ESA nave. */
    private static void elegirNave(String tipo) {
        tableroDeMision = null;
        universoDeMision = null;
        tipoNaveSeleccionada = tipo;
        nave = crearNave(tipo);                       // COMBATE / CARGUERA / EXPLORADORA
        asistente = new AsistenteDeComando(nave, new Bitacora());
        naveOperativa = true;

        TripulantesView trip = new TripulantesView();
        new TripulantesController(nave, new LiquidadorHaberes()).setVista(trip);
        trip.addVolverListener(e -> irAlCentroDeComando());
        v.agregarPantalla(Ventana.TRIPULANTES, trip);  // reemplaza la anterior si la había
        irAlCentroDeComando();
    }

    private static void iniciarPartida(String tipoMision) {
        if (partidaEnCurso || tipoNaveSeleccionada == null) return;
        partidaEnCurso = true;

        final Mision mision = crearMision(tipoMision);
        partida.mostrarTablero(
                universoDeMision.getTablero(),
                tipoNaveSeleccionada,
                nave,
                destinoActual,
                tipoMisionActual,
                asistente.getBitacora(),
                universoDeMision.getReveladas());
        v.mostrar(Ventana.TABLERO);

        new Thread(new Runnable() {
            public void run() {
                InformeMision informeFinal = null;
                try {
                    informeFinal = asistente.encomendarMision(mision);
                } catch (RuntimeException ex) {
                    informeFinal = mision.getInforme();
                }
                final InformeMision informe = informeFinal;
                final boolean sigueViva = !nave.estaDestruida();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        naveOperativa = sigueViva;
                        partidaEnCurso = false;
                        partida.desconectar();
                        resultado.mostrar(informe, nave);
                        v.mostrar(Ventana.RESULTADO);
                    }
                });
            }
        }).start();
    }

    private static ServicioUsuarios crearServicioUsuarios() {
        return new ServicioUsuarios(new UsuarioDAOSqlite());
    }

    private static Nave crearNave(String tipo) {
        return new NaveFactory().crearNave(tipo);
    }

    private static void asegurarTablero() {
        if (tableroDeMision == null) {
            ArrayList<ElementoDelUniverso> elementos = new ArrayList<ElementoDelUniverso>();
            for (int i = 0; i < 10; i++) {
                elementos.add(new Asteroide(new Coordenada(0, 0)));
            }
            NaveFactory fabrica = new NaveFactory();
            for (int i = 0; i < 5; i++) {
                for (String tipo : new String[] {"EXPLORADORA", "CARGUERO", "COMBATE"}) {
                    elementos.add(new NaveEnemiga(fabrica.crearNave(tipo), new Coordenada(0, 0)));
                }
            }
            for (int i = 0; i < 4; i++) {
                elementos.add(new EstacionDeCarga(new Coordenada(0, 0)));
            }
            try {
                tableroDeMision = new Tablero(20, 20, elementos);
            } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException ex) {
                throw new IllegalStateException("No se pudo crear el tablero de misión", ex);
            }
            universoDeMision = new AdaptadorTableroUniverso(tableroDeMision);
        }
    }

    private static Mision crearMision(String tipoMision) {
        asegurarTablero();
        tipoMisionActual = tipoMision;
        Coordenada origen = universoDeMision.getPosicionNave();

        if ("RETORNO".equals(tipoMision)) {
            int bx = tableroDeMision.getX() / 2;
            int by = tableroDeMision.getY() / 2;
            destinoActual = new Posicion(bx, by);
            Coordenada base = new Coordenada(bx, by);
            partida.setTitulo("M-03 Retorno seguro -> Base " + base);
            return new MisionRetorno(asistente, universoDeMision, origen, base);
        }

        destinoActual = tableroDeMision.obtenerObjetivo();
        Coordenada destino = new Coordenada(destinoActual.getX(), destinoActual.getY());

        if ("INTERCEPCION".equals(tipoMision)) {
            partida.setTitulo("M-01 Intercepción -> Auxilio en " + destino);
            return new MisionIntercepcion(asistente, universoDeMision, origen, destino, 20, 20);
        }

        partida.setTitulo("M-02 Recolección -> Zona en " + destino);
        return new MisionRecoleccion(asistente, universoDeMision, origen, destino);
    }

    private static Tablero getTablero() {
        return tableroDeMision;
    }

    private static void abrirUniversoParalelo() {
        if (visor != null && visor.isDisplayable()) {
            visor.toFront();
            return;
        }
        visor = new VistaNaves();
        UniversoParalelo universo = new UniversoParalelo();
        new ControladorVisor(visor, universo, visor::dispose).iniciar();
    }
}