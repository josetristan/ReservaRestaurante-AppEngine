package com.jtristan.reservarestaurante.entidad;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Carta {
		
	public enum TipoDePlato{ENTRANTE, PRIMER_PLATO, SEGUNDO_PLATO, POSTRE, OTROS}
		
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key clave;
	
	@Persistent
	private TipoDePlato tipoPlato;
	@Persistent
	private String nombre;
	@Persistent
	private double precio;
	
	public Key getClave() {
		return clave;
	}
	public void setClave(Key clave) {
		this.clave = clave;
	}
	public TipoDePlato getTipoPlato() {
		return tipoPlato;
	}
	public void setTipoPlato(TipoDePlato tipoPlato) {
		this.tipoPlato = tipoPlato;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	

}
