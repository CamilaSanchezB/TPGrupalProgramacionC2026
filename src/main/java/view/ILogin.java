package view;

import java.awt.event.ActionListener;

import controller.LoginController;

public interface ILogin {

	/** Action commands de los botones, para que el controlador sepa cuál se presionó. */
	String INGRESAR = "INGRESAR";
	String REGISTRAR = "REGISTRAR";

	public void setControlador(LoginController c);
	public void arranca();
	public void setActionListener(ActionListener listener);
	public String getNombreUsuario();
	/** @return la contraseña tipeada; es char[] porque es lo que pide el hasheador. */
	public char[] getContrasena();
	public void limpiarContrasena();
	public void mostrarError(String mensaje);
	public void mostrarAviso(String mensaje);
	/** Cierra la ventana de acceso, una vez que el usuario entró. */
	public void cerrar();
}
