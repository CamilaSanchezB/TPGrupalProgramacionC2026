package persistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persistencia.PersistenciaException;
import persistencia.dto.UsuarioDTO;

/**
 * Implementación del DAO de usuarios sobre SQLite usando JDBC.
 *
 * Las SQLException no salen de acá: se envuelven en PersistenciaException para
 * que quien use el DAO no dependa de JDBC. No crea ni modifica el esquema; la
 * base se prepara aparte con sqlite3 y el script init.sql.
 */
public class UsuarioDAOSqlite implements UsuarioDAO {

    private static final String COLUMNAS = "id, nombre_usuario, contrasena";

    private final ConexionSQLite conexion;

    /** Usa el archivo de base por defecto de ConexionSQLite. */
    public UsuarioDAOSqlite() {
        this(new ConexionSQLite());
    }

    /**
     * <b>PRE</b>
     * - conexion != null
     */
    public UsuarioDAOSqlite(ConexionSQLite conexion) {
        assert conexion != null : "La conexion no puede ser nula.";
        this.conexion = conexion;
    }

    @Override
    public UsuarioDTO guardar(UsuarioDTO usuario) throws PersistenciaException {
        assert usuario != null : "El usuario a guardar no puede ser nulo.";
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena) VALUES (?, ?)";
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasena());
            ps.executeUpdate();

            // El id lo genera la base (AUTOINCREMENT)
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    usuario.setId(claves.getInt(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo guardar el usuario " + usuario.getNombreUsuario(), e);
        }
    }

    @Override
    public UsuarioDTO buscarPorId(int id) throws PersistenciaException {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios WHERE id = ?";
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo buscar el usuario " + id, e);
        }
    }

    @Override
    public UsuarioDTO buscarPorNombreUsuario(String nombreUsuario) throws PersistenciaException {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios WHERE nombre_usuario = ?";
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo buscar el usuario " + nombreUsuario, e);
        }
    }

    @Override
    public List<UsuarioDTO> listarTodos() throws PersistenciaException {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios ORDER BY id";
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<UsuarioDTO> usuarios = new ArrayList<UsuarioDTO>();
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron listar los usuarios", e);
        }
    }

    @Override
    public boolean actualizar(UsuarioDTO usuario) throws PersistenciaException {
        assert usuario != null : "El usuario a actualizar no puede ser nulo.";
        String sql = "UPDATE usuarios SET nombre_usuario = ?, contrasena = ? WHERE id = ?";
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getContrasena());
            ps.setInt(3, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo actualizar el usuario " + usuario.getId(), e);
        }
    }

    @Override
    public boolean eliminar(int id) throws PersistenciaException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el usuario " + id, e);
        }
    }

    // ---------- Mapeo fila -> objeto ----------

    private UsuarioDTO mapear(ResultSet rs) throws SQLException {
        UsuarioDTO usuario = new UsuarioDTO(
                rs.getString("nombre_usuario"),
                rs.getString("contrasena"));
        usuario.setId(rs.getInt("id"));
        return usuario;
    }
}
