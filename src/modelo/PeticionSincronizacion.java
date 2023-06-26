package modelo;

import java.io.Serializable;

public class PeticionSincronizacion implements Serializable {
	private String nombre;
	private String nombrePropio;
	
	public PeticionSincronizacion() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public String getNombrePropio() {
		return nombrePropio;
	}
	
	
	

}
