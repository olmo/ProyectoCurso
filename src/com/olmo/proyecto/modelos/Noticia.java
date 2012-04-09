package com.olmo.proyecto.modelos;

public class Noticia {
	private int id;
	private String gid;
	private String titulo;
	private String autor;
	private String contenido;
	private String url;
	
	public Noticia() {
		
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	public String getGid() {
		return gid;
	}


	public void setGid(String id) {
		this.gid = id;
	}

	public String getTitulo() {
		return titulo;
	}


	public void setTitulo(String title) {
		this.titulo = title;
	}


	public String getAutor() {
		return autor;
	}


	public void setAutor(String author) {
		this.autor = author;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getContenido() {
		return contenido;
	}


	public void setContenido(String content) {
		this.contenido = content;
	}
	
}
