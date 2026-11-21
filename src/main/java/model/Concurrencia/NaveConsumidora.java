package model.Concurrencia;

import java.util.Random;

/** Consumidor: retira y consume una bateria por ciclo. */
public class NaveConsumidora implements Runnable {

    private final String nombre;
    private final PlataFormaDeCarga plataforma;
    private final Random random = new Random();

    public NaveConsumidora(String nombre, PlataFormaDeCarga plataforma) {
        this.nombre = nombre;
        this.plataforma = plataforma;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                plataforma.retirar(nombre);
                Thread.sleep(3000 + random.nextInt(2000)); // tiempo de consumo
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}