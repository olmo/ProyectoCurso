package com.olmo.proyecto;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.olmo.proyectocurso.R;

public class ProyectoCursoActivity extends Activity implements Callback {
	static String googleAuthKey = "";
	Cuentas cuentas = null;
	Handler hand= new Handler(this);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
		googleAuthKey = prefs.getString("googleAuthKey", "");
		
		if (googleAuthKey.equals("")){
			cuentas = new Cuentas(this,hand,this);
		}
		
		new Thread(new Runnable() {
	        public void run() {
	        	try{
	        		GReader.getNoticias(ProyectoCursoActivity.this);
	        	}
	        	catch(IOException e){
	    			
	    		}
	        }
	    }).start();
        
        setContentView(R.layout.main);
    }
    
    public boolean handleMessage(Message msg) {
		googleAuthKey = msg.getData().getString("googleAuthKey");
		if(!googleAuthKey.equals("")){
			SharedPreferences prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
			Editor mEditor = prefs.edit();
			mEditor.putString("googleAuthKey", googleAuthKey);
			mEditor.commit();
		}
		return false;
	}
}