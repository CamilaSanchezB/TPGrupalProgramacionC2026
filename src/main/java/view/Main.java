package view;

import javax.swing.SwingUtilities;

import controller.AsistenteDeComando;
import controller.LiquidadorHaberes;
import controller.LoginController;
import controller.TripulantesController;
import model.ClasesBitacora.Bitacora;
import model.Coordenada;
import model.destinoytablero.CantidadDeElementosSuperaCasillaException;
import model.destinoytablero.DimensionesDeTableroInconsistentesException;
import model.destinoytablero.Posicion;
import model.destinoytablero.Tablero;
import model.nave.Nave;
import model.nave.NaveFactory;
import model.Mision.*;
import model.Universo.AdaptadorTableroUniverso;
import model.Universo.Asteroide;
import model.Universo.ElementoDelUniverso;
import model.Universo.EstacionDeCarga;
import persistencia.ServicioUsuarios;
import persistencia.dao.UsuarioDAOSqlite;
import java.util.ArrayList;
import controller.ControladorVisor;
import model.Concurrencia.UniversoParalelo;

public class Main {
    private static Ventana v;
    private static TableroUniversoPanel partida;
    private static ResultadoPanel resultado;
    private static Nave nave;
    private static AsistenteDeComando asistente;
    private static boolean ultimoGano;
    private static Tablero tableroDeMision;
    private static String tipoNaveSeleccionada = null;
    private static boolean partidaEnCurso;
    private static VistaNaves visor;

    private static AdaptadorTableroUniverso universoDeMision;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { public void run() { armar(); } });
    }

    private static void armar() {
        ConfigView.aplicar();
        v = new Ventana();

        LoginView login = new LoginView();
        new LoginController(crearServicioUsuarios()).setVista(login);
        login.setAlIngresar(() -> v.mostrar(Ventana.SELECTOR));

        SelectorNavePanel selector = new SelectorNavePanel();
        selector.addElegirListener(e -> elegirNave(e.getActionCommand()));

        MainPanel main = new MainPanel();
        main.addTripulantesListener(e -> v.mostrar(Ventana.TRIPULANTES));
        main.addIniciarListener(e -> iniciarPartida());

        partida = new TableroUniversoPanel();
        partida.addUniversoListener(e -> abrirUniversoParalelo());
        resultado = new ResultadoPanel();
        resultado.addAccionListener(e -> v.mostrar(ultimoGano ? Ventana.MAIN : Ventana.SELECTOR));

        v.agregarPantalla(Ventana.INICIAR_SESION, login);
        v.agregarPantalla(Ventana.SELECTOR, selector);
        v.agregarPantalla(Ventana.MAIN, main);
        v.agregarPantalla(Ventana.TABLERO, partida);
        v.agregarPantalla(Ventana.RESULTADO, resultado);
        v.mostrar(Ventana.INICIAR_SESION);
        v.setVisible(true);
        login.arranca();
    }

    /** Crea la nave y su asistente, y arma la pantalla de tripulantes de ESA nave. */
    private static void elegirNave(String tipo) {
        tableroDeMision = null;
        universoDeMision = null;
        tipoNaveSeleccionada = tipo;
        nave = crearNave(tipo);                       // COMBATE / CARGUERA / EXPLORADORA
        asistente = new AsistenteDeComando(nave, new Bitacora());

        TripulantesView trip = new TripulantesView();
        new TripulantesController(nave, new LiquidadorHaberes()).setVista(trip);
        trip.addVolverListener(e -> v.mostrar(Ventana.MAIN));
        v.agregarPantalla(Ventana.TRIPULANTES, trip);  // reemplaza la anterior si la había
        v.mostrar(Ventana.MAIN);
    }

    private static void iniciarPartida() {
        if (partidaEnCurso || tipoNaveSeleccionada == null) return;
        partidaEnCurso = true;

        final Mision mision = crearMision();
        partida.mostrarTablero(tableroDeMision, tipoNaveSeleccionada,
                asistente.getBitacora(), universoDeMision.getReveladas());
        v.mostrar(Ventana.TABLERO);

        new Thread(new Runnable() { public void run() {
            boolean gano;
            try {
                asistente.encomendarMision(mision);   // bloquea; el sleep(1000) por turno está en la misión
                gano = mision.getResultado() == ResultadoMision.EXITOSA;
            } catch (RuntimeException ex) {           // p. ej. OperacionRechazadaException
                gano = false;
            }
            final boolean resultadoFinal = gano;
            SwingUtilities.invokeLater(new Runnable() { public void run() {
                ultimoGano = resultadoFinal;
                partidaEnCurso = false;
                partida.desconectar();
                resultado.mostrar(resultadoFinal);
                v.mostrar(Ventana.RESULTADO);
            }});
        }}).start();
    }

    // ---- Lo único que me falta ver del modelo (ver lista abajo) ----

    private static ServicioUsuarios crearServicioUsuarios() {
        return new ServicioUsuarios(new UsuarioDAOSqlite());
    }

    private static Nave crearNave(String tipo) {
        return new NaveFactory().crearNave(tipo);
    }

    private static Mision crearMision() {
        if (tableroDeMision == null) {                 // tablero solo en la primera misión de esta nave
            ArrayList<ElementoDelUniverso> elementos = new ArrayList<ElementoDelUniverso>();
            for (int i = 0; i < 8; i++) elementos.add(new Asteroide(new Coordenada(0, 0)));      // el tablero fija la posición real
            for (int i = 0; i < 3; i++) elementos.add(new EstacionDeCarga(new Coordenada(0, 0)));
            try {
                tableroDeMision = new Tablero(20, 20, elementos);
            } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException ex) {
                throw new IllegalStateException("No se pudo crear el tablero de misión", ex);
            }
            universoDeMision = new AdaptadorTableroUniverso(tableroDeMision);
        }
        Posicion po = tableroDeMision.getPosicionNave();    // donde quedó la nave
        Posicion pd = tableroDeMision.obtenerObjetivo();    // objetivo nuevo
        return new MisionRecoleccion(asistente, universoDeMision, new Coordenada(po.getX(), po.getY()), new Coordenada(pd.getX(), pd.getY()));
    }

    private static Tablero getTablero() {
        return tableroDeMision;
    }

    private static void abrirUniversoParalelo() {
    if (visor != null && visor.isDisplayable()) { // ya está abierto
        visor.toFront();
        return;
    }
    visor = new VistaNaves();
    UniversoParalelo universo = new UniversoParalelo();
    new ControladorVisor(visor, universo, visor::dispose).iniciar();
    }

}
