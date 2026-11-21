package model.Concurrencia;

/**
 * observer: lo implementa quien quiera enterarse de lo que pasa en el universo paralelo
 * (en este tp, la VistaNaves). El modelo solo conoce esta interfaz, nunca la vista concreta.
 */
public interface ObservadorVisor {
    void agregarNaveGeneradora(String nombre);
    void agregarNaveConsumidora(String nombre);
    void naveGeneroBateria(String nombre);
    void naveConsumioBateria(String nombre);
    void agregarLog(String texto);
}