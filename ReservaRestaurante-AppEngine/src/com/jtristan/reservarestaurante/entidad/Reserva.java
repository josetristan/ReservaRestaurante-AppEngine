package com.jtristan.reservarestaurante.entidad;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.datanucleus.annotations.Unowned;

@PersistenceCapable
public class Reserva {
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	@Unowned
	private Usuario usuario;
	@Persistent
	@Unowned	
	private Restaurante restaurante;
	@Persistent
	@Unowned
	private Set<Usuario> comensales = new HashSet<Usuario>();
	@Persistent
	private Date horaReserva;
	@Persistent
	private int numeroPersonas;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public int getNumeroPersonas() {
		return numeroPersonas;
	}
	public void setNumeroPersonas(int numeroPersonas) {
		this.numeroPersonas = numeroPersonas;
	}
	public Date getHoraReserva() {
		return horaReserva;
	}
	public void setHoraReserva(Date horaReserva) {
		this.horaReserva = horaReserva;
	}	
	public Set<Usuario> getComensales() {
		return comensales;
	}
	public void setComensales(Set<Usuario> comensales) {
		this.comensales = comensales;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Restaurante getRestaurante() {
		return restaurante;
	}
	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}
			
	

}
