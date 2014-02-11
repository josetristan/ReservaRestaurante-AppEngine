package com.jtristan.reservarestaurante.entidad;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;



@PersistenceCapable
public class Restaurante {
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String nombre;
	@Persistent
	private String estrellas;
	@Persistent(defaultFetchGroup="true")
	@Embedded
	private Direccion direccion;
	
	@Persistent	
	@Element(dependent="true")
	@Order(extensions = @Extension(vendorName="datanucleus", key="list-ordering", value="tipoPlato asc"))		
	private List<Carta> carta;
	@Persistent(mappedBy="restaurante", dependentElement="true")
	@Element(dependent="true")
	private Set<Oferta> ofertas = new HashSet<Oferta>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEstrellas() {
		return estrellas;
	}
	public void setEstrellas(String estrellas) {
		this.estrellas = estrellas;
	}
	public Direccion getDireccion() {
		return direccion;
	}
	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}
	public List<Carta> getCarta() {
		return carta;
	}
	public void setCarta(List<Carta> carta) {
		this.carta = carta;
	}
	public Set<Oferta> getOfertas() {
		return ofertas;
	}
	public void setOfertas(Set<Oferta> ofertas) {
		this.ofertas = ofertas;
	}
	
	
	
	

}
