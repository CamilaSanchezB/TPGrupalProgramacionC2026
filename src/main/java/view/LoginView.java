package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controller.LoginController;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class LoginView extends FondoEstrellas implements ILogin {

	private JPanel contentPane;
	private JPanel panelAcceso;
	private JPanel panelBotones;
	private JLabel lblTitulo;
	private JLabel lblSubtitulo;
	private JLabel lblUsuario;
	private JLabel lblContrasena;
	private JLabel lblMensaje;
	private JTextField textoUsuario;
	private JPasswordField textoContrasena;
	private JButton btnIngresar;
	private JButton btnRegistrar;

	private LoginController controlador;
	private Runnable alIngresar;

	public LoginView() {
		initialize();
	}

	/** Crea y ubica todos los componentes de la pantalla. */
	private void initialize() {
		ConfigView.aplicar();

		setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(ConfigView.FONDO);

		contentPane = new JPanel(new MigLayout(
				"insets 0, fill, align center center", "[grow]", "[grow]"));
		contentPane.setOpaque(false);
		add(contentPane, BorderLayout.CENTER);

		panelAcceso = new JPanel(new BorderLayout());
		panelAcceso.setOpaque(true);
		panelAcceso.setBackground(ConfigView.SUPERFICIE);
		panelAcceso.setBorder(BorderFactory.createLineBorder(ConfigView.BORDE));
		panelAcceso.setPreferredSize(new Dimension(460, 480));
		contentPane.add(panelAcceso, "align center");

		JPanel panelMarca = new JPanel(new MigLayout(
				"insets 24 24 20 24, wrap 1, align center", "[grow]", "[]8[]"));
		panelMarca.setOpaque(true);
		panelMarca.setBackground(ConfigView.FONDO);
		panelMarca.setBorder(BorderFactory.createMatteBorder(
				0, 0, 3, 0, ConfigView.ACENTO_CIAN));

		JLabel lblMarca = new JLabel("MISIÓN INTERESTELAR");
		lblMarca.setFont(ConfigView.FUENTE_TITULO_GRANDE);
		lblMarca.setForeground(ConfigView.ACENTO_CIAN);
		panelMarca.add(lblMarca, "align center");

		JLabel lblFrase = new JLabel("La aventura está por comenzar");
		lblFrase.setFont(ConfigView.FUENTE_ETIQUETA);
		lblFrase.setForeground(ConfigView.TEXTO_BANNER);
		panelMarca.add(lblFrase, "align center");

		panelAcceso.add(panelMarca, BorderLayout.NORTH);

		JPanel formulario = new JPanel(new MigLayout(
				"insets 28 34 26 34, fillx, wrap 1",
				"[grow]",
				"[]4[]14[]6[38!]14[]6[38!]18[42!]10[]"));
		formulario.setOpaque(true);
		formulario.setBackground(ConfigView.SUPERFICIE);
		panelAcceso.add(formulario, BorderLayout.CENTER);

		lblTitulo = new JLabel("Iniciar sesión");
		lblTitulo.setFont(ConfigView.FUENTE_TITULO);
		lblTitulo.setForeground(ConfigView.TITULO);
		formulario.add(lblTitulo, "growx");

		lblSubtitulo = new JLabel("Prepará tu nave y elegí tu próxima misión.");
		lblSubtitulo.setFont(ConfigView.FUENTE_ETIQUETA);
		lblSubtitulo.setForeground(ConfigView.TEXTO_SECUNDARIO);
		formulario.add(lblSubtitulo, "growx");

		lblUsuario = new JLabel("Usuario");
		lblUsuario.setFont(ConfigView.FUENTE_ETIQUETA);
		lblUsuario.setForeground(ConfigView.TITULO);
		formulario.add(lblUsuario, "growx");

		textoUsuario = new JTextField();
		textoUsuario.setColumns(15);
		textoUsuario.putClientProperty("JTextField.placeholderText", "Tu nombre de piloto");
		formulario.add(textoUsuario, "growx, h 38!");

		lblContrasena = new JLabel("Contraseña");
		lblContrasena.setFont(ConfigView.FUENTE_ETIQUETA);
		lblContrasena.setForeground(ConfigView.TITULO);
		formulario.add(lblContrasena, "growx");

		textoContrasena = new JPasswordField();
		textoContrasena.setColumns(15);
		textoContrasena.putClientProperty("JTextField.placeholderText", "Tu contraseña");
		formulario.add(textoContrasena, "growx, h 38!");

		panelBotones = new JPanel(new MigLayout(
				"insets 0, gap 12", "[grow][grow]", "[42!]"));
		panelBotones.setOpaque(false);
		formulario.add(panelBotones, "growx");

		btnIngresar = new JButton("Ingresar");
		btnIngresar.setActionCommand("INGRESAR");
		ConfigView.primario(btnIngresar);
		panelBotones.add(btnIngresar, "grow");

		btnRegistrar = new JButton("Crear cuenta");
		btnRegistrar.setActionCommand("REGISTRAR");
		ConfigView.secundario(btnRegistrar);
		panelBotones.add(btnRegistrar, "grow");

		lblMensaje = new JLabel(" ");
		lblMensaje.setFont(ConfigView.FUENTE_MENSAJE);
		formulario.add(lblMensaje, "growx");
	}

	@Override
	public void setControlador(LoginController c) {
		this.controlador = c;
	}

	/** Acción a ejecutar cuando el usuario ingresó correctamente (cambiar de pantalla). */
	public void setAlIngresar(Runnable alIngresar) {
		this.alIngresar = alIngresar;
	}

	/** Llamar con la pantalla ya visible: el botón por defecto necesita el rootPane. */
	@Override
	public void arranca() {
		if (SwingUtilities.getRootPane(this) != null) {
			// Enter en cualquiera de los dos campos equivale a apretar Ingresar.
			SwingUtilities.getRootPane(this).setDefaultButton(btnIngresar);
		}
		textoUsuario.requestFocusInWindow();
	}

	@Override
	public void setActionListener(ActionListener listener) {
		this.btnIngresar.addActionListener(listener);
		this.btnRegistrar.addActionListener(listener);
	}

	@Override
	public String getNombreUsuario() {
		return textoUsuario.getText();
	}

	@Override
	public char[] getContrasena() {
		return textoContrasena.getPassword();
	}

	@Override
	public void limpiarContrasena() {
		textoContrasena.setText("");
		textoContrasena.requestFocusInWindow();
	}

	@Override
	public void mostrarError(String mensaje) {
		lblMensaje.setForeground(ConfigView.ERROR);
		lblMensaje.setText(mensaje);
	}

	@Override
	public void mostrarAviso(String mensaje) {
		lblMensaje.setForeground(ConfigView.AVISO);
		lblMensaje.setText(mensaje);
	}

	/** El usuario entró: limpia la pantalla y avisa a quien la abrió. */
	@Override
	public void cerrar() {
		textoContrasena.setText("");
		lblMensaje.setText(" ");
		if (alIngresar != null) {
			alIngresar.run();
		}
	}
}