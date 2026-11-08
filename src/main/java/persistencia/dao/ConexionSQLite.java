package persistencia.dao;

import persistencia.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a la base SQLite.
 *
 * Solo abre conexiones: no crea ni modifica el esquema. La base se crea y se
 * mantiene aparte, con sqlite3 y el script sql/init.sql.
 */
public class ConexionSQLite {

    /** Archivo de la base por defecto, relativo al directorio de trabajo. */
    public static final String ARCHIVO_POR_DEFECTO = "src/main/resources/usuarios.db";

    private final String url;

    public ConexionSQLite() {
        this(ARCHIVO_POR_DEFECTO);
    }

    /**
     * <b>PRE</b>
     * - archivo != null y no vacío
     * <b>POST</b>
     * - queda apuntando a ese archivo
     *
     * @param archivo ruta del archivo .db
     */
    public ConexionSQLite(String archivo) {
        assert archivo != null && !archivo.trim().isEmpty()
                : "El archivo de la base no puede estar vacio.";
        this.url = "jdbc:sqlite:" + archivo;
    }

    /** @return la URL JDBC a la que apunta esta conexión. */
    public String getUrl() {
        return url;
    }

    /**
     * Abre una conexión nueva
     *
     * @throws PersistenciaException si no se puede conectar
     */
    public Connection abrir() throws PersistenciaException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a la base " + url, e);
        }
    }
}
