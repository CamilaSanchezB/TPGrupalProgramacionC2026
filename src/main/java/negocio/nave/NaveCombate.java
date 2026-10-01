package main.java.negocio.nave;

public class NaveCombate extends Nave {
    protected NaveCombate(int combustibleIni, int energiaIni, int desgaste) {
        super(combustibleIni, energiaIni, desgaste);
    }

    @Override
    public void ejecutarMision() {
        System.out.println("Ejecutando misión de combate.");
    }
}