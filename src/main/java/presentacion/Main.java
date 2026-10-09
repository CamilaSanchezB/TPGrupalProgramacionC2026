package presentacion;
import datos.MisionRecoleccion;
import datos.ClasesBitacora.Bitacora;
import datos.nave.*;
import datos.tripulantes.*;
import negocio.*;

public class Main {
    public static void main(String[] args){
        NaveFactory factory = new NaveFactory();

        Nave naveCombate = factory.crearNave("combate");

        Tripulante t = new Capitan("spock", 50);
        Vulcano spock = new Vulcano(t);
        naveCombate.asignarTripulante(spock);
        Bitacora bitacora = new Bitacora();
        AsistenteDeComando jarvis = new AsistenteDeComando(naveCombate, bitacora);

        MisionRecoleccion mision = new MisionRecoleccion(jarvis);
    }


}
