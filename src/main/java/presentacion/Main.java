package presentacion;

import datos.Bitacora;
import negocio.ClasesMotorWarp.EstadoMotorBase;
import negocio.ClasesMotorWarp.MotorWarp;

public class Main {
    public static void main( String[] args){
        Bitacora b = new Bitacora();
        MotorWarp motorcito = new MotorWarp();
        motorcito.prepararSalto();
        motorcito.entrarEnWarp();
        motorcito.iniciarEnfriamiento();
        motorcito.estarDisponible();
        EstadoMotorBase estado = motorcito.getEstadoActual();
        System.out.println(estado.getNombreEstado()); //muestra el ultimo estado, en este caso seria disponible

        b.mostrarBitacora();

    }
}
