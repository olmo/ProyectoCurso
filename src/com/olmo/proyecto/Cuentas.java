package com.olmo.proyecto;

import java.io.IOException;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Cuentas {
	private Thread initBkgdThread = null;
	Context cont = null;
	Handler hand = null;
	private Activity activity;
	
	public Cuentas(Context c, Handler h, Activity activity) {
		
		this.activity = activity;
		this.cont=c;
		this.hand=h;
		
		//se crea la hebra
		initBkgdThread = new Thread(new Runnable() {
			public void run() {
				sendMessage(getUserToken());
			}
		});
		//se inicia la hebra
		initBkgdThread.start();
		
	}
	
	private void sendMessage(String what) {
		Bundle bundle = new Bundle();
		bundle.putString("googleAuthKey", what);
		Message message = new Message();
		message.setData(bundle);
		hand.sendMessage(message);
	}
	
	public String getUserToken()
	{
		AccountManager accountManager = AccountManager.get(activity);
	    AccountManagerFuture<Bundle> amf = accountManager.getAuthTokenByFeatures("com.google", "reader", null, activity, Bundle.EMPTY, Bundle.EMPTY, null, null );

	    Bundle bundle = null;
	    try {
	        bundle = amf.getResult();
	        //String name = (String) bundle.get(AccountManager.KEY_ACCOUNT_NAME);
	        //String type = (String) bundle.get(AccountManager.KEY_ACCOUNT_TYPE);
	        String authKey = bundle.getString(AccountManager.KEY_AUTHTOKEN); 
	        String nomKey = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
	        return authKey;
	    } catch (OperationCanceledException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (AuthenticatorException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}
