package datos;
import java.util.ArrayList;
import java.util.List;

public class Bitacora {
    List <String> bitacora;

    public Bitacora (){
        this.bitacora = new ArrayList<>();
    }

    public void agregarEvento(String evento){
        this.bitacora.add(evento);
    }

    //muestra de mas viejo a mas reciente
    public void mostrarBitacora(){
        for(String evento : this.bitacora)
            System.out.println("- " + evento);
    }
}
