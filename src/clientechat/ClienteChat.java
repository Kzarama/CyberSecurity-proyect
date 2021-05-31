package clientechat;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.crypto.Cipher;

public class ClienteChat extends JFrame {

	private JTextArea mensajesChat;
	private Socket socket;

	private int puerto;
	private String host;
	private String usuario;

	private String key = "programacionclas";

	public ClienteChat() {
		super("Cliente Chat");

		mensajesChat = new JTextArea();
		mensajesChat.setEnabled(false);
		mensajesChat.setLineWrap(true);
		mensajesChat.setWrapStyleWord(true);
		JScrollPane scrollMensajesChat = new JScrollPane(mensajesChat);
		JTextField tfMensaje = new JTextField("");
		JButton btEnviar = new JButton("Enviar");

		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(20, 20, 20, 20);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		c.add(scrollMensajesChat, gbc);
		gbc.gridwidth = 1;
		gbc.weighty = 0;

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 20, 20, 20);

		gbc.gridx = 0;
		gbc.gridy = 1;
		c.add(tfMensaje, gbc);
		gbc.weightx = 0;

		gbc.gridx = 1;
		gbc.gridy = 1;
		c.add(btEnviar, gbc);

		this.setBounds(400, 100, 400, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		VentanaConfiguracion vc = new VentanaConfiguracion(this);
		host = vc.getHost();
		puerto = vc.getPuerto();
		usuario = vc.getUsuario();

		try {
			socket = new Socket(host, puerto);
		} catch (UnknownHostException ex) {
		} catch (IOException ex) {
		}

		btEnviar.addActionListener(new ConexionServidor(socket, tfMensaje, usuario));
	}

	private SecretKeySpec crearClave(String clave) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] claveEncriptacion = clave.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		claveEncriptacion = sha.digest(claveEncriptacion);
		claveEncriptacion = Arrays.copyOf(claveEncriptacion, 16);
		SecretKeySpec secretKey = new SecretKeySpec(claveEncriptacion, "AES");
		return secretKey;
	}

	public String encriptar(String datos) {
		String encriptado = "";
		try {
			SecretKeySpec secretKey = this.crearClave(key);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			byte[] datosEncriptar = datos.getBytes("UTF-8");
			byte[] bytesEncriptados = cipher.doFinal(datosEncriptar);
			encriptado = Base64.getEncoder().encodeToString(bytesEncriptados);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encriptado;
	}

	public String desencriptar(String datosEncriptados) {
		String datos = "";
		try {
			SecretKeySpec secretKey = crearClave(key);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			byte[] bytesEncriptados = Base64.getDecoder().decode(datosEncriptados);
			byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
			datos = new String(datosDesencriptados);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datos;
	}

	public void recibirMensajesServidor() {
		DataInputStream entradaDatos = null;
		try {
			entradaDatos = new DataInputStream(socket.getInputStream());
		} catch (IOException ex) {
		} catch (NullPointerException ex) {
		}

		boolean conectado = true;
		while (conectado) {
			try {
				String message = entradaDatos.readUTF();
				String user = message.substring(0, message.indexOf(':'));
				String msg = desencriptar(message.substring(message.indexOf(':') + 2));
				mensajesChat.append(user + ": " + msg + System.lineSeparator());
			} catch (IOException ex) {
				conectado = false;
			} catch (NullPointerException ex) {
				conectado = false;
			}
		}
	}

	public static void main(String[] args) {
		ClienteChat c = new ClienteChat();
		c.recibirMensajesServidor();
	}

}
