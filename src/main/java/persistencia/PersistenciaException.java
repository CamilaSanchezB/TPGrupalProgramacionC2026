package persistencia;

/**
 * Excepción checked que usan todas las clases de persistencia.
 *
 * Envuelve la causa técnica (SQLException, IOException, ...) para que quien
 * usa el DAO no dependa del mecanismo de persistencia concreto.
 */
@SuppressWarnings("serial")
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensaje, Exception causa) {
        super(mensaje, causa);
    }

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
