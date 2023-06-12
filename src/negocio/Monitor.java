package negocio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import modelo.HeartBeat;
import modelo.IdentificadorMonitor;
import modelo.NotificacionCaida;

public class Monitor {
	private static Monitor instancia;
	private Socket socket;
	private ObjectOutputStream flujoSalida;
	private ObjectInputStream flujoEntrada;
	
	public static Monitor getInstance() {
		if (instancia==null) {
			System.out.println("Creando monitor");
			instancia = new Monitor();
		}
		return instancia;
	}
	
	public void conectarServer(String host, int puerto)	{
		try {
			Thread.sleep(2500);
			System.out.println("Conectando monitor al server principal "+ host + "puerto "+ puerto);
			this.socket= new Socket(host,puerto);
			System.out.println("Monitor connected");
			Thread.sleep(200);
			this.flujoSalida = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Creamos el flujo de salida");
			Thread.sleep(200);
			flujoSalida.writeObject(new IdentificadorMonitor());
			System.out.println("Identificador Monitor enviado");
			
		} catch (IOException | InterruptedException e) {

		}	
	}
	
	
	public void crearFlujoEntrada() {
		try {
			this.flujoEntrada = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Creamos el flujo de entrada.");
	}
	
	public void heartbeats() {
		try {
			
			Thread.sleep(3500);
			if (this.flujoEntrada.available()>0) {
				this.flujoEntrada.readObject();
				System.out.println("RECIBI LATIDO");
				this.heartbeats();    //mira esa recursividad papá!!
			}
			else
				this.respaldar();
		} catch (InterruptedException | IOException | ClassNotFoundException e) {

		}
		
		/*Timer timer = new Timer();
		 TimerTask mainTask = new TimerTask() {
	            @Override
	            public void run() {
	                try {
						Object object = flujoEntrada.readObject();
						if (object instanceof HeartBeat) {
							System.out.println("HeartBeat recibido");
						} else
							Thread.sleep(5000); //tardar mas hara que se muera el timer y nos da a entender q se murio el server
					} catch (ClassNotFoundException | IOException | InterruptedException e) {

					}
	            }
	      };
	      
	      
	      TimerTask alternateTask = new TimerTask() {
	            @Override
	            public void run() {
	                respaldar();
	            }
	        };
	        
	        
	        timer.schedule(mainTask, 5000);
	        timer.schedule(alternateTask, 10000);*/
		
		
		
	}
	
	public void pingEcho() {
		
		
	}
	
	private void respaldar() {
		try {
			flujoSalida.writeObject(new NotificacionCaida());
			System.out.println("Enviando notificacion caida");
		} catch (IOException e) {

		}
		
	}

}
