package model.Mision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mantiene el registro histórico de los informes de todas las misiones ejecutadas.
 * INV: historial != null
 * INV: ningún elemento de historial es null
 */
public class GestorHistorialMisiones {
    private final List<InformeMision> historial;

    /**
     * POST: crea un historial de misiones vacío.
     */
    public GestorHistorialMisiones() {
        this.historial = new ArrayList<>();
    }

    /**
     * Agrega el informe de una misión finalizada al historial.
     *
     * @param informe Informe generado al cerrar la misión
     * PRE: informe != null
     * POST: el informe queda al final del historial
     */
    public void registrarInforme(InformeMision informe) {
        assert informe != null : "El informe a registrar no puede ser nulo";
        this.historial.add(informe);
    }

    /**
     * Devuelve una copia no modificable de todas las misiones registradas.
     * POST: es una consulta; devuelve la lista inmutable del historial sin modificar el estado.
     * @return lista de informes de misión acumulados
     */
    public List<InformeMision> getHistorial() {
        return Collections.unmodifiableList(this.historial);
    }

    /**
     * Limpia el historial acumulado.
     * POST: el historial queda vacío.
     */
    public void limpiar() {
        this.historial.clear();
    }
}