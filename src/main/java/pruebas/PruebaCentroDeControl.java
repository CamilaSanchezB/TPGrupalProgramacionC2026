package pruebas;

import controller.AsistenteDeComando;
import controller.ControladorPartida;
import model.CentroDeControl;
import model.ClasesBitacora.Bitacora;
import model.Mision.InformeMision;
import model.Mision.Mision;
import model.nave.Nave;
import model.nave.NaveFactory;

/** Simula al usuario sin interfaz: da de alta una nave, crea una misión, la encomienda y la ejecuta. */
public class PruebaCentroDeControl {
    public static void main(String[] args) {
        CentroDeControl centro = new CentroDeControl();
        Nave nave = new NaveFactory().crearNave("EXPLORADORA");
        centro.registrar("N1", new AsistenteDeComando(nave, new Bitacora()));

        AsistenteDeComando asistente = centro.obtener("N1");
        Mision mision = new ControladorPartida().crearMision("RECOLECCION", asistente);
        InformeMision informe = asistente.encomendarMision(mision);

        asistente.mostrarReporteBitacora();
        System.out.println("Resultado: " + informe.getResultado()
                + " | gemas: " + informe.getGemasGanadas());
    }
}