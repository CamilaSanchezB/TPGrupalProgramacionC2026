package datos.Mision;

import datos.Requisitos;
import negocio.AsistenteDeComando;

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

    /**
     * @param asistenteDeComando Asistente al que se le encomienda la misión
     * PRE: asistenteDeComando != null (lo verifica el constructor de Mision)
     * POST: misión en etapa CREADA, con asistenciaCompletada == false
     */
    public MisionIntercepcion(AsistenteDeComando asistenteDeComando){
        super("M-01 Intercepcion y asistencia",asistenteDeComando,new Requisitos(4,5,4));
    }

    /**
     * PRE: etapa == PREPARADA (se llama desde ejecutarAccion())
     * POST: asistenciaCompletada == true y la acción queda registrada en la misión
     */
    @Override
    protected void cumplirObjetivo(){
        registrarAccion("Objetivo alcanzado y asistencia realizada");
        asistenciaCompletada= true;
    }

    /**
     * PRE: etapa == EJECUTADA
     * POST: devuelve true si y solo si la asistencia se completó; no modifica nada
     */
    @Override
    protected boolean cumplioObjetivo(){
        return asistenciaCompletada;
    }
}
