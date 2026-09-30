package presentacion;

import negocio.nave.Nave;
import negocio.nave.NaveFactory;

public class Main {
    public static void main(String[] args){
        NaveFactory factory = new NaveFactory();
        Nave nave = factory.getNave("combate");
        System.out.println(nave.getCapitan());
    }
}
