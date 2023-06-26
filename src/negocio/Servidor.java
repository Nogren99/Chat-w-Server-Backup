package negocio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import controlador.ControladorCliente;
import controlador.ControladorServidor;
import modelo.ActualizarLista;
import modelo.ClienteNoDisponible;
import modelo.ConexionTerminada;
import modelo.ConfirmacionSolicitud;
import modelo.Mensaje;
import modelo.MensajeCliente;
import modelo.SolicitudMensaje;
import modelo.Usuario;
import modelo.PeticionSincronizacion;
import modelo.ServidorCaido;
import modelo. DatosSincronizacion;
import negocio.Monitor;

public class Servidor implements Runnable {
    private static Servidor instancia;
    private static ControladorServidor controlador;
    
    private boolean secambio=false;

    //private Usuario user;
    private ServerSocket socketServer;
    Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private InputStreamReader inSocket;
    private ArrayList<Socket> sockets = new ArrayList<Socket>();
    private HashMap<String, Integer> clientes; //Nombre / puerto
    Socket serverRedundante;
    private ServerSocket socketServerSecundario;
    private ServerSocket socketServerNuevosUsuarios;
    private NotificadorCaida monitor;
    private int puerto;

    private Servidor() {
        //user = Usuario.getInstance();
        clientes = new HashMap<>();
    }

    public static Servidor getInstancia() {
        if (instancia == null) {
            instancia = new Servidor();
        }
        return instancia;
    }
    
    public void addCliente(String nombre, int puerto) {
    	this.clientes.put(nombre, puerto);
    }
    
	public Socket getSocket() {
		return socket;
	}

