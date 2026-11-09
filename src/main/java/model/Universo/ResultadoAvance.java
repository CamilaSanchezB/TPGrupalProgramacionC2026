package model.Universo;

/**
 * Qué pasó en un turno de navegación.
 * SALTO: avanzó una casilla; SALTO_WARP: avanzó varias de golpe;
 * SIN_RUTA: no encontró camino y se quedó en su lugar (el tablero cambia, puede reintentarse).
 */
public enum ResultadoAvance {
    SALTO, SALTO_WARP, SIN_RUTA
}