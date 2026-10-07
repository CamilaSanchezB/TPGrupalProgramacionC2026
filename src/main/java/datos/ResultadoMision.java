package datos;
/**
 * Resultado final de una misión:
 * EXITOSA (cumplió su objetivo y la nave saltó), RECHAZADA (no se pudo preparar,
 * la nave no fue modificada) o FALLIDA (se ejecutó pero no cumplió su objetivo
 * o no pudo realizarse el salto).
 */
public enum ResultadoMision {
    EXITOSA, RECHAZADA, FALLIDA
}
