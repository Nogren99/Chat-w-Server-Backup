package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import modelo.Usuario;
import negocio.Monitor;
import negocio.ServerRespaldo;
import negocio.Servidor;
import vista.Ivista;
import vista.SistemaDeMensajeria;
import vista.Bienvenido;
import vista.Chat;
import vista.Inicio;
import vista.SalaDeEspera;

public class ControladorServidor implements ActionListener, Runnable {

	private Ivista vista;
    private static ControladorServidor instancia;
    private Thread comunicacion;
    private Monitor monitor;
    private boolean primario;

    
    public static ControladorServidor getInstancia() {
        if (instancia == null)
            instancia = new ControladorServidor();
        return instancia;
    }

	public ControladorServidor() {
        this.vista = new Inicio();
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
    @SuppressWarnings("deprecation")
	@Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
       
        if (comando.equalsIgnoreCase("Iniciar Sesión")) {
        	this.monitor= Monitor.getInstance();
        	Inicio ventana = (Inicio) this.vista;
        	
        	//Ingreso datos del servidor
        	int puerto = Integer.parseInt( ventana.getTextField_1().getText() );
        	Usuario.getInstance().setPuerto(puerto);
        	try {
        		Usuario.getInstance().setIp(InetAddress.getLocalHost().getHostAddress());
			}catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
        	System.out.println("Inicio en ip: "+ Usuario.getInstance().getIp() + "y en puerto: "+puerto);
        	
        	//Inicio hilo para que el servidor empieze a escuchar clientes
        	//ButtonGroup selectedGroup = ventana.getButtonGroup();
        	//JRadioButton selectedButton = (JRadioButton) ventana.getButtonGroup().getSelection();
        	if (ventana.getRdbtnNewRadioButton().isSelected()) {
        		System.out.println("Creando server principal");
        		primario=true;
        		System.out.println("Primario: "+ primario);
	            Thread hilo = new Thread(Servidor.getInstancia()); //server principal
	            hilo.start();
	            
        	} else if (ventana.getRdbtnNewRadioButton_1().isSelected()) {
        		primario=false;
        		System.out.println("Creando server secundario");
        		Thread hiloSecundario = new Thread(ServerRespaldo.getInstancia());
        		hiloSecundario.start();
        		
        	} else {
        		System.out.println("Validar ");
        	}
        	this.vista.cerrar();
        	
        } else if (comando.equalsIgnoreCase("CerrarServer")) {
        	Monitor.getInstance().respaldar();
        	this.vista.cerrar();
        }
        	
        	
          	
    } 
    
    public void ventanaEspera() {
    	this.vista.cerrar();
    	
    	this.setVista(new SalaDeEspera());
    	SalaDeEspera vista = (SalaDeEspera) this.vista;
    	
    	System.out.println("Es el primario: "+ this.primario);
    	if (!primario)
    		vista.getLblNewLabel().setText("Secundario");
    	else {
    		monitor.conectarServer("localhost", 1);
    		vista.getLblNewLabel().setText("Principal");
    	}
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
