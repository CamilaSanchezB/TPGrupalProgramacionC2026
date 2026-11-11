package persistencia;

import persistencia.dao.UsuarioDAO;
import persistencia.dto.UsuarioDTO;


public class ServicioUsuarios {

    private final UsuarioDAO dao;

    /**
     * <b>PRE</b>
     * - dao != null
     */
    public ServicioUsuarios(UsuarioDAO dao) {
        assert dao != null : "El DAO no puede ser nulo.";
        this.dao = dao;
    }

    /**
     * Da de alta un usuario guardando el hash de su contraseña.
     *
     * <b>POST</b>
     * - el usuario queda en la base con su id asignado
     * - en la columna contrasena queda el hash
     *
     * @param contrasena texto plano; es char[] porque es lo que devuelve
     *                   JPasswordField.getPassword()
     * @return el usuario creado, con su id
     * @throws UsuarioInvalidoException    si falta el nombre o la contraseña
     * @throws NombreUsuarioEnUsoException si el nombre ya está tomado
     * @throws PersistenciaException       si falla la base
     */
    public UsuarioDTO registrar(String nombreUsuario, char[] contrasena)
            throws UsuarioException, PersistenciaException {
        String nombre = nombreUsuario == null ? "" : nombreUsuario.trim();
        if (nombre.isEmpty()) {
            throw new UsuarioInvalidoException("El nombre de usuario no puede estar vacio.");
        }
        if (contrasena == null || contrasena.length == 0) {
            throw new UsuarioInvalidoException("La contrasena no puede estar vacia.");
        }
        if (dao.buscarPorNombreUsuario(nombre) != null) {
            throw new NombreUsuarioEnUsoException(nombre);
        }
        return dao.guardar(new UsuarioDTO(nombre, HasheadorContrasenas.hashear(contrasena)));
    }

    /**
     * Verifica las credenciales de ingreso.
     *
     * Devuelve null tanto si el usuario no existe como si la contraseña está
     * mal
     * <b>POST</b>
     * - no modifica nada
     *
     * @return el usuario si las credenciales son correctas, null si no
     * @throws PersistenciaException si falla la base
     */
    public UsuarioDTO autenticar(String nombreUsuario, char[] contrasena)
            throws PersistenciaException {
        String nombre = nombreUsuario == null ? "" : nombreUsuario.trim();
        if (nombre.isEmpty() || contrasena == null || contrasena.length == 0) {
            return null;
        }
        UsuarioDTO usuario = dao.buscarPorNombreUsuario(nombre);
        if (usuario == null) {
            return null;
        }
        if (!HasheadorContrasenas.coincide(contrasena, usuario.getContrasena())) {
            return null;
        }
        return usuario;
    }
}
