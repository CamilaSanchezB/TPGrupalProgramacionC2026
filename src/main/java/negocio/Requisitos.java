package negocio;
//Lo que la mision necesita y consume
public class Requisitos {
    //final: Una vez creado el objeto, los valores no cambian
    private final int combustible;
    private final int energia;
    private final int desgaste;

    public Requisitos(int combustible, int energia, int desgaste){
        // PRE: ningún costo puede ser negativo
        if (combustible < 0 || energia < 0 || desgaste < 0)
            throw new IllegalArgumentException("Los costos no pueden ser negativos");
        this.combustible = combustible;
        this.energia = energia;
        this.desgaste = desgaste;
    }

    public int getCombustible() { return combustible; }
    public int getEnergia()     { return energia; }
    public int getDesgaste()    { return desgaste; }
}
