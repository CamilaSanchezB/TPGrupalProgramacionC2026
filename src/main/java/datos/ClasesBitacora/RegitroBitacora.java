package datos.ClasesBitacora;

import java.time.LocalDateTime;

public class RegitroBitacora {
    private final LocalDateTime tiempoActual;
    private final String descripcion;

    public RegitroBitacora(String descripcion) {
        this.tiempoActual = LocalDateTime.now();
        this.descripcion = descripcion;
    }

    public LocalDateTime getTiempoActual() {
        return tiempoActual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "[" + tiempoActual + "] " + descripcion;
    }
}
