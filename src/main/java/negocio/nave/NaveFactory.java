package negocio.nave;

public class NaveFactory {
    public Nave crearNave(String tipo) {
        tipo = tipo.toUpperCase();
        switch (tipo) {
            case "EXPLORADORA": return new NaveExploradora(60, 80);
            case "CARGUERO": return new NaveCarguero(100, 60);
            case "COMBATE": return   new NaveCombate(80, 100);
            default: throw new IllegalArgumentException("Tipo de nave desconocido");
        }
    }
}