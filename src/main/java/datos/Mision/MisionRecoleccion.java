package datos.Mision;

import datos.Requisitos;
import negocio.AsistenteDeComando;

/**
 * M-02 Recolección.
 *
 * INV: sus requisitos son fijos: 4 de combustible, 5 de energía y 4 de desgaste
 * INV: elementoObtenido es false hasta que se ejecuta cumplirObjetivo()
 *      y no vuelve a false después
 * INV: cumple además todos los invariantes de Mision
 */
public class MisionRecoleccion extends Mision{
    private boolean elementoObtenido=false;

    /**
     * @param asistenteDeComando Asistente al que se le encomienda la misión
     * PRE: asistenteDeComando != null (lo verifica el constructor de Mision)
     * POST: misión en etapa CREADA, con elementoObtenido == false
     */
    public MisionRecoleccion(AsistenteDeComando asistenteDeComando){
        super("M-02 Recoleccion",asistenteDeComando,new Requisitos(4,5,4));
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: elementoObtenido == true y la acción queda registrada en la misión
     */
    @Override
    protected void cumplirObjetivo(){
        registrarAccion("Objetivo alcanzado y recoleccion realizada");
        elementoObtenido= true;
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: devuelve true si y solo si el elemento quedó registrado como obtenido;
     *       no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo(){
        return elementoObtenido;
    }
}
