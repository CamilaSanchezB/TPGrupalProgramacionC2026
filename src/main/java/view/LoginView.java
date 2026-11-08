package view;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.LoginController;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class LoginView extends JFrame implements ILogin {

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

	public LoginView() {
		initialize();
	}

	/** Crea y ubica todos los componentes de la ventana. */
	private void initialize() {
		ConfigView.aplicar();
		setTitle("Acceso");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 420);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setLayout(new MigLayout("insets 24", "[grow][][grow]", "[grow][][grow]"));
		setContentPane(contentPane);

		// La tarjeta de acceso queda centrada en las dos direcciones.
		panelAcceso = new JPanel();
		panelAcceso.setLayout(new MigLayout("insets 0, gap 14 10", "[100!][260!]",
				"[]4[]18[32!][32!]18[]12[]"));
		contentPane.add(panelAcceso, "cell 1 1");

		lblTitulo = new JLabel("Acceso");
		lblTitulo.setFont(ConfigView.FUENTE_TITULO_GRANDE);
		lblTitulo.setForeground(ConfigView.TITULO);
		panelAcceso.add(lblTitulo, "cell 0 0 2 1");

		lblSubtitulo = new JLabel("Ingresá con tu usuario o registrate");
		lblSubtitulo.setFont(ConfigView.FUENTE_ETIQUETA);
		panelAcceso.add(lblSubtitulo, "cell 0 1 2 1");

		lblUsuario = new JLabel("Usuario");
		lblUsuario.setFont(ConfigView.FUENTE_ETIQUETA);
		panelAcceso.add(lblUsuario, "cell 0 2");

		textoUsuario = new JTextField();
		textoUsuario.setColumns(10);
		panelAcceso.add(textoUsuario, "cell 1 2, growx");

		lblContrasena = new JLabel("Contraseña");
		lblContrasena.setFont(ConfigView.FUENTE_ETIQUETA);
		panelAcceso.add(lblContrasena, "cell 0 3");

		textoContrasena = new JPasswordField();
		textoContrasena.setColumns(10);
		panelAcceso.add(textoContrasena, "cell 1 3, growx");

		panelBotones = new JPanel();
		panelBotones.setLayout(new MigLayout("insets 0, gap 10", "[grow][grow]", "[36!]"));
		panelAcceso.add(panelBotones, "cell 0 4 2 1, growx");

		btnIngresar = new JButton("Ingresar");
		btnIngresar.setActionCommand("INGRESAR");
		btnIngresar.setBackground(ConfigView.ACENTO);
		btnIngresar.setForeground(Color.WHITE);
		panelBotones.add(btnIngresar, "cell 0 0, grow");

		btnRegistrar = new JButton("Registrarse");
		btnRegistrar.setActionCommand("REGISTRAR");
		panelBotones.add(btnRegistrar, "cell 1 0, grow");

		// Franja de feedback: queda vacía hasta que haya algo que decir.
		lblMensaje = new JLabel(" ");
		lblMensaje.setFont(ConfigView.FUENTE_MENSAJE);
		panelAcceso.add(lblMensaje, "cell 0 5 2 1, growx");

		// Enter en cualquiera de los dos campos equivale a apretar Ingresar.
		getRootPane().setDefaultButton(btnIngresar);
	}

	@Override
	public void setControlador(LoginController c) {
		this.controlador = c;
	}

	@Override
	public void arranca() {
		setVisible(true);
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

	@Override
	public void cerrar() {
		dispose();
	}
}
