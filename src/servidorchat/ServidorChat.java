package servidorchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorChat {

	public static void main(String[] args) {

		int puerto = 1234;
		int maximoConexiones = 10;
		ServerSocket servidor = null;
		Socket socket = null;
		MensajesChat mensajes = new MensajesChat();

		try {
			servidor = new ServerSocket(puerto, maximoConexiones);

			while (true) {
				socket = servidor.accept();

				ConexionCliente cc = new ConexionCliente(socket, mensajes);
				cc.start();

			}
		} catch (IOException ex) {
		} finally {
			try {
				socket.close();
				servidor.close();
			} catch (IOException ex) {
			}
		}
	}
}