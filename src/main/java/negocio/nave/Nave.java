package negocio.nave;

public abstract class Nave {
    protected int combustible;
    protected int energia;

    public Nave(int combustibleMax, int energiaMax) {
        this.combustible = combustibleMax;
        this.energia = energiaMax;
    }

    public abstract void ejecutarMision();
}