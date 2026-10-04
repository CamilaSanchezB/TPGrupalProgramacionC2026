package negocio;

public class MisionRecoleccion extends Mision{
    private boolean elementoObtenido=false;

    public MisionRecoleccion(AsistenteDeComando asistenteDeComando){
        super("M-02 Recoleccion",asistenteDeComando,new Requisitos(4,5,4));
    }

    @Override
    protected void cumplirObjetivo(){
        registrarAccion("Objetivo alcanzado y recoleccion realizada");
        elementoObtenido= true;
    }

    @Override
    protected boolean cumplioObjetivo(){
        return elementoObtenido;
    }
}
