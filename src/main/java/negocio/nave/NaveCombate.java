package negocio.nave;

public class NaveCombate extends Nave {
    public NaveCombate(int combustibleMax, int energiaMax) {
        super(combustibleMax, energiaMax);
    }

    @Override
    public void ejecutarMision() {
        System.out.println("Ejecutando misión de combate.");
    }
}