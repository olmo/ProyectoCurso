package com.olmo.proyecto;

import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class ActualizarNoticiasService extends IntentService {
	
	public ActualizarNoticiasService() {
		super(ActualizarNoticiasService.class.getSimpleName());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int result = Activity.RESULT_CANCELED;
		
		try{
			
			GReader.getTags(ActualizarNoticiasService.this);
			GReader.getFeeds(ActualizarNoticiasService.this);
			GReader.getNoticias(ActualizarNoticiasService.this);
			result = Activity.RESULT_OK;
		}
		catch(IOException e){
			
		}
		
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Messenger messenger = (Messenger) extras.get("MESSENGER");
			Message msg = Message.obtain();
			msg.arg1 = result;
			try {
				messenger.send(msg);
			} catch (android.os.RemoteException e1) {
				Log.w(getClass().getName(), "Exception sending message", e1);
			}

		}
		
		// Programamos la siguiente actualización
		programarSiguienteActualizacion();
	}

	private void programarSiguienteActualizacion() {
		Intent intent = new Intent(this, this.getClass());
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Se actualiza cada 5 minutos

		long currentTimeMillis = System.currentTimeMillis();
		long nextUpdateTimeMillis = currentTimeMillis + 5 * DateUtils.MINUTE_IN_MILLIS;
		Time nextUpdateTime = new Time();
		nextUpdateTime.set(nextUpdateTimeMillis);
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
	}
}
