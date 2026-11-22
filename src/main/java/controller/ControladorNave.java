package controller;

import java.util.ArrayList;
import java.util.List;

import model.CentroDeControl;
import model.ClasesBitacora.Bitacora;
import model.ClasesBitacora.RegitroBitacora;
import model.Mision.InformeMision;
import model.Mision.ResultadoMision;
import model.nave.Nave;
import model.nave.NaveFactory;
import model.tripulantes.Tripulante;

/**
 * Crea la nave elegida, la registra en el centro de control y arma, para las vistas,
 * los datos que necesitan mostrar (EstadoNaveVista y ResumenMisionVista).
 */
public class ControladorNave {
    private final CentroDeControl centro = new CentroDeControl();
    private final NaveFactory fabrica = new NaveFactory();
    private int contador = 0;

    private Nave nave;
    private AsistenteDeComando asistente;
    private String tipo;
    private boolean operativa;

    /** POST: hay una nave nueva, registrada en el centro de control, y operativa */
    public void elegirNave(String tipo) {
        Nave nueva = fabrica.crearNave(tipo);
        String id = tipo + "-" + (++contador);
        centro.registrar(id, new AsistenteDeComando(nueva, new Bitacora()));
        this.nave = nueva;
        this.tipo = tipo;
        this.asistente = centro.obtener(id);
        this.operativa = true;
    }

    public boolean haySeleccion() { return asistente != null; }
    public Nave getNave() { return nave; }
    public AsistenteDeComando getAsistente() { return asistente; }
    public String getTipo() { return tipo; }
    public boolean isOperativa() { return operativa; }

    /** Se llama al terminar una misión. */
    public void actualizarOperativa() { operativa = !asistente.estaDestruida(); }

    public EstadoNaveVista estado() {
        List<String> trip = new ArrayList<String>();
        for (Tripulante t : asistente.getTripulantes()) {
            trip.add("• " + t.getCargo() + ": " + t.getNombre());
        }
        List<InformeMision> hist = asistente.getHistorialMisiones();
        List<String> lineas = new ArrayList<String>();
        int exitosas = 0;
        for (int i = hist.size() - 1; i >= 0; i--) {
            InformeMision inf = hist.get(i);
            if (inf.getResultado() == ResultadoMision.EXITOSA) exitosas++;
            lineas.add(String.format("[%s] %s (Comb:%+d Energ:%+d Desg:%+d)",
                    inf.getResultado(), inf.getNombreMision(),
                    -inf.getCombustibleConsumido(), -inf.getEnergiaConsumida(),
                    inf.getDesgasteGenerado()));
        }
        return new EstadoNaveVista(tipo, asistente.estaDestruida(), asistente.requiereMantenimiento(),
                asistente.getCombustible(), asistente.getEnergia(), asistente.getDesgaste(),
                asistente.getGemas(), asistente.getEstadoMotor(),
                asistente.puedeIniciarMision(4, 5, 4),
                asistente.puedeIniciarMision(4, 10, 4),
                asistente.puedeIniciarMision(4, 0, 4),
                trip, lineas, exitosas, hist.size());
    }

    /** @param informe puede ser null (se muestra como fallida) */
    public ResumenMisionVista resumen(InformeMision informe) {
        if (informe == null) {
            return new ResumenMisionVista(ResumenMisionVista.Estado.FALLIDA, "", "",
                    asistente.getCombustible(), 0, asistente.getEnergia(), 0,
                    asistente.getDesgaste(), 0, asistente.getGemas(),
                    new ArrayList<String>(), asistente.estaDestruida());
        }
        ResumenMisionVista.Estado estado;
        switch (informe.getResultado()) {
            case EXITOSA: estado = ResumenMisionVista.Estado.EXITOSA; break;
            case RECHAZADA: estado = ResumenMisionVista.Estado.RECHAZADA; break;
            default: estado = ResumenMisionVista.Estado.FALLIDA;
        }
        List<String> eventos = new ArrayList<String>();
        for (RegitroBitacora ev : informe.getEventos()) {
            eventos.add("[" + ev.getTiempoActual() + "] " + ev.getDescripcion());
        }
        return new ResumenMisionVista(estado, informe.getNombreMision(), informe.getObservaciones(),
                informe.getCombustibleFinal(), -informe.getCombustibleConsumido(),
                informe.getEnergiaFinal(), -informe.getEnergiaConsumida(),
                informe.getDesgasteFinal(), informe.getDesgasteGenerado(),
                asistente.getGemas(), eventos, asistente.estaDestruida());
    }
}