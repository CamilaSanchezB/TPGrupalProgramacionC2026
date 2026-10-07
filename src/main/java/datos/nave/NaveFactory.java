package datos.nave;

public class NaveFactory {
    public static Nave crearNave(String tipo) {
        tipo = tipo.toUpperCase();
        switch (tipo) {
            case "EXPLORADORA": return new NaveExploradora(60, 80, 0);
            case "CARGUERO": return new NaveCarguero(100, 60, 0);
            case "COMBATE": return new NaveCombate(80, 100, 0);
            default: throw new IllegalArgumentException("Tipo de nave desconocido");
        }
    }
}