package model.Concurrencia;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Recurso compartido (monitor) del problema productor-consumidor.
 * Toda la sincronizacion vive aca adentro. No imprime ni conoce la vista:
 * solo avisa a sus observadores.
 *
 * Los avisos se hacen DENTRO del metodo synchronized para que el orden de los
 * eventos coincida con el orden real de las operaciones (la vista los pasa al EDT).
 */
public class PlataFormaDeCarga {

    private final LinkedList<Bateria> baterias = new LinkedList<>();
    private final int capacidad;
    private final List<ObservadorVisor> observadores = new CopyOnWriteArrayList<>();

    public PlataFormaDeCarga(int capacidad) {
        this.capacidad = capacidad;
    }

    public void agregarObservador(ObservadorVisor o) {
        observadores.add(o);
    }

    public synchronized void depositar(Bateria nueva, String nombre) throws InterruptedException {
        boolean avisoEspera = false;
        while (baterias.size() == capacidad) {
            if (!avisoEspera) { // se avisa una sola vez, no en cada despertar
                avisarLog(nombre + " esperando a que se libere espacio en la Plataforma.");
                avisoEspera = true;
            }
            wait();
        }
        baterias.add(nueva);
        for (ObservadorVisor o : observadores) {
            o.naveGeneroBateria(nombre);
        }
        notifyAll();
    }

    public synchronized Bateria retirar(String nombre) throws InterruptedException {
        avisarLog(nombre + " solicitando bateria.");
        boolean avisoEspera = false;
        while (baterias.isEmpty()) {
            if (!avisoEspera) {
                avisarLog(nombre + " esperando por falta de baterias en la Plataforma.");
                avisoEspera = true;
            }
            wait();
        }
        Bateria b = baterias.removeFirst();
        for (ObservadorVisor o : observadores) {
            o.naveConsumioBateria(nombre);
        }
        notifyAll();
        return b;
    }

    private void avisarLog(String texto) {
        for (ObservadorVisor o : observadores) {
            o.agregarLog(texto);
        }
    }
}