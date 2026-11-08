package pruebas;

import java.awt.EventQueue;

import controller.LiquidadorHaberes;
import controller.LoginController;
import controller.TripulantesController;
import model.nave.Nave;
import model.nave.NaveFactory;
import model.tripulantes.Consejero;
import model.tripulantes.Terricola;
import model.tripulantes.Tripulante;
import model.tripulantes.Vulcano;
import persistencia.ServicioUsuarios;
import persistencia.dao.UsuarioDAOSqlite;
import view.LoginView;
import view.TripulantesView;

public class PruebaLogin {
    public static void main(String[] args){
        UsuarioDAOSqlite dao = new UsuarioDAOSqlite();
        ServicioUsuarios servicio = new ServicioUsuarios(dao);
        LoginController controlador = new LoginController(servicio);
        EventQueue.invokeLater(() -> {
            LoginView vista = new LoginView();
            controlador.setVista(vista);
            vista.arranca();
        });
    }
}