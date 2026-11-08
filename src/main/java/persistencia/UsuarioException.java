package persistencia;

/**
 * Raíz de los rechazos de ServicioUsuarios.
 *
 * Existe para que quien llame al servicio pueda atrapar
 * cualquier motivo por el que un alta o un ingreso no prosperó
 *
 */
public class UsuarioException extends Exception {

    public UsuarioException(String motivo) {
        super(motivo);
    }
}
