package negocio;

public class MisionRetorno extends Mision {

    public MisionRetorno(AsistenteDeComando asistente) {
        super("M-03 Retorno seguro", asistente, new Requisitos(4, 0, 4));
    }

    @Override
    protected void cumplirObjetivo() {
        registrarAccion("Regreso a la zona designada completado");
    }

    @Override
    protected boolean cumplioObjetivo() {
        return naveOperativa();
    }
}
