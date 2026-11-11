package model.Mision;

import controller.AsistenteDeComando;
import model.Coordenada;
import model.Requisitos;
import model.Universo.Universo;

/**
 * M-01 Intercepción y asistencia.
 *
 * INV: sus requisitos son fijos: 4 de combustible, 5 de energía y 4 de desgaste
 * INV: asistenciaCompletada es false hasta que se ejecuta cumplirObjetivo()
 *      y no vuelve a false después
 * INV: cumple además todos los invariantes de Mision
 */
public class MisionIntercepcion extends Mision{
    private boolean asistenciaCompletada=false;
    private final int energiaATransferir;
    private final int combustibleATransferir;
    /**
     * @param asistenteDeComando Asistente al que se le encomienda la misión
     * PRE: asistenteDeComando != null (lo verifica el constructor de Mision)
     * POST: misión en etapa CREADA, con asistenciaCompletada == false
     */
    public MisionIntercepcion(AsistenteDeComando asistenteDeComando,Universo universo, Coordenada origen, Coordenada destino, int energiaATransferir,int combustibleATransferir){
        super("M-01 Intercepcion y asistencia",asistenteDeComando,universo,origen,destino,new Requisitos(4,5,4));
        this.energiaATransferir=energiaATransferir;
        this.combustibleATransferir=combustibleATransferir;
    }

    @Override
    protected void verificarEnDestino(){
        AsistenteDeComando asistente = getAsistenteDeComando();
        if(asistente.getEnergia()< energiaATransferir || asistente.getCombustible()< combustibleATransferir){
            asistenciaCompletada=false;
            registrarAccion("Fallo en destino: Recursos insuficientes para auxilio");
        }
        else{
            asistenciaCompletada=true;
        }
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: asistenciaCompletada == true y la acción queda registrada en la misión
     */

    @Override
    protected void cumplirObjetivo(){
        if(asistenciaCompletada && getNavegador()!=null && getNavegador().haLlegado()){
            registrarAccion("Objetivo alcanzado y asistencia validada");
        }
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: devuelve true si y solo si la asistencia se completó; no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo(){
        return asistenciaCompletada && getNavegador()!=null && getNavegador().haLlegado();
    }

    @Override
    protected void aplicarEfectosFinales(){
        getAsistenteDeComando().consumir(combustibleATransferir,energiaATransferir,0);
        registrarAccion("Asistencia entregada: -" + combustibleATransferir + " comb, -" + energiaATransferir + " energ.");
    }
}
