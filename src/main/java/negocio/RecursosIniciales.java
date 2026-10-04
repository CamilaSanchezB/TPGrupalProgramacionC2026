package negocio;
//Estado de los recursos iniciales antes de la ejecucion de la mision.
public class RecursosIniciales {
    private final int combustible;
    private final int energia;
    private final int desgaste;

    public RecursosIniciales(int combustible,int energia, int desgaste){
        this.combustible=combustible;
        this.energia=energia;
        this.desgaste=desgaste;
    }

    public int getCombustible() { return combustible; }
    public int getEnergia()     { return energia; }
    public int getDesgaste()    { return desgaste; }
}
