package persistencia;

/**
 * Los datos del usuario no alcanzan para darlo de alta: falta el nombre o falta
 * la contraseña.
 */
@SuppressWarnings("serial")
public class UsuarioInvalidoException extends UsuarioException {

    public UsuarioInvalidoException(String motivo) {
        super(motivo);
    }
}
