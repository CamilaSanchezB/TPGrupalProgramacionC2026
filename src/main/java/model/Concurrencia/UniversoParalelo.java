package model.Concurrencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fachada del modelo del visor: crea naves (cada una en su hilo) en tiempo de
 * ejecucion y es el Observable que la vista/controlador usan.
 */
public class UniversoParalelo {

    private static final int CAPACIDAD_PLATAFORMA = 5;

    private final PlataFormaDeCarga plataforma = new PlataFormaDeCarga(CAPACIDAD_PLATAFORMA);
    private final List<ObservadorVisor> observadores = new CopyOnWriteArrayList<>();
    private final List<Thread> hilos = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger contadorGeneradoras = new AtomicInteger();
    private final AtomicInteger contadorConsumidoras = new AtomicInteger();

    public void agregarObservador(ObservadorVisor o) {
        observadores.add(o);
        plataforma.agregarObservador(o);
    }

    public void agregarGeneradora() {
        String nombre = "Generadora-" + contadorGeneradoras.incrementAndGet();
        for (ObservadorVisor o : observadores) {
            o.agregarNaveGeneradora(nombre);
            o.agregarLog("¡Ingreso una nueva Generadora al universo paralelo! (" + nombre + ")");
        }
        iniciar(new NaveGeneradora(nombre, plataforma), nombre);
    }

    public void agregarConsumidora() {
        String nombre = "Consumidora-" + contadorConsumidoras.incrementAndGet();
        for (ObservadorVisor o : observadores) {
            o.agregarNaveConsumidora(nombre);
            o.agregarLog("¡Ingreso una nueva Nave al universo paralelo! (" + nombre + ")");
        }
        iniciar(new NaveConsumidora(nombre, plataforma), nombre);
    }

    /** Corta todos los hilos (interrupt). Despues de esto el universo no se reutiliza. */
    public void detener() {
        synchronized (hilos) {
            for (Thread t : hilos) {
                t.interrupt();
            }
            hilos.clear();
        }
    }

    private void iniciar(Runnable r, String nombre) {
        Thread t = new Thread(r, nombre);
        t.setDaemon(true); // no impide cerrar la aplicacion
        hilos.add(t);
        t.start();
    }
}