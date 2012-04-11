package com.olmo.proyecto.modelos;

public class Tag {
	private int id;
	private String gid;
	private String shortid;
	private String nombre;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getShortid() {
		return shortid;
	}
	public void setShortid(String term) {
		this.shortid = term;
	}
}
