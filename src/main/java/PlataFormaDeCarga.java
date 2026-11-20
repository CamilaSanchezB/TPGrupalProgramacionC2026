/**
 * Plataforma de carga compartida entre las naves.
 *
 * Es el recurso compartido del problema productor-consumidor:
 *  - Las naves productoras depositan recursos (se bloquean si no hay lugar).
 *  - Las naves consumidoras retiran recursos (se bloquean si no hay stock suficiente).
 *
 * Toda la sincronizacion vive aca adentro (monitor de la propia plataforma), asi las
 * naves no necesitan conocer nada del locking. Esta clase es del MODELO: no imprime
 * nada ni conoce la vista.
 */
import java.util.ArrayList;

public class PlataFormaDeCarga {
	
	private ArrayList<Bateria> baterias = new ArrayList<>();
	private int maxBaterias = 3;
	
	public synchronized void depositar(Bateria nueva) throws InterruptedException{
		while(baterias.size() == maxBaterias) {
			System.out.println(nombreHilo() + " esperando a q se libere la plataforma");
			wait();
		}
		baterias.add(nueva);
		System.out.println(nombreHilo() + " deposito una bateria");
		notifyAll();
		
	}
	
	public synchronized void sacar() throws InterruptedException {
		while(baterias.size() == 0 ) {
			System.out.println(nombreHilo() + " esperando una bateria ");
			wait();
		}
		Bateria sacado = baterias.remove(0);
		System.out.println(nombreHilo() + " consumio una bateria");
		notifyAll();
	}

	
	
	private String nombreHilo() {
		return Thread.currentThread().getName();
	}
}