package model.Mision;

import controller.AsistenteDeComando;
import model.Requisitos;
import model.Universo.UniversoDinamico;
import model.Coordenada;

/**
 * M-03 Retorno seguro.
 *
 * INV: sus requisitos son fijos: 4 de combustible, 0 de energía y 4 de desgaste
 * INV: no guarda estado propio: su éxito depende solo de haber llegado a la base con la nave viva
 * INV: la recompensa (recarga total + mantenimiento) se aplica una sola vez y solo si fue EXITOSA
 * INV: cumple además todos los invariantes de Mision
 */
public class MisionRetorno extends Mision {

    /**
     * PRE: asistente, universo, origen y destinoBase != null (lo verifica Mision)
     * POST: misión en etapa CREADA
     */
    public MisionRetorno(AsistenteDeComando asistente, UniversoDinamico universo,
                         Coordenada origen, Coordenada destinoBase) {
        super("M-03 Retorno seguro", asistente, universo, origen, destinoBase, new Requisitos(4, 0, 4));
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: si la nave llegó, el regreso queda registrado; no modifica la nave
     */
    @Override
    protected void cumplirObjetivo() {
        if (getNavegador() != null && getNavegador().haLlegado()) {
            registrarAccion("Regreso a la zona designada completado");
        }
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: true si y solo si la nave llegó a la base (Mision.evaluar() ya descarta la destruida);
     *       el desgaste alto no impide el éxito: justamente se repara al llegar; no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo() {
        return getNavegador() != null && getNavegador().haLlegado();
    }

    /**
     * PRE: la misión resultó EXITOSA
     * POST: combustible == energía == 100 y desgaste == 0
     */
    @Override
    protected void aplicarEfectosFinales() {
        getAsistenteDeComando().recargarTotal();
        getAsistenteDeComando().realizarMantenimiento();
        registrarAccion("Mantenimiento de base completado al retornar");
    }
}