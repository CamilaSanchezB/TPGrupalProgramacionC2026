package model.Concurrencia;

import java.util.Random;

/** Productor: produce una bateria por ciclo y la deposita en la plataforma. */
public class NaveGeneradora implements Runnable {

    private final String nombre;
    private final PlataformaDeCarga plataforma;
    private final Random random = new Random();

    public NaveGeneradora(String nombre, PlataformaDeCarga plataforma) {
        this.nombre = nombre;
        this.plataforma = plataforma;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(3000 + random.nextInt(2000)); // tiempo de produccion
                plataforma.depositar(new Bateria(), nombre);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
