package clientechat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JTextField;

public class ConexionServidor implements ActionListener {

	private JTextField tfMensaje;
	private String usuario;
	private DataOutputStream salidaDatos;

	public ConexionServidor(Socket socket, JTextField tfMensaje, String usuario) {
		this.tfMensaje = tfMensaje;
		this.usuario = usuario;
		try {
			this.salidaDatos = new DataOutputStream(socket.getOutputStream());
		} catch (NullPointerException ex) {
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			SecretKeySpec secretKey = this.crearClave("programacionclas");
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String msg = encriptar(tfMensaje.getText());
		try {
			salidaDatos.writeUTF(usuario + ": " + msg);
			tfMensaje.setText("");
		} catch (IOException ex) {
		}
	}
	
}
