package com.olmo.proyecto.modelos;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.olmo.proyecto.DatabaseHelper;

public class TagDB {
	public static final String KEY_ID = "id";
	public static final String KEY_GID = "gid";
	public static final String KEY_NOMBRE = "nombre";
	public static final String KEY_TERM = "term";
	public static final String DATABASE_TABLE = "Tags";
	public static final String DATABASE_CREATE ="" +
			"create table Tags (id integer primary key autoincrement, "
	          + "gid text unique, nombre text not null, term text not null);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
		
	public TagDB(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public TagDB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
			DBHelper.close();
	}
	
	public long insertTag(Tag tag)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_GID, tag.getGid());
		initialValues.put(KEY_NOMBRE, tag.getNombre());
		initialValues.put(KEY_TERM, tag.getTerm());
		return db.insert(DATABASE_TABLE, null, initialValues);	
	}
	
	public boolean deleteTag(long id)
	{
		return db.delete (DATABASE_TABLE,KEY_ID + "=" + id, null) > 0;
	}
	
	public ArrayList<Tag> getAll()
	{
		Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_NOMBRE, KEY_TERM}, null, null, null, null, null);
		
		ArrayList<Tag> tags = new ArrayList<Tag>();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			Tag tag = new Tag();
			tag.setId(cursor.getInt(0));
			tag.setGid(cursor.getString(1));
			tag.setNombre(cursor.getString(2));
			tag.setTerm(cursor.getString(3));
        	tags.add(tag);
       	    cursor.moveToNext();
        }
		
		cursor.close();
		
		return tags;
	}
	
	public Tag getTag(long id) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_NOMBRE, KEY_TERM}, KEY_ID + "=" + id, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			Tag tag = new Tag();
			tag.setId(cursor.getInt(0));
			tag.setGid(cursor.getString(1));
			tag.setNombre(cursor.getString(2));
			tag.setTerm(cursor.getString(3));
			
			return tag;
		}
		
        return null;
	}
	
	public boolean updateTag(Tag tag)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_ID, tag.getGid());
		args.put(KEY_NOMBRE, tag.getNombre());
		args.put(KEY_TERM, tag.getTerm());
		
	  return db.update(DATABASE_TABLE, args, KEY_ID + "=" + tag.getId(), null) > 0;
	  
	}
}
