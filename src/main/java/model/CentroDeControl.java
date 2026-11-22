package model;
import java.util.LinkedHashMap;
import java.util.Map;
import controller.AsistenteDeComando;

/**
 * Punto de entrada del sistema: registra naves listas para operar (a través de su asistente)
 * y devuelve cualquiera cuando se la pide.
 * No construye asistentes ni naves (DIP): recibe objetos ya armados, así una nueva variante
 * de asistente (subclase de AsistenteDeComando) se registra sin cambiar esta clase.
 *
 * INV: ningún id ni asistente registrado es null; no hay ids repetidos
 */
public class CentroDeControl {
    private final Map<String, AsistenteDeComando> asistentes = new LinkedHashMap<String, AsistenteDeComando>();

    /**
     * PRE: id != null y no vacío, asistente != null, id no registrado
     * POST: el asistente queda registrado bajo ese id
     * @throws IllegalArgumentException si se viola la PRE
     */
    public void registrar(String id, AsistenteDeComando asistente) {
        if (id == null || id.trim().isEmpty() || asistente == null)
            throw new IllegalArgumentException("Id y asistente son obligatorios");
        if (asistentes.containsKey(id))
            throw new IllegalArgumentException("Ya hay una nave registrada con id " + id);
        asistentes.put(id, asistente);
    }

    /** @return el asistente registrado con ese id; null si no existe */
    public AsistenteDeComando obtener(String id) {
        return asistentes.get(id);
    }
}