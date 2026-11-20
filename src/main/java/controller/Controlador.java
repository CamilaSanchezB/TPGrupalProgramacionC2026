package controller;

import view.CrearNavePanel;
import view.IniciarSesionPanel;
import view.TableroUniversoPanel;
import view.Ventana;

/** Decide el flujo entre pantallas. Las pantallas no se conocen entre sí. */
public class Controlador {
    private final Ventana ventana = new Ventana();
    private final IniciarSesionPanel iniciarSesion = new IniciarSesionPanel();
    private final CrearNavePanel crearNave = new CrearNavePanel();
    private final TableroUniversoPanel tablero = new TableroUniversoPanel();

    public static void main(String[] args) {
        new Controlador().arrancar();
    }

    private void arrancar() {
        ventana.agregarPantalla(Ventana.INICIAR_SESION, iniciarSesion);
        ventana.agregarPantalla(Ventana.CREAR_NAVE, crearNave);
        ventana.agregarPantalla(Ventana.TABLERO, tablero);

        // TODO: enganchar los listeners de cada panel, por ejemplo:
        // iniciarSesion.addIngresarListener(...)  -> ingresar()
        // crearNave.addIniciarMisionListener(...) -> iniciarMision()
        // tablero.addVolverListener(...)          -> volver()

        ventana.mostrar(Ventana.INICIAR_SESION);
        ventana.setVisible(true);
    }

    private void ingresar() {
        // TODO: validar usuario en el modelo
        ventana.mostrar(Ventana.CREAR_NAVE);
    }

    private void iniciarMision() {
        // TODO: crear el Tablero (y el Destino/Timer) y pasárselo al panel:
        // tablero.mostrarTablero(unTablero);
        ventana.mostrar(Ventana.TABLERO);
    }

    private void volver() {
        // TODO: detener el Timer de la misión
        tablero.desconectar();
        ventana.mostrar(Ventana.CREAR_NAVE);
    }
}
