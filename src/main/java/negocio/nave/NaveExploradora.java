package main.java.negocio.nave;

public class NaveExploradora extends Nave{
	public NaveExploradora(int combustibleIni, int energiaIni, int desgaste) {
		super(combustibleIni, energiaIni, desgaste);
	}

	@Override
	public void ejecutarMision() {
		System.out.println("Explorando el espacio.");
	}
}
