package com.jtristan.reservarestaurante.entidad;

import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Oferta {
	
	public enum Dias{LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO}
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String clave;
	
	@Persistent
	private String descripcion;
	@Persistent
	private List<Dias> diasDisponibles;
	@Persistent(mappedBy="ofertas")
	private Restaurante restaurante;
	
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public List<Dias> getDiasDisponibles() {
		return diasDisponibles;
	}
	public void setDiasDisponibles(List<Dias> diasDisponibles) {
		this.diasDisponibles = diasDisponibles;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public Restaurante getRestaurante() {
		return restaurante;
	}
	
	
	
	
	

}
