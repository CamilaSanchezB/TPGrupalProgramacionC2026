package negocio.nave;

public class NaveCarguero extends Nave{
	public NaveCarguero(int combustibleMax, int energiaMax, int desgaste) {
		super(combustibleMax, energiaMax, desgaste);
	}

	@Override
	public void ejecutarMision() {
		System.out.println("Transportando carga.");
	}
}
