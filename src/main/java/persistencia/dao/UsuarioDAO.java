package persistencia.dao;

import java.util.List;

import persistencia.PersistenciaException;
import persistencia.dto.UsuarioDTO;

public interface UsuarioDAO {

    /**
     * Guarda un usuario nuevo y le asigna su id.
     *
     * @return el mismo usuario con el id que generó la base
     * @throws PersistenciaException si el nombre de usuario ya existe o falla la base
     */
    UsuarioDTO guardar(UsuarioDTO usuario) throws PersistenciaException;

    /** @return el usuario con ese id, o null si no existe. */
    UsuarioDTO buscarPorId(int id) throws PersistenciaException;

    /** @return el usuario con ese nombre, o null si no existe. */
    UsuarioDTO buscarPorNombreUsuario(String nombreUsuario) throws PersistenciaException;

    List<UsuarioDTO> listarTodos() throws PersistenciaException;

    /** @return true si existía un usuario con ese id y se actualizó. */
    boolean actualizar(UsuarioDTO usuario) throws PersistenciaException;

    /** @return true si existía un usuario con ese id y se eliminó. */
    boolean eliminar(int id) throws PersistenciaException;
}
