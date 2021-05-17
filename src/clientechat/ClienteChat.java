package clientechat;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

public class ClienteChat extends JFrame {

	private JTextArea mensajesChat;
	private Socket socket;

	private int puerto;
	private String host;
	private String usuario;

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

	public void recibirMensajesServidor() {
		DataInputStream entradaDatos = null;
		String mensaje;
		try {
			entradaDatos = new DataInputStream(socket.getInputStream());
		} catch (IOException ex) {
		} catch (NullPointerException ex) {
		}

		boolean conectado = true;
		while (conectado) {
			try {
				mensaje = entradaDatos.readUTF();
				mensajesChat.append(mensaje + System.lineSeparator());
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