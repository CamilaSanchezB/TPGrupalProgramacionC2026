package view;

import java.awt.EventQueue;

import controller.LiquidadorHaberes;
import controller.TripulantesController;
import model.nave.Nave;
import model.nave.NaveFactory;
import model.tripulantes.Consejero;
import model.tripulantes.Terricola;
import model.tripulantes.Tripulante;
import model.tripulantes.Vulcano;

public class Main {
    public static void main(String[] args) {
        NaveFactory factory = new NaveFactory();
        Nave miNave = factory.crearNave("combate");
        LiquidadorHaberes liquidador = new LiquidadorHaberes();

        Tripulante capitan = miNave.getTripulantesPorCargo("CAPITAN").get(0);
        capitan.setAntiguedad(10);
        capitan.setNombre("Test capitan");
        capitan = new Vulcano(capitan);
        miNave.editarTripulante("Test capitan", capitan);
        miNave.asignarTripulante(new Terricola(new Consejero("Test consejero", 5, 10)));
        liquidador.imprimirLiquidacion(miNave);

        TripulantesController controlador = new TripulantesController(miNave, liquidador);
        EventQueue.invokeLater(() -> {
            TripulantesView vista = new TripulantesView();
            controlador.setVista(vista);
            vista.arranca();
        });
    }
}