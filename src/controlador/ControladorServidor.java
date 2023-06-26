package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.Usuario;
import negocio.Heartbeat;
import negocio.Servidor;
import vista.Ivista;
import vista.SistemaDeMensajeria;
import vista.Bienvenido;
import vista.Chat;
import vista.Inicio;
import vista.InicioNuevo;
import vista.SalaDeEspera;

public class ControladorServidor implements ActionListener, Runnable {

	private Ivista vista;
    private static ControladorServidor instancia;
    private Thread comunicacion;
    

    
    public static ControladorServidor getInstancia() {
        if (instancia == null)
            instancia = new ControladorServidor();
        return instancia;
    }

	public ControladorServidor() {
        this.vista = new InicioNuevo();
        this.vista.setActionListener(this);
        this.vista.mostrar();
    }
    
    public Ivista getVista(){
        return vista;
    }

    private void setVista(Ivista vista) {
        this.vista=vista;
        this.vista.setActionListener(this);
        this.vista.mostrar();
    }
    


    /**
     *Metodo que se encarga de escuchar la interaccion del servidor
     *Se ingresa el puerto por pantalla y se toma el ip de quien lo inicia
     *Luego se inicia el hilo correspondiente al servidor
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
       
        if (comando.equalsIgnoreCase("Iniciar Sesión")) {
        	InicioNuevo ventana = (InicioNuevo) this.vista;
        	
        	//Ingreso datos del servidor
        	int puerto = Integer.parseInt( ventana.getTextField_1().getText() );
        	//Usuario.getInstance().setPuerto(puerto);
        	try {
        		//Usuario.getInstance().setIp(InetAddress.getLocalHost().getHostAddress());
        		Servidor.getInstancia().setPuerto(puerto);
        	}catch (IOException e1) {
				e1.printStackTrace();
			}
        	System.out.println("Inicio en ip: " + "y en puerto: "+puerto);
        	
        	
        	if(ventana.getRdbtnNewRadioButton().isSelected()) {
        		System.out.println("servidor 1");
        		Heartbeat.getInstance().setIp("localhost");
                Heartbeat.getInstance().setPuerto(3000);
                Heartbeat.getInstance().start();
                System.out.println("HB Funcionando");
                Servidor.getInstancia().conectarConSecundario("localhost", 3); //ip y puerto del secundario
                System.out.println("Mensaje desde controlador: Conexion establecida con el servidor secundario.");
                //ControladorNuevosUsuarios.getInstance().start();
        	}
        	if(ventana.getRdbtnNewRadioButton_1().isSelected()) {
        		System.out.println("sv2");
        		try {
					Servidor.getInstancia().conectarConPrimario();
					System.out.println("Mensaje desde controlador: Conexion establecida con el servidor primario.");
					Servidor.getInstancia().conectarConMonitor(); // pendiente, espero encontrar quien manda objeto NotificadorCaida
					System.out.println("Mensaje desde controlador: Conexion establecida con el monitor.");
					Servidor.getInstancia().sincronizacionDeEstado();
        		 } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                //ControladorNuevosUsuarios.getInstance().start();
        	}
        	
        	/*
        	 * futuro upgrade: reabrir sv1
        	 * Servidor.getInstance().conectarConSecundario(ipSecundario, Integer.parseInt(puertoSecundario));
                Servidor.getInstance().recibirUsuariosAResincronizar();
        	 */
        	
        	//Inicio hilo para que el servidor empieze a escuchar clientes
            Thread hilo = new Thread(Servidor.getInstancia());
            hilo.start();
        	this.vista.cerrar();
        	
        }else if (comando.equalsIgnoreCase("Cerrar Servidor")) {
        	System.out.println("CHAU SESION");
        	
        	Servidor.getInstancia().cerrarServidor();
        	 Heartbeat.getInstance().interrupt();
        }
    }
    
    public void ventanaEspera() {
    	this.vista.cerrar();
    	this.setVista(new SalaDeEspera());
    }
    
    public void ventanaChat() {
    	this.vista.cerrar();
    	this.setVista(new Chat());
    	this.comunicacion = new Thread(this);
    	this.comunicacion.start();
    }
    
    public void cerrarVentana() {
    	System.out.println("cierro ventana");
    	this.comunicacion.interrupt();
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	
}
