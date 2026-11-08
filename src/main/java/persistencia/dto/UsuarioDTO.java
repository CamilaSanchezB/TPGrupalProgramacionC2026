package persistencia.dto;

/**
 * Datos de un usuario tal como viajan entre la capa de persistencia y el resto
 * de la aplicación.
 *
 * Es un objeto de transporte: no valida reglas de negocio ni sabe de SQL.
 * La contraseña que lleva es SIEMPRE el hash, nunca el texto plano: hashear es
 * responsabilidad de la capa que está por encima del DAO.
 */
public class UsuarioDTO {

    /** 0 mientras el usuario no fue guardado; la base asigna el id real. */
    private int id;
    private String nombreUsuario;
    private String contrasena;

    public UsuarioDTO() {
    }

    /**
     * @param nombreUsuario nombre con el que inicia sesión
     * @param contrasena    hash de la contraseña, no el texto plano
     */
    public UsuarioDTO(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /** @return el hash de la contraseña. */
    public String getContrasena() {
        return contrasena;
    }

    /** @param contrasena el hash de la contraseña, no el texto plano. */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /** No incluye la contraseña, para no filtrar el hash en logs ni mensajes. */
    @Override
    public String toString() {
        return "UsuarioDTO{id=" + id + ", nombreUsuario='" + nombreUsuario + "'}";
    }
}
