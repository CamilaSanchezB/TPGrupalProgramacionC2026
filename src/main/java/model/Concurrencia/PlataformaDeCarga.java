package model.Concurrencia;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Recurso compartido (monitor) del problema productor-consumidor.
 * Los avisos se hacen DENTRO del método synchronized para que el orden de los eventos
 * coincida con el orden real de las operaciones (la vista los pasa al EDT).
 */
public class PlataformaDeCarga {

    private final LinkedList<Bateria> baterias = new LinkedList<>();
    private final int capacidad;
    private final List<ObservadorVisor> observadores = new CopyOnWriteArrayList<>();

    public PlataformaDeCarga(int capacidad) {
        this.capacidad = capacidad;
    }

    public void agregarObservador(ObservadorVisor o) {
        observadores.add(o);
    }

    public synchronized void depositar(Bateria nueva, String nombre) throws InterruptedException {
        avisar(EventoVisor.mensaje("Batería producida por " + nombre + "."));
        boolean avisoEspera = false;
        while (baterias.size() >= capacidad) {
            if (!avisoEspera) { // se avisa una sola vez, no en cada despertar
                avisar(EventoVisor.mensaje(nombre + " esperando a que se libere espacio en la Plataforma."));
                avisoEspera = true;
            }
            wait();
        }
        baterias.add(nueva);
        avisar(EventoVisor.bateriaDepositada(nombre));
        notifyAll();
    }

    public synchronized Bateria retirar(String nombre) throws InterruptedException {
        avisar(EventoVisor.mensaje(nombre + " solicitando batería."));
        boolean avisoEspera = false;
        while (baterias.isEmpty()) {
            if (!avisoEspera) {
                avisar(EventoVisor.mensaje(nombre + " esperando por falta de baterías en la Plataforma."));
                avisoEspera = true;
            }
            wait();
        }
        Bateria b = baterias.removeFirst();
        avisar(EventoVisor.bateriaRetirada(nombre));
        notifyAll();
        return b;
    }

    private void avisar(EventoVisor evento) {
        for (ObservadorVisor o : observadores) {
            o.actualizar(evento);
        }
    }
}