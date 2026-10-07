package negocio;
//Lo que la mision necesita y consume
/**
 * INV: combustible >= 0, energia >= 0 y desgaste >= 0
 * INV: es inmutable: sus valores no cambian después de creado
 */
public class Requisitos {
    //final: Una vez creado el objeto, los valores no cambian
    private final int combustible;
    private final int energia;
    private final int desgaste;

    /**
     * @param combustible Combustible que consume la misión
     * @param energia Energía que consume la misión
     * @param desgaste Desgaste que produce la misión
     * PRE: combustible >= 0, energia >= 0 y desgaste >= 0
     * POST: los tres valores quedan fijados y no pueden modificarse
     */
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
