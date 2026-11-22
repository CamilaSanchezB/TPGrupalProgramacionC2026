package view;

import javax.swing.SwingUtilities;

import controller.ControladorNave;
import controller.ControladorPartida;
import controller.ControladorVisor;
import controller.LiquidadorHaberes;
import controller.LoginController;
import controller.TripulantesController;
import model.Concurrencia.UniversoParalelo;
import persistencia.ServicioUsuarios;
import persistencia.dao.UsuarioDAOSqlite;

/** Arranque y navegación entre pantallas. No contiene lógica de negocio. */
public class Main {
    private static final ControladorNave naveCtrl = new ControladorNave();
    private static final ControladorPartida partidaCtrl = new ControladorPartida();

    private static Ventana v;
    private static MainPanel mainPanel;
    private static TableroUniversoPanel partida;
    private static ResultadoPanel resultado;
    private static boolean partidaEnCurso;
    private static VistaNaves visor;

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
            if (naveCtrl.isOperativa()) {
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
        mainPanel.actualizarEstado(naveCtrl.estado());
        v.mostrar(Ventana.MAIN);
    }

    private static void elegirNave(String tipo) {
        partidaCtrl.reiniciarTablero();
        naveCtrl.elegirNave(tipo);

        TripulantesView trip = new TripulantesView();
        new TripulantesController(naveCtrl.getNave(), new LiquidadorHaberes()).setVista(trip);
        trip.addVolverListener(e -> irAlCentroDeComando());
        v.agregarPantalla(Ventana.TRIPULANTES, trip);   // reemplaza la anterior si la había
        irAlCentroDeComando();
    }

    private static void iniciarPartida(String tipoMision) {
        if (partidaEnCurso || !naveCtrl.haySeleccion()) return;
        partidaEnCurso = true;

        partidaCtrl.preparar(tipoMision, naveCtrl.getAsistente());
        partidaCtrl.mostrarEn(partida, naveCtrl);
        v.mostrar(Ventana.TABLERO);

        partidaCtrl.ejecutarEnSegundoPlano(naveCtrl, resumen ->
                SwingUtilities.invokeLater(() -> {
                    partidaEnCurso = false;
                    partida.desconectar();
                    resultado.mostrar(resumen);
                    v.mostrar(Ventana.RESULTADO);
                }));
    }

    private static ServicioUsuarios crearServicioUsuarios() {
        return new ServicioUsuarios(new UsuarioDAOSqlite());
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