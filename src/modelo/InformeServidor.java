package modelo;

import java.io.Serializable;

public class InformeServidor implements Serializable {
	private String nombre;
	private String nombrePropio;
	private int puerto;
	
	public InformeServidor() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public String getNombrePropio() {
		return nombrePropio;
	}
	
	
	

}
