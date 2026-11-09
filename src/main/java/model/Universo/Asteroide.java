package model.Universo;
import model.Coordenada;
import model.nave.Nave;

/**
 * Obstáculo: al chocar descuenta un monto fijo de energía.
 * INV: posicion != null
 */
public class Asteroide implements ElementoDelUniverso {
    public static final int DANIO_ENERGIA = 20;   // decisión del grupo

    private Coordenada posicion;

    public Asteroide(Coordenada posicion) {
        if (posicion == null)
            throw new IllegalArgumentException("El asteroide necesita una posición");
        this.posicion = posicion;
    }

    @Override
    public Coordenada getPosicion() { return posicion; }

    @Override
    public void setPosicion(Coordenada nueva) {
        if (nueva == null)
            throw new IllegalArgumentException("La posición no puede ser nula");
        this.posicion = nueva;
    }

    /**
     * POST: la energía baja DANIO_ENERGIA, o hasta 0 si había menos (nunca negativa);
     *       combustible y desgaste no cambian. Si la energía llega a 0,
     *       propia.estaDestruida() == true
     */
    @Override
    public String interactuar(Nave propia) {
        if (propia == null)
            throw new IllegalArgumentException("La nave no puede ser nula");
        int danio = Math.min(DANIO_ENERGIA, propia.getEnergia());
        propia.consumir(0, danio, 0);
        return "Impacto con asteroide en " + posicion + ": -" + danio + " de energía";
    }

    @Override
    public boolean esObstaculo() { return true; }
}