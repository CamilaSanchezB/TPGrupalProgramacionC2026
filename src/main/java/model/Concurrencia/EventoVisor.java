package model.Concurrencia;

/** Evento inmutable del universo paralelo. INV: tipo != null; nombre y texto nunca son null */
public final class EventoVisor {
    public enum Tipo { GENERADORA_AGREGADA, CONSUMIDORA_AGREGADA, BATERIA_DEPOSITADA, BATERIA_RETIRADA, MENSAJE }

    private final Tipo tipo;
    private final String nombre;
    private final String texto;

    private EventoVisor(Tipo tipo, String nombre, String texto) {
        this.tipo = tipo;
        this.nombre = nombre == null ? "" : nombre;
        this.texto = texto == null ? "" : texto;
    }

    public static EventoVisor generadoraAgregada(String nombre) { return new EventoVisor(Tipo.GENERADORA_AGREGADA, nombre, ""); }
    public static EventoVisor consumidoraAgregada(String nombre) { return new EventoVisor(Tipo.CONSUMIDORA_AGREGADA, nombre, ""); }
    public static EventoVisor bateriaDepositada(String nombre) { return new EventoVisor(Tipo.BATERIA_DEPOSITADA, nombre, ""); }
    public static EventoVisor bateriaRetirada(String nombre) { return new EventoVisor(Tipo.BATERIA_RETIRADA, nombre, ""); }
    public static EventoVisor mensaje(String texto) { return new EventoVisor(Tipo.MENSAJE, "", texto); }

    public Tipo getTipo() { return tipo; }
    public String getNombre() { return nombre; }
    public String getTexto() { return texto; }
}