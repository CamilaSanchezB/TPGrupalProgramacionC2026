package model.nave;

public class NaveCombate extends Nave {
    protected NaveCombate(int combustibleIni, int energiaIni, int desgaste) {
        super(combustibleIni, energiaIni, desgaste);
    }

    @Override
    public ResultadoBatalla batallar(Nave enemiga) {
        exigirEnemiga(enemiga);
        return enemiga.recibirBatallaDe(this);
    }

    // Yo soy la enemiga (Combate):
    @Override
    protected ResultadoBatalla recibirBatallaDe(NaveExploradora propia) { return ResultadoBatalla.PIERDE; }
    @Override
    protected ResultadoBatalla recibirBatallaDe(NaveCarguero propia)    { return ResultadoBatalla.GANA; }
    @Override
    protected ResultadoBatalla recibirBatallaDe(NaveCombate propia)     { return ResultadoBatalla.PACTO_DE_PAZ; }
}