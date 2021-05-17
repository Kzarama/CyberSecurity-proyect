package clientechat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextField;

public class ConexionServidor implements ActionListener {

	private Socket socket;
	private JTextField tfMensaje;
	private String usuario;
	private DataOutputStream salidaDatos;

	public ConexionServidor(Socket socket, JTextField tfMensaje, String usuario) {
		this.socket = socket;
		this.tfMensaje = tfMensaje;
		this.usuario = usuario;
		try {
			this.salidaDatos = new DataOutputStream(socket.getOutputStream());
		} catch (NullPointerException ex) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			salidaDatos.writeUTF(usuario + ": " + tfMensaje.getText());
			tfMensaje.setText("");
		} catch (IOException ex) {
		}
	}
}