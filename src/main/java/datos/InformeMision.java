package datos;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Informe final de una misión. No imprime nada: solo ofrece datos.
 *
 * INV: nombreMision, resultado, acciones y observaciones nunca son null
 * INV: combustibleConsumido >= 0, energiaConsumida >= 0 y desgasteGenerado >= 0
 * INV: 0 <= combustibleFinal, energiaFinal, desgasteFinal <= 100
 * INV: es inmutable: ningún dato (ni la lista de acciones) puede modificarse después de creado
 */

public class InformeMision{
    private final String nombreMision;
    private final ResultadoMision resultado;
    private final List <String> acciones;
    //Recursos consumidos durante la mision:
    private final int combustibleConsumido;
    private final int energiaConsumida;
    private final int desgasteGenerado;
    // Estado en que quedo la nave
    private final int combustibleFinal;
    private final int energiaFinal;
    private final int desgasteFinal;
    private final String observaciones;

    public InformeMision(String nombreMision, ResultadoMision resultado, List<String> acciones,
                         int combustibleConsumido, int energiaConsumida, int desgasteGenerado,
                         int combustibleFinal, int energiaFinal, int desgasteFinal,
                         String observaciones) {
        // PRE: no se puede crear un informe sin misión ni resultado
        if (nombreMision == null || resultado == null)
            throw new IllegalArgumentException("El informe necesita misión y resultado");
        this.nombreMision = nombreMision;
        this.resultado = resultado;
        // Copia defensiva: si la misión modifica su lista después, el informe no cambia
        this.acciones = Collections.unmodifiableList(new ArrayList<>(acciones));
        this.combustibleConsumido = combustibleConsumido;
        this.energiaConsumida = energiaConsumida;
        this.desgasteGenerado = desgasteGenerado;
        this.combustibleFinal = combustibleFinal;
        this.energiaFinal = energiaFinal;
        this.desgasteFinal = desgasteFinal;
        this.observaciones = observaciones;
    }

    // Solo getters: sin setters, el informe no se puede modificar
    public String getNombreMision()       { return nombreMision; }
    public ResultadoMision getResultado() { return resultado; }
    public List<String> getAcciones()     { return acciones; }
    public int getCombustibleConsumido()  { return combustibleConsumido; }
    public int getEnergiaConsumida()      { return energiaConsumida; }
    public int getDesgasteGenerado()      { return desgasteGenerado; }
    public int getCombustibleFinal()      { return combustibleFinal; }
    public int getEnergiaFinal()          { return energiaFinal; }
    public int getDesgasteFinal()         { return desgasteFinal; }
    public String getObservaciones()      { return observaciones; }
}
