package negocio.nave;
import java.util.ArrayList;
import datos.tripulantes.Tripulante;

public abstract class |Nave {
    private ArrayList<Tripulante> tripulantes;
    protected int combustible;
    protected int energia;

    public Nave(int combustibleMax, int energiaMax) {
        this.combustible = combustibleMax;
        this.energia = energiaMax;
    }

    public abstract void ejecutarMision();
}