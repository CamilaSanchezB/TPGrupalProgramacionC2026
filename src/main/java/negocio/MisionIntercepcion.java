package negocio;

public class MisionIntercepcion extends Mision{
    private boolean asistenciaCompletada=false;

    public MisionIntercepcion(AsistenteDeComando asistenteDeComando){
        super("M-01 Intercepcion y asistencia",asistenteDeComando,new Requisitos(4,5,4));
    }

    @Override
    protected void cumplirObjetivo(){
        registrarAccion("Objetivo alcanzado y asistencia realizada");
        asistenciaCompletada= true;
    }

    @Override
    protected boolean cumplioObjetivo(){
        return asistenciaCompletada;
    }
}
