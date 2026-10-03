package negocio;

/**
 * INV: 0 <= combustible <= 100, 0 <= energia <= 100, 0 <= desgaste <= 100
 */
public class Recursos {
    public static final int CAPACIDAD_MAXIMA = 100;
    public static final int DESGASTE_MAXIMO = 100;
    public static final int UMBRAL_MANTENIMIENTO = 80;

    private int combustible;
    private int energia;
    private int desgaste;

    /**
     * @param combustible Cantidad de combustible disponible para cargar
     * PRE: combustible > 0 y this.combustible + combustible <= CAPACIDAD_MAXIMA
     * POST: si no falla, combustible aumenta dentro de los limites establecidos
     */
    public void cargarCombustible(int combustible) {
        if (combustible <= 0)
            throw new IllegalArgumentException("La carga debe ser positiva");
        if (this.combustible > CAPACIDAD_MAXIMA - combustible)
            throw new IllegalStateException("La carga supera la capacidad maxima");
        this.combustible += combustible;
    }

    /**
     * @param energia Cantidad de energia disponible para cargar
     * PRE: energia > 0 y this.energia + energia <= CAPACIDAD_MAXIMA
     * POST: si no falla, energia aumenta dentro de los limites establecidos
     */
    public void cargarEnergia(int energia) {
        if (energia <= 0)
            throw new IllegalArgumentException("La carga debe ser positiva");
        if (this.energia > CAPACIDAD_MAXIMA - energia)
            throw new IllegalStateException("La carga supera la capacidad maxima");
        this.energia += energia;
    }
    /**
     *
     * @param combustible Cantidad de combustible
     * @param energia Cantidad de energia
     * @param desgaste Desgaste de la nave
     * PRE: combustible, energia y desgaste (los parametros) deben ser >= 0 y, para poder consumirse, deben estar dentro de los parametros (el combustible y la energia no pueden resultar menores que 0 y el desgaste no puede superar su capacidad maxima)
     * POST: el consumo se efectua, perdiendo combustible y energia y generando desgaste
     */
    public void consumir(int combustible, int energia, int desgaste) {
        if (combustible < 0 || energia < 0 || desgaste < 0)
            throw new IllegalArgumentException("Los consumos no pueden ser negativos");
        if (!puedeConsumir(combustible, energia, desgaste))
            throw new IllegalStateException("Recursos insuficientes o desgaste excedido");
        this.combustible -= combustible;
        this.energia -= energia;
        this.desgaste += desgaste;
    }

    private static boolean enRango (int x) {
        return x >= 0 && x <= CAPACIDAD_MAXIMA;
    }

    public Recursos(int combustible, int energia, int desgaste) {
        if (!enRango(combustible) || !enRango(energia) || !enRango(desgaste))
            throw new IllegalArgumentException("Valores iniciales no válidos");
        this.desgaste = desgaste;
        this.energia = energia;
        this.combustible = combustible;
    }

    public int getCombustible() {
        return combustible;
    }

    public int getEnergia() {
        return energia;
    }

    public int getDesgaste() {
        return desgaste;
    }

    public boolean requiereMantenimiento() {
        return desgaste >= UMBRAL_MANTENIMIENTO;
    }

    /**
     * POST: El desgaste de la nave se reestablece (desgaste = 0)
     */
    public void realizarMantenimiento() {
        desgaste = 0;
    }

    public boolean puedeConsumirCombustible(int combustible) {
        return combustible >= 0 && combustible <= this.combustible;
    }

    public boolean puedeConsumirEnergia(int energia) {
        return energia >= 0 && energia <= this.energia;
    }

    public boolean puedeDesgastar(int desgaste) {
        return desgaste >= 0 && desgaste <= DESGASTE_MAXIMO - this.desgaste;
    }

    public boolean puedeConsumir(int combustible, int energia, int desgaste) {
        return puedeConsumirCombustible(combustible) && puedeConsumirEnergia(energia) && puedeDesgastar(desgaste);
    }
}