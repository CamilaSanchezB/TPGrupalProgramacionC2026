package negocio.nave;

public class NaveExploradora extends Nave{
	public NaveExploradora(int combustibleMax, int energiaMax, int desgaste) {
		super(combustibleMax, energiaMax, desgaste);
	}

	@Override
	public void ejecutarMision() {
		System.out.println("Explorando el espacio.");
	}
}
