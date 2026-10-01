package negocio.nave;
import java.util.ArrayList;
import datos.tripulantes.Tripulante;
import negocio.ClasesMotorWarp.MotorWarp;

public abstract class Nave {
    private ArrayList<Tripulante> tripulantes;
    private MotorWarp motorWarp;
    protected int combustible;
    protected int energia;
    protected int desgaste;

    public Nave(int combustibleMax, int energiaMax, int desgaste) {
        this.combustible = combustibleMax;
        this.energia = energiaMax;
        this.desgaste = desgaste;
    }

    public abstract void ejecutarMision();
}