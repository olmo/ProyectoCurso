package com.olmo.proyecto;

import com.olmo.proyecto.modelos.NoticiaDB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "ProyectoDB";
	public static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context)
	{
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//TODO Comprobar si la BD est� creada o actualizada y si no crearla. (Almacenarlo en un sharedpreferences)
		try {
		    db.execSQL(NoticiaDB.DATABASE_CREATE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(NoticiaDB.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS contacts");
		
		onCreate(db);
	}
	
}