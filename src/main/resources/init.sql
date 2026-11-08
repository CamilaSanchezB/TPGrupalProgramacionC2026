-- Esquema de la base SQLite de usuarios.
-- Lo ejecuta ConexionSQLite automáticamente la primera vez, cuando la tabla
-- "usuarios" todavía no existe.

CREATE TABLE usuarios (
    id             INTEGER      PRIMARY KEY AUTOINCREMENT,
    nombre_usuario VARCHAR(50)  NOT NULL UNIQUE,
    contrasena     VARCHAR(255) NOT NULL
);
