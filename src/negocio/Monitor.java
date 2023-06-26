package negocio;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import modelo.ServidorCaido;

public class Monitor {
    private boolean primarioVivo;
    private int puerto;
    private Timer heartbeatTimer;
    Socket serverSecundario;
    ObjectOutputStream flujoSalida;

    public Monitor(int puerto) {
        this.primarioVivo = true;
        this.heartbeatTimer = new Timer();
        this.puerto = puerto;
        serverSecundario = null;
    }

    public void conectarServerSecundario(String ip, int puerto) {
        while (serverSecundario == null) {
            try {
                Thread.sleep(1000);
                serverSecundario = new Socket(ip, puerto);
                System.out.println("Conexion establecida con el servidor secundario.");
            } catch (IOException e) {
                System.out.println("No se pudo conectar con el servidor secundario...");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void empezarMonitoreo() throws IOException {
    	
        ServerSocket conexionPrimario = new ServerSocket(this.puerto);
        while (primarioVivo) {
            this.heartbeatTimer.schedule(new HeartbeatTask(), 3000);

            conexionPrimario.accept();
            this.recibirHeartbeat();
        }
        /*
        System.out.println("Primario no vivo");
        conexionPrimario.accept();
        primarioVivo = true;
        PrintWriter salida = new PrintWriter(this.serverSecundario.getOutputStream(), true);
        salida.println("reiniciar primario");
        conexionPrimario.close();
        this.empezarMonitoreo();*/
    }

    public void recibirHeartbeat() {
        this.heartbeatTimer.cancel();
        this.heartbeatTimer = new Timer();
        System.out.println("Se recibio latido del servidor primario.");
    }

    public void activarSecundario() {
        System.out.println("No se recibio latido del servidor primario en los ultimos 3 segs.");
        System.out.println("Activando servidor secundario...");
        try {
        	ServidorCaido server = (ServidorCaido) new ServidorCaido();
        	Socket tempo = new Socket("localhost",3);
        	flujoSalida = new ObjectOutputStream(tempo.getOutputStream());
        	flujoSalida.writeObject(server);
        	System.out.println("envie aviso al sv2");
        } catch (IOException e) {
            System.out.println("No se pudo activar el servidor secundario.");
        }
    }

    private class HeartbeatTask extends TimerTask {
        @Override
        public void run() {
            primarioVivo = false;
            activarSecundario();
        }
    }

}
