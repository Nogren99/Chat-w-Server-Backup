package negocio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import modelo.NotificacionCaida;

public class Monitor {
	private static Monitor instancia;
	private Socket socket;
	private ObjectOutputStream flujoSalida;
	private ObjectInputStream flujoEntrada;
	
	public static Monitor getInstance() {
		if (instancia==null) {
			instancia = new Monitor();
		}
		return instancia;
	}
	
	public void conectarServer(String host, int puerto)	{
		try {
			this.socket= new Socket(host,puerto);
			this.flujoSalida = new ObjectOutputStream(socket.getOutputStream());
			this.flujoEntrada = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {

		}	
	}
	
	
	public void heartbeats() {
		
	}
	
	public void pingEcho() {
		
		
	}
	
	private void respaldar() {
		try {
			flujoSalida.writeObject(new NotificacionCaida());
		} catch (IOException e) {

		}
		
	}

}
