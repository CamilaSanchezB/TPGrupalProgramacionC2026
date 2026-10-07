package negocio.nave;
import java.util.ArrayList;
import datos.tripulantes.Tripulante;
import datos.Bitacora;
import negocio.ClasesMotorWarp.MotorWarp;
import negocio.Recursos;

public abstract class Nave {
    private ArrayList<Tripulante> tripulantes;
    private MotorWarp motorWarp;
    private Bitacora bitacora;
    private Recursos recursos;

    protected Nave(int combustibleIni, int energiaIni, int desgaste) {
        this.bitacora = new Bitacora();
        this.motorWarp = new MotorWarp();
        this.recursos = new Recursos(combustibleIni, energiaIni, desgaste);
    }

    public void asignarTripulante(Tripulante t){
        if (t != null)
            tripulantes.add(t);
        else
            throw new IllegalArgumentException("tripulante invalido");
    }

    public void prepararSalto() {
        motorWarp.prepararSalto();
    }
    public void entrarEnWarp() {
        motorWarp.entrarEnWarp();
    }
    public void iniciarEnfriamiento() {
        motorWarp.iniciarEnfriamiento();
    }
    public void estarDisponible() {
        motorWarp.estarDisponible();
    }

    public void consumir(int combustible, int energia, int desgaste) {
        recursos.consumir(combustible, energia, desgaste);
    }
    public void realizarMantenimiento() {
        recursos.realizarMantenimiento();
    }

    public void cargarCombustible(int cantidad){
        recursos.cargarCombustible(cantidad);
    }

    public void cargarEnergia(int cantidad){
        recursos.cargarEnergia(cantidad);
    }

}