    @Override
    public void run() {
            try {
            	if (this.socketServer==null)
            		this.socketServer = new ServerSocket(puerto);
				System.out.println("Servidor iniciado. Puerto: " + puerto);
				controlador.getInstancia().ventanaEspera();
				while (true) {
					socket = socketServer.accept();
					sockets.add(socket);
					
					Thread clientThread = new Thread(new EscucharCliente(socket));
	                clientThread.start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
    }
    
    private class EscucharCliente implements Runnable {
        private Socket cliente;

        public EscucharCliente(Socket cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {
            try {
            	ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
        		ObjectOutputStream flujoSalida = new ObjectOutputStream(cliente.getOutputStream());
        		
        		while (true) {	
            		
                    Object object = flujoEntrada.readObject();
                    
                    if (object instanceof MensajeCliente) {
                    	MensajeCliente datos = (MensajeCliente) object;
                    	Servidor.getInstancia().addCliente(datos.getName(), datos.getPuerto());

	                		for (int k=0; k < sockets.size() ; k++) { 			
			                    ObjectOutputStream listaClientes = new ObjectOutputStream(sockets.get(k).getOutputStream());
			                    listaClientes.writeObject(Servidor.getInstancia().getClientes());
			                    listaClientes.flush();
	                		}

	                		Iterator<Map.Entry<String, Integer>> iterator = clientes.entrySet().iterator();
		                    while (iterator.hasNext()) {
		                        Map.Entry<String, Integer> entry = iterator.next();
		                        String nombre = entry.getKey();
		                        Integer puerto = entry.getValue();
		                    }	
		                    
		                    System.out.println("Recibi a un wacho "+ datos.getName());
		                    
		                    
		                    
		                  //ENVIO DATOS AL SERVIDOR SECUNDARIO SI ES QUE EXISTE
		            		
		            		if(serverRedundante!=null) {
		            		  int puertoAux = serverRedundante.getPort() - 1;
		       				  Socket temporal = new Socket ("localhost",puertoAux);
		       				  flujoSalida = new ObjectOutputStream(temporal.getOutputStream());
		       				  flujoSalida.writeObject(datos);
		            		}
	                	}
                	
                      else if (object instanceof SolicitudMensaje) {
                    	//Estructura recibida:  (nombre del usuario al que le escribo , nombre mio)
                    	SolicitudMensaje soli = (SolicitudMensaje) object;
                    	//System.out.println(Servidor.getInstancia().getClientes().get(soli.getNombre()));
                    	int puerto = Servidor.getInstancia().getClientes().get(soli.getNombre());
                    	int i=0;
                    	while (i<sockets.size() && sockets.get(i).getPort()!=puerto) {
                    		//System.out.println("Sochet numero "+ i + " Puerto :" +sockets.get(i).getLocalPort());
                    		i++;
                    	}
                    	
                    	try {
                			flujoSalida = new ObjectOutputStream(sockets.get(i).getOutputStream());
                			flujoSalida.writeObject(new SolicitudMensaje(soli.getNombre(),soli.getNombrePropio()));
                		} catch (IOException e) {
                			
                		} 
                    	
                		
                      }else if (object instanceof ConfirmacionSolicitud){
                    	ConfirmacionSolicitud confirmacion = (ConfirmacionSolicitud) object; 
                    	int puerto = Servidor.getInstancia().getClientes().get(confirmacion.getNombreSolicitante());
                    	
                    	int i=0;
                    	while (i<sockets.size() && sockets.get(i).getPort()!=puerto) {
                    		//System.out.println("Sokket numero "+ i + " Puerto :" +sockets.get(i).getLocalPort());
                    		i++;
                    	}
                    	
                    	flujoSalida = new ObjectOutputStream(sockets.get(i).getOutputStream());
                      	flujoSalida.writeObject(confirmacion.isConfirmacion());
  
                      } else if (object instanceof Mensaje){
                    	  Mensaje mensaje = (Mensaje) object;
                    	  int puerto = Servidor.getInstancia().getClientes().get(mensaje.getNombreDestinatario());
                    	  int i=0;
                    	  
                      	  while (i<sockets.size() && sockets.get(i).getPort()!=puerto) {
                      		//System.out.println("Soquet numero "+ i + " Puerto :" +sockets.get(i).getLocalPort());
                      		i++;
                      	  }
                      	  
                      	  //System.out.println("Enviando mensaje "+ mensaje.toString() + "al cliente "+ mensaje.getNombreDestinatario());
                      	  flujoSalida = new ObjectOutputStream(sockets.get(i).getOutputStream());
                          flujoSalida.writeObject(mensaje);
                    	  
                      } else if (object instanceof ClienteNoDisponible){
                    	  ClienteNoDisponible cdp = (ClienteNoDisponible) object;
                    	  int puerto = Servidor.getInstancia().getClientes().get(cdp.getNombre());
                    	  int i=0;
                    	  
                      	  while (i<sockets.size() && sockets.get(i).getPort()!=puerto) {
                      		//System.out.println("Soquet numero "+ i + " Puerto :" +sockets.get(i).getLocalPort());
                      		i++;
                      	  }
                      	  
                      	flujoSalida = new ObjectOutputStream(sockets.get(i).getOutputStream());
                        flujoSalida.writeObject(cdp);
                    	  
                      }else if (object instanceof ConexionTerminada){
                    	  ConexionTerminada conexionTerminada = (ConexionTerminada) object;
                    	  int puerto = Servidor.getInstancia().getClientes().get(conexionTerminada.getNombreDestinatario());
                    	  int i=0;
                    	  
                      	  while (i<sockets.size() && sockets.get(i).getPort()!=puerto) {
                      		//System.out.println("Soquet numero "+ i + " Puerto :" +sockets.get(i).getLocalPort());
                      		i++;
                      	  }
                      	  
                      	flujoSalida = new ObjectOutputStream(sockets.get(i).getOutputStream());
                        flujoSalida.writeObject(conexionTerminada);
                    	  
                    	  
                      } else if (object instanceof ActualizarLista){
                    	  ActualizarLista act = (ActualizarLista) object;
                    	  
                    	  int puerto = Servidor.getInstancia().getClientes().get(act.getNombre());
                    	  int i=0;
                    	  
                      	  while (i<sockets.size() && sockets.get(i).getPort()!=puerto) {
                      		//System.out.println("Soquet numero "+ i + " Puerto :" +sockets.get(i).getLocalPort());
                      		i++;
                      	  }
                      	  
                      	flujoSalida = new ObjectOutputStream(sockets.get(i).getOutputStream());
                        flujoSalida.writeObject(Servidor.getInstancia().getClientes());
                    	  
                        
                        //=============PARA SERVIDOR DE RESPALDO======================
                        
                      } else if (object instanceof PeticionSincronizacion){
                    	  
                    	System.out.println("===Recibi peticion de sincronizacion ===");
                    	
                    	DatosSincronizacion datos = (DatosSincronizacion) new DatosSincronizacion();
                    	
                    	datos.setClientes(clientes);
                    	

                        int puertoAux = serverRedundante.getPort() - 1;
                      	
                    	Socket temporal = new Socket ("localhost",puertoAux);
                      	flujoSalida = new ObjectOutputStream(temporal.getOutputStream());
                        flujoSalida.writeObject(datos);
                    	  
                      }else if (object instanceof DatosSincronizacion){
                    	  
                    	System.out.println("===Recibi los daots para sincronizarme :) ===");
                    	DatosSincronizacion datos = (DatosSincronizacion) object;
                    	clientes=datos.getClientes();
                    	System.out.println("recibi Todos los clientes satisfactoriamente!");
                    	  
                      }else if (object instanceof ServidorCaido){
                    	  
                    	  
                    	  System.out.println("apa la papa se cayo el sv");
                    	  
                    	  
                    	  for (Map.Entry<String, Integer> entry : clientes.entrySet()) {
                              String cliente = entry.getKey();
                              int puerto = entry.getValue();

                              try {
                                  Socket socket = new Socket("localhost", puerto);
                                  sockets.add(socket);
                              } catch (IOException e) {
                                  // Manejar cualquier excepción de conexión aquí
                                  e.printStackTrace();
                              }
                          }
                    	  
                      	  
                        }
                      
                      else {
                    	System.out.println(object.toString());
                    }
            	}
            } catch (IOException | ClassNotFoundException e) {

            }
            
        }
    }
    
    public void setPuerto(int puerto) throws IOException {
        this.puerto = puerto;
        this.socketServer = new ServerSocket(puerto);
        this.socketServerSecundario = new ServerSocket(puerto + 1);
    }
    
    public void conectarConPrimario() throws IOException {
        System.out.println("Esperando solicitud del servidor primario...");
        //this.socketServerSecundario = new ServerSocket(3);
        this.serverRedundante = this.socketServerSecundario.accept();
        System.out.println("Conexion establecida con el servidor primario.");
    }
    
    public void conectarConSecundario(String IP, int puertoSecundario) {
        System.out.println("Enviando solicitud al servidor secundario...");
        while (this.serverRedundante == null) {
            try {
                this.serverRedundante = new Socket(IP, puertoSecundario+1);//ANTES TENIA PUERTO+1
                
                System.out.println("Conexion establecida con el servidor secundario.");
            } catch (IOException e) {
                System.out.println("No se pudo conectar con el servidor secundario. Reintentando en 5 segundos...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }
    
    public void conectarConMonitor() throws IOException {
        System.out.println("Esperando conexion desde el monitor... puerto "+puerto);
        //if (this.socketServer==null)
        	//this.socketServer = new ServerSocket(user.getPuerto()); 
        System.out.println("svsocket:"+socketServer+" espero en accept");
        Socket socket = this.socketServer.accept();
        System.out.println("ya acepte");
        this.monitor = new NotificadorCaida(socket);
        System.out.println("Conexion establecida con el monitor.");
        this.monitor.start();
    }
    
	public void sincronizacionDeEstado() {
		PeticionSincronizacion peti = (PeticionSincronizacion) new PeticionSincronizacion();
		ObjectOutputStream pidoSincronizacion;
		try {
			socket = new Socket("localhost", 1);
			pidoSincronizacion = new ObjectOutputStream(socket.getOutputStream());
			pidoSincronizacion.writeObject(peti);
			pidoSincronizacion.flush();
			System.out.println("==Envie solicitud de sincronizacion");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

               

    public static ControladorServidor getControlador() {
		return controlador;
	}
/*
	public Usuario getUser() {
		return user;
	}*/

	public ServerSocket getSocketServer() {
		return socketServer;
	}

	public PrintWriter getOut() {
		return out;
	}

	public BufferedReader getIn() {
		return in;
	}

	public InputStreamReader getInSocket() {
		return inSocket;
	}

	public HashMap<String, Integer> getClientes() {
		return clientes;
	}

	public boolean isSecambio() {
		return secambio;
	}

	public void setSecambio(boolean secambio) {
		this.secambio = secambio;
	}


   
}