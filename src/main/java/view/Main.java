package view;

import javax.swing.SwingUtilities;

import controller.AsistenteDeComando;
import controller.LiquidadorHaberes;
import controller.LoginController;
import controller.TripulantesController;
import model.ClasesBitacora.Bitacora;
import model.Tablero;
import model.Mision.Mision;
import model.nave.Nave;
import persistencia.ServicioUsuarios;
import persistencia.dao.UsuarioDAOSqlite;

public class Main {
    private static Ventana v;
    private static TableroUniversoPanel partida;
    private static ResultadoPanel resultado;
    private static Nave nave;
    private static AsistenteDeComando asistente;
    private static boolean ultimoGano;

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
        nave = crearNave(tipo);                       // COMBATE / CARGUERA / EXPLORADORA
        asistente = new AsistenteDeComando(nave, new Bitacora());

        TripulantesView trip = new TripulantesView();
        new TripulantesController(nave, new LiquidadorHaberes()).setVista(trip);
        trip.addVolverListener(e -> v.mostrar(Ventana.MAIN));
        v.agregarPantalla(Ventana.TRIPULANTES, trip);  // reemplaza la anterior si la había
        v.mostrar(Ventana.MAIN);
    }

    private static void iniciarPartida() {
        final Mision mision = crearMision(nave);
        partida.mostrarTablero(getTablero(mision));
        v.mostrar(Ventana.TABLERO);

        new Thread(new Runnable() { public void run() {
            boolean gano;
            try {
                asistente.encomendarMision(mision);   // bloquea; el sleep(1000) por turno está en la misión
                gano = !asistente.estaDestruida();    // TODO: ajustar con el informe (ver abajo)
            } catch (RuntimeException ex) {           // p. ej. OperacionRechazadaException
                gano = false;
            }
            final boolean resultadoFinal = gano;
            SwingUtilities.invokeLater(new Runnable() { public void run() {
                ultimoGano = resultadoFinal;
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
        throw new UnsupportedOperationException("TODO: FabricaDeNaves... según tipo");
    }

    private static Mision crearMision(Nave n) {
        throw new UnsupportedOperationException("TODO: new MisionXX(n, <tablero/destino>...)");
    }

    private static Tablero getTablero(Mision m) {
        throw new UnsupportedOperationException("TODO: obtener el Tablero de la misión");
    }
}