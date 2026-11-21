package model.Concurrencia;

import java.util.Random;

/** Productor: produce una bateria por ciclo y la deposita en la plataforma. */
public class NaveGeneradora implements Runnable {

    private final String nombre;
    private final PlataFormaDeCarga plataforma;
    private final Random random = new Random();

    public NaveGeneradora(String nombre, PlataFormaDeCarga plataforma) {
        this.nombre = nombre;
        this.plataforma = plataforma;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(600 + random.nextInt(1200)); // tiempo de produccion
                plataforma.depositar(new Bateria(), nombre);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
