package pruebas;

import java.awt.EventQueue;
import controller.LoginController;
import persistencia.ServicioUsuarios;
import persistencia.dao.UsuarioDAOSqlite;
import view.LoginView;

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