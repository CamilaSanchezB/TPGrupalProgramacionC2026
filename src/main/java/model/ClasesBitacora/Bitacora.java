package model.ClasesBitacora;
import java.util.ArrayList;
import java.util.Observable;

public class Bitacora extends Observable{
    private final ArrayList<RegitroBitacora> eventos;

    public Bitacora() {
        this.eventos = new ArrayList<>();
    }

    /**
     * @param descripcion Describe el evento que se registará en la bitacora"
     * PRE: el evento no puede ser nulo o vacio
     * POST: el evento quedará registrado con su descripcion y la fechg en la que fue hecho el registro
     */
    public synchronized void registrar(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty())
            throw new IllegalArgumentException("No se ha ingresado ningun evento");
        this.eventos.add(new RegitroBitacora(descripcion));
        setChanged();
        notifyObservers();
    }

    public synchronized ArrayList<RegitroBitacora> getEventos() {
        return new ArrayList<RegitroBitacora>(eventos);
    }
}
