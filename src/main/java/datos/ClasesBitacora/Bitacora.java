package datos.ClasesBitacora;
import java.util.ArrayList;
import java.util.List;

public class Bitacora {
    private final ArrayList<RegitroBitacora> eventos;

    public Bitacora() {
        this.eventos = new ArrayList<>();
    }

    /**
     * @param descripcion Describe el evento que se registará en la bitacora"
     * PRE: el evento no puede ser nulo o vacio
     * POST: el evento quedará registrado con su descripcion y la fechg en la que fue hecho el registro
     */
    public void registrar(String descripcion) {
        assert descripcion == null : "No se ha ingresado ningun evento";
        this.eventos.add(new RegitroBitacora(descripcion));
    }

    public ArrayList<RegitroBitacora> getEventos() {
        return eventos;
    }
}
