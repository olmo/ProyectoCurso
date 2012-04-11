package com.olmo.proyecto.modelos;

import java.util.ArrayList;

public class Feed {
	private int id;
	private String gid;
	private String shortid;
	private String nombre;
	private String web;
	private String feed;
	private ArrayList<Tag> tags = new ArrayList<Tag>();
	
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
	public String getShortid() {
		return shortid;
	}
	public void setShortid(String shorid) {
		this.shortid = shorid;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getFeed() {
		return feed;
	}
	public void setFeed(String feed) {
		this.feed = feed;
	}
	public ArrayList<Tag> getTags() {
		return tags;
	}
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
	
	
}
