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
	private Socket socketRespaldo;
	private ObjectOutputStream flujoSalida,flujoSalidaRespaldo;
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
			System.out.println("Conectando monitor al server principal "+ host + "puerto "+ puerto);
			this.socket= new Socket(host,puerto);
			System.out.println("Monitor connected");

			this.flujoSalida = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Creamos el flujo de salida");
			

			flujoSalida.writeObject(new IdentificadorMonitor());
			System.out.println("Identificador Monitor enviado");
			this.socketRespaldo = new Socket("localhost",2);
			
			flujoSalidaRespaldo = new ObjectOutputStream(socketRespaldo.getOutputStream());
			System.out.println("SocketREspaldo: "+ socketRespaldo + "\n flujo salida: "+ flujoSalidaRespaldo);
		} catch (IOException e) {

		}	
	}
	
	
	public void crearFlujoEntrada() {
		try {
			this.flujoEntrada = new ObjectInputStream(socket.getInputStream());
			System.out.println(flujoEntrada.readObject().toString());
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Creamos el flujo de entrada.");
	}
	
	public void heartbeats() {
		Thread receptorHB = new Thread(new ReceptorHeartbeats());
		receptorHB.start();
		
		
		
		
		
			/*Thread.sleep(10000);
			System.out.println("Datos available para lectura: "+ this.flujoEntrada.available());
			if (this.flujoEntrada.available()>0) {
				this.flujoEntrada.readObject();
				System.out.println("RECIBI LATIDO");
				this.heartbeats();    //mira esa recursividad papá!!
			}
			else
				this.respaldar();
		} catch (InterruptedException | IOException | ClassNotFoundException e) {

		} */
		
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
	
	
	private class ReceptorHeartbeats implements Runnable {
	    private int i;

	    @Override
	    public void run() {
	      /* while (true) {
	        	System.out.println("Iteracion del receptor");
	            try {
	                Object object = flujoEntrada.readObject();
	                System.out.println("Objeto: " + object.toString());
	            } catch (IOException | ClassNotFoundException e) {
	                e.printStackTrace();
	            }
	        } */
	    }
	}
	
	public void pingEcho() {
		
		
	}
	
	public void respaldar() {
		try {
			this.flujoSalidaRespaldo.writeObject(new NotificacionCaida());
			System.out.println("Enviando notificacion caida");
		} catch (IOException e) {

		}
		
	}

}
