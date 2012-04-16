package com.olmo.proyecto;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.olmo.proyectocurso.R;

public class ProyectoCursoActivity extends Activity {
	static String googleAuthKey = "";
	private String authtwitter = "";
	private String secrettwitter = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
		
		googleAuthKey = prefs.getString("googleAuthKey", "");
		authtwitter = prefs.getString("authtwitter", "");
		secrettwitter = prefs.getString("secrettwitter", "");
		
		if (googleAuthKey.equals("") || authtwitter.equals("")){
			Intent intent = new Intent(ProyectoCursoActivity.this, ConfiguracionCuentasActivity.class);
	        startActivity(intent);
		}
		else{
			startService(new Intent(this, ActualizarNoticiasService.class));
		}
		
		ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(false);
        
        setContentView(R.layout.main);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.principal, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.Twitter:
        	
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
}