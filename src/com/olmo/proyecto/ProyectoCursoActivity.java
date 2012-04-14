package com.olmo.proyecto;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

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
		
		//if (googleAuthKey.equals("")){
			cuentas = new Cuentas(this,hand,this);
		//}
		
		startService(new Intent(this, ActualizarNoticiasService.class));
        
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