package model.Concurrencia;

/**
 * Observer: lo implementa quien quiera enterarse de lo que pasa en el universo paralelo.
 * El modelo solo conoce esta interfaz, nunca la vista concreta.
 */
public interface ObservadorVisor {
    void actualizar(EventoVisor evento);
}