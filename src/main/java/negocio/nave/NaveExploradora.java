package negocio.nave;
import datos.tripulantes.Tripulante;

public class NaveExploradora extends Nave{
	public NaveExploradora(int combustibleIni, int energiaIni, int desgaste) {
		super(combustibleIni, energiaIni, desgaste);
	}

	public void cargarCombustible(int cantidad){
		super.cargarCombustible(cantidad);
	}
	public void consumirCombustible(int cantidad){
		super.consumirCombustible(cantidad);
	}

	public void cargarEnergia(int cantidad){
		super.cargarEnergia(cantidad);
	}
	public void consumirEnergia(int cantidad){
		super.consumirEnergia(cantidad);
	}
	public void asignarTripulante(Tripulante t){
		super.asignarTripulante(t);
	}
	public  void realizarMantenimiento(){
		super.realizarMantenimiento();
	}

}
