package datos;
//Estado de los recursos iniciales antes de la ejecucion de la mision.
/**
 * INV: 0 <= combustible <= 100, 0 <= energia <= 100, 0 <= desgaste <= 100
 *      (mismos rangos que Recursos)
 * INV: es inmutable: es una "foto" de la nave que no cambia después de creada
 */
public class RecursosIniciales {
    private final int combustible;
    private final int energia;
    private final int desgaste;

    /**
     * @param combustible Combustible de la nave antes de la misión
     * @param energia Energía de la nave antes de la misión
     * @param desgaste Desgaste de la nave antes de la misión
     * PRE: los tres valores están entre 0 y Recursos.CAPACIDAD_MAXIMA
     * POST: los valores quedan fijados y no pueden modificarse
     */
    public RecursosIniciales(int combustible,int energia, int desgaste){
        this.combustible=combustible;
        this.energia=energia;
        this.desgaste=desgaste;
    }

    public int getCombustible() { return combustible; }
    public int getEnergia()     { return energia; }
    public int getDesgaste()    { return desgaste; }
}
