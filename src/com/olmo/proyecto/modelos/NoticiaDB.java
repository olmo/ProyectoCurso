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
	public static final String KEY_TIME = "timestamp";
	public static final String KEY_FEED = "feed";
	public static final String TAG = "DBAdapter";
	public static final String DATABASE_TABLE = "Noticias";
	public static final String DATABASE_CREATE ="" +
			"create table Noticias (id integer primary key autoincrement, "
	          + "gid text unique, titulo text not null, autor text not null, contenido text not null, url text not null, timestamp integer not null, feed integer not null);";
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
		initialValues.put(KEY_TIME, noticia.getTimestamp().getTime());
		initialValues.put(KEY_FEED, noticia.getFeed().getId());
		return db.insert(DATABASE_TABLE, null, initialValues);	
	}
	
	public boolean deleteNoticia(long id)
	{
		return db.delete (DATABASE_TABLE,KEY_ID + "=" + id, null) > 0;
	}
	
	public ArrayList<Noticia> getAll()
	{
		Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_TITULO, KEY_AUTOR, KEY_CONTENIDO, KEY_URL, KEY_TIME, KEY_FEED}, null, null, null, null, KEY_TIME + " DESC");
		
		ArrayList<Noticia> noticias = new ArrayList<Noticia>();
		
		FeedDB feeddb = new FeedDB(context);
		feeddb.open();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			Noticia noticia = new Noticia();
			noticia.setId(cursor.getInt(0));
			noticia.setGid(cursor.getString(1));
			noticia.setTitulo(cursor.getString(2));
			noticia.setAutor(cursor.getString(3));
			noticia.setContenido(cursor.getString(4));
			noticia.setUrl(cursor.getString(5));
			noticia.setTimestamp(cursor.getInt(6));
			noticia.setFeed(feeddb.getFeed(cursor.getInt(7)));
        	noticias.add(noticia);
       	    cursor.moveToNext();
        }
		
		feeddb.close();
		
		cursor.close();
		
		return noticias;
	}
	
	public Noticia getNoticia(long id) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_TITULO, KEY_AUTOR, KEY_CONTENIDO, KEY_URL, KEY_TIME, KEY_FEED}, KEY_ID + "=" + id, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			Noticia noticia = new Noticia();
			noticia.setId(cursor.getInt(0));
			noticia.setGid(cursor.getString(1));
			noticia.setTitulo(cursor.getString(2));
			noticia.setAutor(cursor.getString(3));
			noticia.setContenido(cursor.getString(4));
			noticia.setUrl(cursor.getString(5));
			noticia.setTimestamp(cursor.getInt(6));
			
			FeedDB feeddb = new FeedDB(context);
			feeddb.open();
			noticia.setFeed(feeddb.getFeed(cursor.getInt(7)));
			feeddb.close();
			
			cursor.close();
			
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
		args.put(KEY_URL, noticia.getTimestamp().getTime());
		args.put(KEY_FEED, noticia.getFeed().getId());
		
	  return db.update(DATABASE_TABLE, args, KEY_ID + "=" + noticia.getId(), null) > 0;
	  
	}
	
}
