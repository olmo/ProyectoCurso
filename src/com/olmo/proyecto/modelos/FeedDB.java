package com.olmo.proyecto.modelos;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.olmo.proyecto.DatabaseHelper;

public class FeedDB {
	public static final String KEY_ID = "id";
	public static final String KEY_GID = "gid";
	public static final String KEY_SHORTID = "shortid";
	public static final String KEY_NOMBRE = "nombre";
	public static final String KEY_WEB = "web";
	public static final String KEY_FEED = "feed";
	public static final String DATABASE_TABLE = "Feeds";
	public static final String DATABASE_CREATE ="" +
			"create table Feeds (id integer primary key autoincrement, "
	          + "gid text unique, shortid text not null, nombre text not null, web text not null, feed text not null);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
		
	public FeedDB(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public FeedDB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
			DBHelper.close();
	}
	
	public long insertFeed(Feed feed)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_GID, feed.getGid());
		initialValues.put(KEY_SHORTID, feed.getShortid());
		initialValues.put(KEY_NOMBRE, feed.getNombre());
		initialValues.put(KEY_WEB, feed.getWeb());
		initialValues.put(KEY_FEED, feed.getFeed());
		
		long row = db.insert(DATABASE_TABLE, null, initialValues);
		
		if(row!=-1){
			FeedTagDB ftdb = new FeedTagDB(context);
			ftdb.open();
			for(Tag tag : feed.getTags()){
				ftdb.insertFeedTag(row, tag.getId());
			}
			ftdb.close();
		}
		
		return row;
	}
	
	public boolean deleteFeed(long id)
	{
		return db.delete (DATABASE_TABLE,KEY_ID + "=" + id, null) > 0;
	}
	
	public ArrayList<Feed> getAll()
	{
		Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_SHORTID, KEY_NOMBRE, KEY_WEB, KEY_FEED}, null, null, null, null, null);
		
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			Feed feed = new Feed();
			feed.setId(cursor.getInt(0));
			feed.setGid(cursor.getString(1));
			feed.setShortid(cursor.getString(2));
			feed.setNombre(cursor.getString(3));
			feed.setWeb(cursor.getString(4));
			feed.setFeed(cursor.getString(5));
			
			FeedTagDB ftdb = new FeedTagDB(context);
			ftdb.open();
			feed.setTags(ftdb.getTags(cursor.getInt(0)));
			ftdb.close();
			
        	feeds.add(feed);
       	    cursor.moveToNext();
        }
		
		cursor.close();
		
		return feeds;
	}
	
	public Feed getFeed(long id) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_SHORTID, KEY_NOMBRE, KEY_WEB, KEY_FEED}, KEY_ID + "=" + id, null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			Feed feed = new Feed();
			feed.setId(cursor.getInt(0));
			feed.setGid(cursor.getString(1));
			feed.setShortid(cursor.getString(2));
			feed.setNombre(cursor.getString(3));
			feed.setWeb(cursor.getString(4));
			feed.setFeed(cursor.getString(5));
			
			FeedTagDB ftdb = new FeedTagDB(context);
			ftdb.open();
			feed.setTags(ftdb.getTags(cursor.getInt(0)));
			ftdb.close();
			
			cursor.close();
			
			return feed;
		}
		
        return null;
	}
	
	public Feed getFeed(String gid) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_GID, KEY_SHORTID, KEY_NOMBRE, KEY_WEB, KEY_FEED}, KEY_GID + "= '" + gid + "'", null, null, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			Feed feed = new Feed();
			feed.setId(cursor.getInt(0));
			feed.setGid(cursor.getString(1));
			feed.setShortid(cursor.getString(2));
			feed.setNombre(cursor.getString(3));
			feed.setWeb(cursor.getString(4));
			feed.setFeed(cursor.getString(5));
			
			FeedTagDB ftdb = new FeedTagDB(context);
			ftdb.open();
			feed.setTags(ftdb.getTags(cursor.getInt(0)));
			ftdb.close();
			
			cursor.close();
			
			return feed;
		}
		
        return null;
	}
	
	public boolean updateTag(Feed feed)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_GID, feed.getGid());
		args.put(KEY_SHORTID, feed.getShortid());
		args.put(KEY_NOMBRE, feed.getNombre());
		args.put(KEY_WEB, feed.getWeb());
		args.put(KEY_FEED, feed.getFeed());
		
		//TODO Actualizar tags si cambian.
		
	  return db.update(DATABASE_TABLE, args, KEY_ID + "=" + feed.getId(), null) > 0;
	  
	}
}
