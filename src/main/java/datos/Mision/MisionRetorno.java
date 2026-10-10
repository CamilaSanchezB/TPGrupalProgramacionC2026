package datos.Mision;

import datos.Requisitos;
import negocio.AsistenteDeComando;

/**
 * M-03 Retorno seguro.
 *
 * INV: sus requisitos son fijos: 4 de combustible, 0 de energía y 4 de desgaste
 * INV: no guarda estado propio: su éxito depende solo del estado de la nave
 * INV: cumple además todos los invariantes de Mision
 */
public class MisionRetorno extends Mision {
    /**
     * @param asistente Asistente al que se le encomienda la misión
     * PRE: asistente != null (lo verifica el constructor de Mision)
     * POST: misión en etapa CREADA
     */
    public MisionRetorno(AsistenteDeComando asistente) {
        super("M-03 Retorno seguro", asistente, new Requisitos(4, 0, 4));
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: la acción de regreso queda registrada en la misión
     */
    @Override
    protected void cumplirObjetivo() {
        registrarAccion("Regreso a la zona designada completado");
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: devuelve true si y solo si la nave finalizó en estado operativo válido
     *       (no requiere mantenimiento, es decir desgaste < 80); no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo() {
        return naveOperativa();
    }
}
