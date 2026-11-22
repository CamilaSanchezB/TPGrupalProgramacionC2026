package model;

/**
 * INV: 0 <= combustible <= 100, 0 <= energia <= 100, 0 <= desgaste <= 100, gemas >= 0
 */
public class Recursos {
    public static final int CAPACIDAD_MAXIMA = 100;
    public static final int DESGASTE_MAXIMO = 100;
    public static final int UMBRAL_MANTENIMIENTO = 80;

    private int combustible;
    private int energia;
    private int desgaste;
    private int gemas;

    /**
     * @param combustible Cantidad de combustible disponible para cargar
     *                    PRE: combustible > 0 y this.combustible + combustible <= CAPACIDAD_MAXIMA
     *                    POST: si no falla, combustible aumenta dentro de los limites establecidos
     */
    public void cargarCombustible(int combustible) {
        if (combustible <= 0) throw new IllegalArgumentException("La carga debe ser positiva");
        if (this.combustible > CAPACIDAD_MAXIMA - combustible)
            throw new IllegalArgumentException("La carga supera la capacidad maxima");
        this.combustible += combustible;
    }

    /**
     * @param energia Cantidad de energia disponible para cargar
     *                PRE: energia > 0 y this.energia + energia <= CAPACIDAD_MAXIMA
     *                POST: si no falla, energia aumenta dentro de los limites establecidos
     */
    public void cargarEnergia(int energia) {
        if (energia <= 0) throw new IllegalArgumentException("La carga debe ser positiva");
        if (this.energia > CAPACIDAD_MAXIMA - energia)
            throw new IllegalArgumentException("La carga supera la capacidad maxima");
        this.energia += energia;
    }

    /**
     *
     * @param combustible Cantidad de combustible
     * @param energia     Cantidad de energia
     * @param desgaste    Desgaste de la nave
     *                    PRE: combustible, energia y desgaste (los parametros) deben ser >= 0 y, para poder consumirse, deben estar dentro de los parametros (el combustible y la energia no pueden resultar menores que 0 y el desgaste no puede superar su capacidad maxima)
     *                    POST: el consumo se efectua, perdiendo combustible y energia y generando desgaste
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

    private static boolean enRango(int x) {
        return x >= 0 && x <= CAPACIDAD_MAXIMA;
    }

    public Recursos(int combustible, int energia, int desgaste) {
        this(combustible, energia, desgaste, 0);
    }

    public Recursos(int combustible, int energia, int desgaste, int gemas) {
        if (!(enRango(combustible) && enRango(energia) && enRango(desgaste)))
            throw new IllegalArgumentException("Valores iniciales no válidos");
        if (gemas < 0) throw new IllegalArgumentException("Las gemas no pueden ser negativas");
        this.desgaste = desgaste;
        this.energia = energia;
        this.combustible = combustible;
        this.gemas = gemas;
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

    public int getGemas() {
        return gemas;
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

    /**
     * @return true si cantidad > 0 y la carga no supera CAPACIDAD_MAXIMA. No modifica nada.
     */
    public boolean puedeCargarCombustible(int cantidad) {
        return cantidad > 0 && combustible <= CAPACIDAD_MAXIMA - cantidad;
    }

    /**
     * @return true si cantidad > 0 y la carga no supera CAPACIDAD_MAXIMA. No modifica nada.
     */
    public boolean puedeCargarEnergia(int cantidad) {
        return cantidad > 0 && energia <= CAPACIDAD_MAXIMA - cantidad;
    }

    public void agregarGemas(int cant) {
        if (cant <= 0) throw new IllegalArgumentException("La cantidad de gemas debe ser positiva");
        gemas += cant;
    }

    /**
     * Estación de carga o M-03: sube hasta el tope en vez de rechazar
     * PRE: combustible >= o y energia >= 0
     * POST: cada uno = min(100, anterior + cantidad); desgaste y gemas no combian
     */
    public void recargarHastaTope(int combustible, int energia) {
        if (combustible < 0 || energia < 0)
            throw new IllegalArgumentException("Las recargas no pueden ser negativas");
        this.combustible = Math.min(CAPACIDAD_MAXIMA, this.combustible + combustible);
        this.energia = Math.min(CAPACIDAD_MAXIMA, this.energia + energia);
    }

    /**
     * Daño del universo (asteroide, enfriamiento): nunca se rechaza, se detiene en los límites.
     * PRE: energía >= 0 y desgaste >= 0
     * POST: energia = max(0, anterior - energia); desgaste = min(100, anterior + desgaste)
     */
    public void recibirImpacto(int energia, int desgaste) {
        if (energia < 0 || desgaste < 0)
            throw new IllegalArgumentException("El impacto no puede ser negativo");
        this.energia = Math.max(0, this.energia - energia);
        this.desgaste = Math.min(DESGASTE_MAXIMO, this.desgaste + desgaste);
    }

    /** Enunciado: desgaste 100, energía 0 o combustible 0 destruyen la nave. Es una consulta. */
    public boolean estaDestruida() {
        return desgaste >= DESGASTE_MAXIMO || energia <= 0 || combustible <= 0;
    }

    /** POST: copia independiente (con gemas), para el "antes y después" del informe. */
    public Recursos copia() {
        return new Recursos(combustible, energia, desgaste, gemas);
    }
}