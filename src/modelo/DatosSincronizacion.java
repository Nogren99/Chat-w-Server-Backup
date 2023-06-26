package modelo;

import java.io.Serializable;
import java.util.HashMap;

public class DatosSincronizacion implements Serializable {
	private String nombre;
	private String nombrePropio;
	private HashMap<String, Integer> clientes;
	
	public HashMap<String, Integer> getClientes() {
		return clientes;
	}

	public void setClientes(HashMap<String, Integer> clientes) {
		this.clientes = clientes;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setNombrePropio(String nombrePropio) {
		this.nombrePropio = nombrePropio;
	}

	public DatosSincronizacion() {
		super();
	}

	public String getNombre() {
		return nombre;
	}

	public String getNombrePropio() {
		return nombrePropio;
	}
	
	
	

}
