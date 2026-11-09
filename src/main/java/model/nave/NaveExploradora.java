package model.nave;

public class NaveExploradora extends Nave{
	protected NaveExploradora(int combustibleIni, int energiaIni, int desgaste) {
		super(combustibleIni, energiaIni, desgaste);
	}

	@Override
	public ResultadoBatalla batallar(Nave enemiga) {
    	exigirEnemiga(enemiga);
    	return enemiga.recibirBatallaDe(this);
	}
	@Override
	protected ResultadoBatalla recibirBatallaDe(NaveExploradora propia) { return ResultadoBatalla.PACTO_DE_PAZ; }
	@Override
	protected ResultadoBatalla recibirBatallaDe(NaveCarguero propia)    { return ResultadoBatalla.PIERDE; }
	@Override
	protected ResultadoBatalla recibirBatallaDe(NaveCombate propia)     { return ResultadoBatalla.GANA; }
}
