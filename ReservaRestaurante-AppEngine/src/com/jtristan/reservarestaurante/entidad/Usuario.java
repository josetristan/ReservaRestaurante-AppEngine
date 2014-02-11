package com.jtristan.reservarestaurante.entidad;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


import com.google.appengine.api.users.User;

@PersistenceCapable
public class Usuario {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Long id;
	
	@Persistent
	private String nombre;
	@Persistent
	private String apellidos;
	@Persistent
	private User cuentaGoogle;
	
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
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public User getCuentaGoogle() {
		return cuentaGoogle;
	}
	public void setCuentaGoogle(User cuentaGoogle) {
		this.cuentaGoogle = cuentaGoogle;
	}
	

}
