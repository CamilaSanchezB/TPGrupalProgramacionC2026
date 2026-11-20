package view;
import java.util.Random;

import javax.swing.SwingUtilities;

/**
 * Demo para probar la vista sin tu codigo MVC: cada nave es un hilo y la
 * plataforma es un buffer con capacidad limitada (productor/consumidor).
 * No hace falta incluir este archivo en tu TP.
 */
public class DemoVistaNaves {

    private static final int CAPACIDAD = 10;

    private static final Object lock = new Object();
    private static int baterias = 0;
    private static int contadorGeneradoras = 0;
    private static int contadorConsumidoras = 0;
    private static final Random random = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaNaves vista = new VistaNaves();

            vista.addAgregarGeneradoraListener(e -> {
                String nombre = "Generadora-" + (++contadorGeneradoras);
                vista.agregarNaveGeneradora(nombre);
                iniciarHilo(() -> cicloGeneradora(vista, nombre));
            });

            vista.addAgregarConsumidoraListener(e -> {
                String nombre = "Consumidora-" + (++contadorConsumidoras);
                vista.agregarNaveConsumidora(nombre);
                iniciarHilo(() -> cicloConsumidora(vista, nombre));
            });

            vista.addVolverListener(e -> vista.agregarLog("(Volver) aca el controlador cambia de pantalla"));

            vista.setVisible(true);
        });
    }

    private static void cicloGeneradora(VistaNaves vista, String nombre) {
        try {
            while (true) {
                Thread.sleep(600 + random.nextInt(1200));
                synchronized (lock) {
                    while (baterias == CAPACIDAD) {
                        lock.wait();
                    }
                    baterias++;
                    // Se avisa a la vista DENTRO de la seccion critica para que
                    // el orden de los eventos coincida con el del modelo.
                    vista.naveGeneroBateria(nombre);
                    lock.notifyAll();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static void cicloConsumidora(VistaNaves vista, String nombre) {
        try {
            while (true) {
                Thread.sleep(900 + random.nextInt(1500));
                synchronized (lock) {
                    while (baterias == 0) {
                        lock.wait();
                    }
                    baterias--;
                    vista.naveConsumioBateria(nombre);
                    lock.notifyAll();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static void iniciarHilo(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }
}
