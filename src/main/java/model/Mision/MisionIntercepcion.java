package model.Mision;

import controller.AsistenteDeComando;
import model.Coordenada;
import model.Requisitos;
import model.Universo.UniversoDinamico;

/**
 * M-01 Intercepción y asistencia.
 *
 * INV: sus requisitos son fijos: 4 de combustible, 5 de energía y 4 de desgaste
 * INV: 0 <= energiaATransferir <= 50 y 0 <= combustibleATransferir <= 50 (Anexo) y no cambian
 * INV: asistenciaCompletada es true solo si en destino la nave tenía MÁS recursos que el auxilio
 *      pedido (así la entrega nunca la deja destruida); lo fija verificarEnDestino()
 * INV: cumple además todos los invariantes de Mision
 */
public class MisionIntercepcion extends Mision {
    public static final int AUXILIO_MAXIMO = 50;
    private boolean asistenciaCompletada = false;
    private final int energiaATransferir;
    private final int combustibleATransferir;

    /**
     * PRE: asistenteDeComando, universo, origen y destino != null (lo verifica Mision);
     *      0 <= energiaATransferir, combustibleATransferir <= AUXILIO_MAXIMO
     * POST: misión en etapa CREADA, con asistenciaCompletada == false
     * @throws IllegalArgumentException si se viola la PRE
     */
    public MisionIntercepcion(AsistenteDeComando asistenteDeComando, UniversoDinamico universo,
                              Coordenada origen, Coordenada destino,
                              int energiaATransferir, int combustibleATransferir) {
        super("M-01 Intercepcion y asistencia", asistenteDeComando, universo, origen, destino,
                new Requisitos(4, 5, 4));
        if (energiaATransferir < 0 || energiaATransferir > AUXILIO_MAXIMO
                || combustibleATransferir < 0 || combustibleATransferir > AUXILIO_MAXIMO)
            throw new IllegalArgumentException("El auxilio debe estar entre 0 y " + AUXILIO_MAXIMO);
        this.energiaATransferir = energiaATransferir;
        this.combustibleATransferir = combustibleATransferir;
    }

    /**
     * PRE: la nave llegó al destino y no está destruida
     * POST: asistenciaCompletada == true si y solo si energía > energiaATransferir y
     *       combustible > combustibleATransferir (estrictamente, para no quedar en 0);
     *       si no alcanza, queda registrado; no modifica la nave
     */
    @Override
    protected void verificarEnDestino() {
        AsistenteDeComando asistente = getAsistenteDeComando();
        if (asistente.getEnergia() <= energiaATransferir || asistente.getCombustible() <= combustibleATransferir) {
            asistenciaCompletada = false;
            registrarAccion("Fallo en destino: Recursos insuficientes para auxilio");
        } else {
            asistenciaCompletada = true;
        }
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: si la asistencia es posible y la nave llegó, queda registrada; no modifica la nave
     */
    @Override
    protected void cumplirObjetivo() {
        if (asistenciaCompletada && getNavegador() != null && getNavegador().haLlegado()) {
            registrarAccion("Objetivo alcanzado y asistencia validada");
        }
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: true si y solo si la asistencia se completó y la nave llegó; no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo() {
        return asistenciaCompletada && getNavegador() != null && getNavegador().haLlegado();
    }

    /**
     * PRE: la misión resultó EXITOSA (asistenciaCompletada == true)
     * POST: combustible y energía de la nave bajan lo transferido y la entrega queda registrada
     *       (si el consumo falla, Mision.cerrar() marca la misión FALLIDA y genera el informe)
     */
    @Override
    protected void aplicarEfectosFinales() {
        getAsistenteDeComando().consumir(combustibleATransferir, energiaATransferir, 0);
        registrarAccion("Asistencia entregada: -" + combustibleATransferir + " comb, -" + energiaATransferir + " energ.");
    }
}