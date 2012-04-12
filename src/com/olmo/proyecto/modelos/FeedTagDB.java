package com.olmo.proyecto.modelos;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.olmo.proyecto.DatabaseHelper;

public class FeedTagDB {
	public static final String KEY_ID = "id";
	public static final String KEY_TAG = "tag";
	public static final String KEY_FEED = "feed";
	public static final String DATABASE_TABLE = "feed_tag";
	public static final String DATABASE_CREATE ="" +
			"create table feed_tag (feed integer not null, tag integer not null, primary key (feed, tag));";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
		
	public FeedTagDB(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public FeedTagDB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
			DBHelper.close();
	}
		
	public long insertFeedTag(long feed, int tag)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TAG, tag);
		initialValues.put(KEY_FEED, feed);
		return db.insert(DATABASE_TABLE, null, initialValues);	
	}
	
	/*public boolean deleteFeed(long id)
	{
		return db.delete (DATABASE_TABLE,KEY_ID + "=" + id, null) > 0;
	}*/
	
	/*public ArrayList<Feed> getAll()
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
        	feeds.add(feed);
       	    cursor.moveToNext();
        }
		
		cursor.close();
		
		return feeds;
	}*/
	
	public ArrayList<Feed> getFeeds(long id) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_FEED, KEY_TAG}, KEY_TAG + "=" + id, null, null, null, null, null);
		
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		FeedDB feeddb = new FeedDB(context);
		feeddb.open();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			feeds.add(feeddb.getFeed(cursor.getInt(0)));
			cursor.moveToNext();
		}
		feeddb.close();
		cursor.close();
		
        return feeds;
	}
	
	public ArrayList<Tag> getTags(long id) throws SQLException
	{
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_FEED, KEY_TAG}, KEY_FEED + "=" + id, null, null, null, null, null);
		
		ArrayList<Tag> tags = new ArrayList<Tag>();
		TagDB tagdb = new TagDB(context);
		tagdb.open();
		
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			tags.add(tagdb.getTag(cursor.getInt(1)));
			cursor.moveToNext();
		}
		tagdb.close();
		cursor.close();
		
        return tags;
	}
	
	/*public boolean updateTag(int id, int feed, int tag)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_TAG, tag);
		args.put(KEY_FEED, feed);
		
	  return db.update(DATABASE_TABLE, args, KEY_ID + "=" + id, null) > 0;
	  
	}*/
}
