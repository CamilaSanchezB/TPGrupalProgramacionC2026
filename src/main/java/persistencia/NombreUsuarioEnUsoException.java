package persistencia;

/**
 * Ya hay otro usuario registrado con ese nombre.
 *
 * Está separada de UsuarioInvalidoException porque la vista la trata distinto:
 * acá los datos están bien formados, lo que falla es que el nombre está tomado
 * y hay que pedir otro.
 */
@SuppressWarnings("serial")
public class NombreUsuarioEnUsoException extends UsuarioException {

    private final String nombreUsuario;

    public NombreUsuarioEnUsoException(String nombreUsuario) {
        super("Ya existe un usuario llamado " + nombreUsuario + ".");
        this.nombreUsuario = nombreUsuario;
    }

    /** @return el nombre que estaba tomado, por si la vista quiere resaltarlo. */
    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
