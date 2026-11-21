package model.Mision;

import java.util.Random;

import controller.AsistenteDeComando;
import model.Requisitos;
import model.Coordenada;
import model.Universo.UniversoDinamico;

/**
 * M-02 Recolección.
 *
 * INV: sus requisitos son fijos: 4 de combustible, 10 de energía y 4 de desgaste (Anexo)
 * INV: elementoObtenido es true solo si la nave llegó al destino
 * INV: las gemas se entregan una sola vez, solo si la misión resultó EXITOSA,
 *      y son entre GEMAS_MIN y GEMAS_MAX
 * INV: cumple además todos los invariantes de Mision
 */
public class MisionRecoleccion extends Mision {
    public static final int GEMAS_MIN = 20;
    public static final int GEMAS_MAX = 100;
    private final Random azar = new Random();
    private boolean elementoObtenido = false;

    /**
     * PRE: asistenteDeComando, universo, origen y destino != null (lo verifica Mision)
     * POST: misión en etapa CREADA, con elementoObtenido == false
     */
    public MisionRecoleccion(AsistenteDeComando asistenteDeComando, UniversoDinamico universo,
                             Coordenada origen, Coordenada destino) {
        super("M-02 Recoleccion", asistenteDeComando, universo, origen, destino, new Requisitos(4, 10, 4));
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: elementoObtenido == true si la nave llegó al destino (el Anexo no exige desgaste bajo);
     *       la acción queda registrada; no modifica la nave
     */
    @Override
    protected void cumplirObjetivo() {
        if (getNavegador() != null && getNavegador().haLlegado()) {
            registrarAccion("Objetivo alcanzado y recoleccion realizada");
            elementoObtenido = true;
        }
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: true si y solo si se obtuvo el elemento y la nave llegó; no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo() {
        return elementoObtenido && getNavegador() != null && getNavegador().haLlegado();
    }

    /**
     * PRE: la misión resultó EXITOSA
     * POST: las gemas de la nave suben entre GEMAS_MIN y GEMAS_MAX
     */
    @Override
    protected void aplicarEfectosFinales() {
        getAsistenteDeComando().agregarGemas(GEMAS_MIN + azar.nextInt(GEMAS_MAX - GEMAS_MIN + 1));
    }
}