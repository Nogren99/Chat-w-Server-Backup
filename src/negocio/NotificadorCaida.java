package negocio;


//import server.ControladorNuevosUsuarios;
//import server.Servidor;
//import server.SocketUsuario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class NotificadorCaida extends Thread {
    private Socket socket;
    private InputStreamReader entradaSocket;
    private PrintWriter salida;
    private BufferedReader entrada;

    public NotificadorCaida(Socket socket) throws IOException {
        this.socket = socket;
        this.entradaSocket = new InputStreamReader(socket.getInputStream());
        this.entrada = new BufferedReader(entradaSocket);
        this.salida = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
    	System.out.println("NOTIFICADOR Servidor secundario activado");
    	/*
        try {
            while (true) {
                //String mensaje = this.entrada.readLine();
                
                //ACTIVAR SECUNDARIO
                
                	
                System.out.println("NOTIFICADOR Servidor secundario activado");
                /*
                ArrayList<String> usuariosDesconectados = new ArrayList<String>();
                for (Map.Entry<String, SocketUsuario> entry : Servidor.getInstance().getUsuarios().entrySet()) {
                    if (!entry.getValue().notificarCaida()) {
                        usuariosDesconectados.add(entry.getKey());
                    }
                }
                for (String usuario : usuariosDesconectados) {
                    Servidor.getInstance().getUsuarios().remove(usuario);
                }*/
                System.out.println("NOTIFICADOR Estado resincronizado");
                    
                
                /*
                mensaje = this.entrada.readLine();
                if (mensaje.equals("REINICIAR PRIMARIO") {
                    System.out.println("Servidor primario reiniciado. Resincronizando...");
                    Servidor.getInstance().conectarConPrimario();
                    Servidor.getInstance().informarUsuariosAlPrimario();
                    for (Map.Entry<String, SocketUsuario> entry : Servidor.getInstance().getUsuarios().entrySet()) {
                        entry.getValue().reinicioPrimario();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }
}
