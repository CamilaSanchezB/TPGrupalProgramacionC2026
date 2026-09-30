package negocio.nave;

public class NaveCarguero extends Nave{
	public NaveCarguero(int combustibleMax, int energiaMax) {
		super(combustibleMax, energiaMax);
	}

	@Override
	public void ejecutarMision() {
		System.out.println("Transportando carga.");
	}
}
