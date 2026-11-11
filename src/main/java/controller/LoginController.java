package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import persistencia.NombreUsuarioEnUsoException;
import persistencia.PersistenciaException;
import persistencia.ServicioUsuarios;
import persistencia.UsuarioException;
import persistencia.dto.UsuarioDTO;
import view.ILogin;

/**
 * Controlador de la pantalla de acceso.
 *
 * Traduce los dos botones a las operaciones del servicio y deja el resultado en
 * usuarioAutenticado, para que quien abrió el login sepa quién entró.
 */
public class LoginController implements ActionListener {

	private final ServicioUsuarios servicio;
	private ILogin vista;
	private UsuarioDTO usuarioAutenticado;

	/**
	 * <b>PRE</b>
	 * - servicio != null
	 */
	public LoginController(ServicioUsuarios servicio) {
		assert servicio != null : "El servicio no puede ser nulo.";
		this.servicio = servicio;
	}

	public void setVista(ILogin vista) {
		this.vista = vista;
		this.vista.setControlador(this);
		this.vista.setActionListener(this);
	}

	/** @return el usuario que ingresó, o null si todavía no entró nadie. */
	public UsuarioDTO getUsuarioAutenticado() {
		return usuarioAutenticado;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ILogin.INGRESAR.equals(e.getActionCommand())) {
			ingresar();
		} else if (ILogin.REGISTRAR.equals(e.getActionCommand())) {
			registrar();
		}
	}

	private void ingresar() {
		char[] contrasena = vista.getContrasena();
		try {
			UsuarioDTO usuario = servicio.autenticar(vista.getNombreUsuario(), contrasena);
			if (usuario == null) {
				// Un solo mensaje para los dos casos: no se revela si el usuario existe.
				vista.mostrarError("Usuario o contraseña incorrectos.");
				vista.limpiarContrasena();
				return;
			}
			usuarioAutenticado = usuario;
			vista.cerrar();
		} catch (PersistenciaException ex) {
			vista.mostrarError("No se pudo acceder a la base de usuarios.");
		} finally {
			borrar(contrasena);
		}
	}

	private void registrar() {
		char[] contrasena = vista.getContrasena();
		try {
			UsuarioDTO usuario = servicio.registrar(vista.getNombreUsuario(), contrasena);
			vista.mostrarAviso("Usuario " + usuario.getNombreUsuario()
					+ " creado. Ya podés ingresar.");
			vista.limpiarContrasena();
		} catch (NombreUsuarioEnUsoException ex) {
			vista.mostrarError(ex.getMessage());
		} catch (UsuarioException ex) {
			vista.mostrarError(ex.getMessage());
		} catch (PersistenciaException ex) {
			vista.mostrarError("No se pudo acceder a la base de usuarios.");
		} finally {
			borrar(contrasena);
		}
	}

	/**
	 * Pisa la contraseña en memoria apenas se deja de usar. Es la razón por la
	 * que se maneja como char[] y no como String: un String queda en el pool
	 * hasta que el recolector lo levante y no se puede sobrescribir.
	 */
	private void borrar(char[] contrasena) {
		if (contrasena != null) {
			Arrays.fill(contrasena, '\0');
		}
	}
}
