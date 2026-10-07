package negocio;

/**
 * Etapas por las que pasa una misión, siempre en este orden:
 * CREADA -> PREPARADA -> EJECUTADA -> EVALUADA -> CERRADA.
 * Una misión rechazada pasa de CREADA directamente a CERRADA.
 * Solo la propia Mision modifica su etapa.
 */

public enum EtapaMision {
    CREADA, PREPARADA, EJECUTADA, EVALUADA, CERRADA
}
