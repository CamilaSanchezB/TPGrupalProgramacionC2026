package main.java.negocio.nave;
import java.util.ArrayList;
import datos.tripulantes.Tripulante;
import negocio.ClasesMotorWarp.MotorWarp;

public abstract class Nave {
    public static final int maxCombustible = 100;
    public static final int maxEnergia = 100;
    private ArrayList<Tripulante> tripulantes;
    private MotorWarp motorWarp;
    protected int combustible;
    protected int energia;
    protected int desgaste;

    protected Nave(int combustibleIni, int energiaIni, int desgaste) {
        this.combustible = combustibleIni;
        this.energia = energiaIni;
        this.desgaste = desgaste;
    }

    public void cargarCombustible(int cantidad){
        if (cantidad < maxCombustible - this.combustible)
            this.combustible += cantidad;
        else
            throw new IllegalArgumentException("No se puede superar el maximo de combustible");
    }
    public void consumirCombustible(int cantidad){
        if (cantidad < this.combustible)
            this.combustible -= cantidad;
        else
            throw new IllegalArgumentException("No se puede tener combustible negativo");
    }

    public void cargarEnergia(int cantidad){
        if (cantidad < maxEnergia - this.energia)
            this.energia += cantidad;
        else
            throw new IllegalArgumentException("No se puede superar el maximo de Energia");
    }
    public void consumirEnergia(int cantidad){
        if (cantidad < this.energia)
            this.energia -= cantidad;
        else
            throw new IllegalArgumentException("No se puede tener Energia negativa");

    }
    public void asignarTripulante(Tripulante t){
        if (t != null)
            tripulantes.add(t);
        else
            throw new IllegalArgumentException("tripulante invalido");
    }
    public  void realizarMantenimiento(){
        this.desgaste = 0;
    }
}