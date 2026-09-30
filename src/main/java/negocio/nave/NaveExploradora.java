package negocio.nave;

public class NaveExploradora extends Nave{
	public NaveExploradora(int combustibleMax, int energiaMax) {
		super(combustibleMax, energiaMax);
	}

	@Override
	public void ejecutarMision() {
		System.out.println("Explorando el espacio.");
	}
}
