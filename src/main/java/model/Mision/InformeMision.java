package model.Mision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.ClasesBitacora.RegitroBitacora;

/**
 * Informe final de una misión. No imprime nada: solo ofrece datos.
 *
 * INV: nombreMision, resultado, eventos y observaciones nunca son null
 * INV: combustibleConsumido, energiaConsumida y desgasteGenerado son el balance NETO del viaje
 *      (final - inicial, medido antes de las recompensas); pueden ser negativos si la nave
 *      recargó en una estación durante la travesía
 * INV: 0 <= combustibleFinal, energiaFinal, desgasteFinal <= 100
 * INV: es inmutable: ningún dato (ni la lista de eventos) puede modificarse después de creado
 * INV: eventos contiene solo lo ocurrido durante ESTA misión
 */
public class InformeMision {
    private final String nombreMision;
    private final ResultadoMision resultado;
    private final List<RegitroBitacora> eventos;
    private final int combustibleConsumido;
    private final int energiaConsumida;
    private final int desgasteGenerado;
    private final int combustibleFinal;
    private final int energiaFinal;
    private final int desgasteFinal;
    private final String observaciones;

    /**
     * PRE: nombreMision, resultado y eventos != null
     * POST: el informe guarda una copia de eventos (si la lista original cambia, el informe no);
     *       observaciones null se guarda como ""
     * @throws IllegalArgumentException si se viola la PRE
     */
    public InformeMision(String nombreMision, ResultadoMision resultado, List<RegitroBitacora> eventos,
                         int combustibleConsumido, int energiaConsumida, int desgasteGenerado,
                         int combustibleFinal, int energiaFinal, int desgasteFinal,
                         String observaciones) {
        if (nombreMision == null || resultado == null || eventos == null)
            throw new IllegalArgumentException("El informe necesita misión, resultado y eventos");
        this.nombreMision = nombreMision;
        this.resultado = resultado;
        this.eventos = Collections.unmodifiableList(new ArrayList<RegitroBitacora>(eventos));
        this.combustibleConsumido = combustibleConsumido;
        this.energiaConsumida = energiaConsumida;
        this.desgasteGenerado = desgasteGenerado;
        this.combustibleFinal = combustibleFinal;
        this.energiaFinal = energiaFinal;
        this.desgasteFinal = desgasteFinal;
        this.observaciones = observaciones == null ? "" : observaciones;
    }

    // Solo getters: sin setters, el informe no se puede modificar
    public String getNombreMision()       { return nombreMision; }
    public ResultadoMision getResultado() { return resultado; }
    /** POST: consulta; lista no modificable con los eventos de esta misión, en orden cronológico */
    public List<RegitroBitacora> getEventos() { return eventos; }
    public int getCombustibleConsumido()  { return combustibleConsumido; }
    public int getEnergiaConsumida()      { return energiaConsumida; }
    public int getDesgasteGenerado()      { return desgasteGenerado; }
    public int getCombustibleFinal()      { return combustibleFinal; }
    public int getEnergiaFinal()          { return energiaFinal; }
    public int getDesgasteFinal()         { return desgasteFinal; }
    public String getObservaciones()      { return observaciones; }
}