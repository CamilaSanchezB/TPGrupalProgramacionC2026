package datos;
public class OperacionRechazadaException extends RuntimeException {
    public OperacionRechazadaException(String motivo) { super(motivo); }
}