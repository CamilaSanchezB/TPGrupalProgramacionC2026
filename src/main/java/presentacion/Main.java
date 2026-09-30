package presentacion;

import negocio.Bitacora;
import negocio.ClasesMotorWarp.EstadoMotor;
import negocio.ClasesMotorWarp.MotorWarp;

public class Main {
    public static void main( String[] args){
        Bitacora b = new Bitacora();
        MotorWarp motorcito = new MotorWarp(b);
        motorcito.prepararSalto();
        motorcito.entrarEnWarp();
        motorcito.iniciarEnfriamiento();
        motorcito.estarDisponible();
        EstadoMotor estado = motorcito.getEstadoActual();
        System.out.println(estado.getNombreEstado()); //muestra el ultimo estado, en este caso seria disponible

        b.mostrarBitacora();

    }
}
