package negocio.nave;

public class NaveCombate extends Nave {
    public NaveCombate(int combustibleMax, int energiaMax, int desgaste) {
        super(combustibleMax, energiaMax, desgaste);
    }

    @Override
    public void ejecutarMision() {
        System.out.println("Ejecutando misión de combate.");
    }
}