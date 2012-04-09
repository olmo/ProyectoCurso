package com.olmo.proyecto.modelos;

import java.util.ArrayList;

import com.olmo.proyecto.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class NoticiaDB {
		public static final String KEY_ID = "id";
		public static final String KEY_GID = "gid";
		public static final String KEY_TITULO = "titulo";
		public static final String KEY_AUTOR = "autor";
		public static final String KEY_CONTENIDO = "contenido";
		public static final String KEY_URL = "url";
		public static final String TAG = "DBAdapter";
		public static final String DATABASE_TABLE = "Noticias";
		public static final String DATABASE_CREATE ="" +
				"create table Noticias (id integer primary key autoincrement, "
		          + "gid text unique, titulo text not null, autor text not null, contenido text not null, url text not null);";
		private final Context context;
		private DatabaseHelper DBHelper;
		private SQLiteDatabase db;
		
	public NoticiaDB(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public NoticiaDB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
			DBHelper.close();
	}
	
	public long insertNoticia(Noticia noticia)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_GID, noticia.getGid());
		initialValues.put(KEY_TITULO, noticia.getTitulo());
		initialValues.put(KEY_AUTOR, noticia.getAutor());
		initialValues.put(KEY_CONTENIDO, noticia.getContenido());
		initialValues.put(KEY_URL, noticia.getUrl());
		return db.insert(DATABASE_TABLE, null, initialValues);	
	}
	
	public boolean deleteNoticia(long id)
	{
		return db.delete (DATABASE_TABLE,KEY_ID + "=" + id, null) > 0;
	}
	
	public ArrayList<Noticia> getAll()
	{
		Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_TITULO, KEY_AUTOR, KEY_CONTENIDO, KEY_URL}, null, null, null, null, null);
		
		ArrayList<Noticia> noticias = new ArrayList<Noticia>();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			Noticia noticia = new Noticia();
			noticia.setId(cursor.getInt(0));
			noticia.setGid(cursor.getString(1));
			noticia.setTitulo(cursor.getString(2));
			noticia.setAutor(cursor.getString(3));
			noticia.setContenido(cursor.getString(4));
			noticia.setUrl(cursor.getString(5));
        	noticias.add(noticia);
       	    cursor.moveToNext();
        }
		
		cursor.close();
		
		return noticias;
	}
	
	public Noticia getNoticia(long id) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_TITULO, KEY_AUTOR, KEY_CONTENIDO, KEY_URL}, KEY_ID + "=" + id, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			Noticia noticia = new Noticia();
			noticia.setId(cursor.getInt(0));
			noticia.setGid(cursor.getString(1));
			noticia.setTitulo(cursor.getString(2));
			noticia.setAutor(cursor.getString(3));
			noticia.setContenido(cursor.getString(4));
			noticia.setUrl(cursor.getString(5));
			
			return noticia;
		}
		
        return null;
	}
	
	public boolean updateNoticia(Noticia noticia)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_ID, noticia.getGid());
		args.put(KEY_TITULO, noticia.getTitulo());
		args.put(KEY_AUTOR, noticia.getAutor());
		args.put(KEY_CONTENIDO, noticia.getContenido());
		args.put(KEY_URL, noticia.getUrl());
		
	  return db.update(DATABASE_TABLE, args, KEY_ID + "=" + noticia.getId(), null) > 0;
	  
	}
	
}
