package main;

import negocio.Monitor;

import java.util.Scanner;

public class MonitorMain {
    public static void main(String[] args) {

        try {

            int puertoMonitor = 3000;  // Por defecto esta en el puerto 3000, sino, en lo que se pase como parametro
            String ipSecundario = "localhost";
            String puertoSecundario = "3";
            Scanner sc = new Scanner(System.in);

            /*
            String modoMonitor = null;

            System.out.println("Ingrese puerto para este monitor: ");
            puertoMonitor = Integer.parseInt(sc.nextLine());
            System.out.println("Ingrese IP del servidor secundario: ");
            ipSecundario = sc.nextLine();
            System.out.println("Ingrese puerto del servidor secundario: ");
            puertoSecundario = sc.nextLine();
            */
            System.out.println("monitor funcionando");
            
            Monitor monitor = new Monitor(puertoMonitor); // puerto para el monitor
            monitor.conectarServerSecundario(ipSecundario, Integer.parseInt(puertoSecundario));

            monitor.empezarMonitoreo();
            
            System.out.println("monitor funcionando");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
