package com.olmo.proyecto;

import java.io.IOException;

import twitter4j.auth.AccessToken;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Cuentas {
	private Thread initBkgdThread = null;
	Context cont = null;
	Handler hand = null;
	private Activity activity;
	//private MessageHandler handler = new MessageHandler();
	private static final int MSG_GOT_AUTH_TOKEN = 100;
    private static final int MSG_GOT_AUTH_SECRET = 101;
    private static final int MSG_NO_AUT_TOKEN_RECVD = 102;
    String authtoketwit="";
	
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
	    /*AccountManagerFuture<Bundle> amf2 = accountManager.getAuthTokenByFeatures("com.twitter.android.auth.login", "com.twitter.android.oauth.token", null, activity, Bundle.EMPTY, Bundle.EMPTY, null, null );

	    try{
	    	String twitter = amf2.getResult().getString(AccountManager.KEY_AUTHTOKEN);
	    } catch (OperationCanceledException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (AuthenticatorException e) {
	        e.printStackTrace();
	    }*/
	    
	    /*Account[] accts = accountManager.getAccountsByType("com.twitter.android.auth.login");
	    
        if(accts.length > 0) {
            Account acct = accts[0];

            accountManager.getAuthToken(acct, "com.twitter.android.oauth.token", null, activity, new AccountManagerCallback<Bundle>() {
            	
                @Override
                public void run(AccountManagerFuture<Bundle> arg0) {
                    try {
                        Bundle b = arg0.getResult();
                        String token = b.getString(AccountManager.KEY_AUTHTOKEN);
                        String userName = b.getString(AccountManager.KEY_ACCOUNT_NAME);
                        handler.sendMessage(handler.obtainMessage(MSG_GOT_AUTH_TOKEN, token));
                    }
                    catch (Exception e) {
                        Log.e("asdf", "EXCEPTION@AUTHTOKEN");
                        handler.sendEmptyMessage(MSG_NO_AUT_TOKEN_RECVD);
                    }
                }}, null);
        }*/
	    
	    
	    
	    Bundle bundle = null;
	    try {
	        bundle = amf.getResult();
	        //String name = (String) bundle.get(AccountManager.KEY_ACCOUNT_NAME);
	        //String type = (String) bundle.get(AccountManager.KEY_ACCOUNT_TYPE);
	        String authKey = bundle.getString(AccountManager.KEY_AUTHTOKEN); 
	        //String nomKey = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
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
	
	class MessageHandler extends Handler {
        String token = null;
        String secret = null;
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_GOT_AUTH_TOKEN || msg.what == MSG_GOT_AUTH_SECRET){
                if (msg.what == MSG_GOT_AUTH_TOKEN){        
                	authtoketwit = (String)msg.obj;
                }
                else if (msg.what == MSG_GOT_AUTH_SECRET){
                    secret = (String)msg.obj;
                }
                if (null != token && null != secret){
                    /*AccessToken accesstoken = new AccessToken(token,secret);
                    mTwitter.setOAuthAccessToken(accesstoken);
                    try {
                        mTwitter.getAccountSettings();
                        keys.User_Id = mTwitter.getScreenName();                    
                    } catch (Exception e) {
                        // That means Authentication Failed
                        // So fall back to web login 
                        Intent i = new Intent(mActivity.getApplicationContext(), PrepareRequestTokenActivity.class);
                        mActivity.startActivity(i);                 
                    }*/
                }

            }
            else if (msg.what == MSG_NO_AUT_TOKEN_RECVD){
                // That means There is no twiter account with Account Manager
                // So fall back to web login 
                /*Intent i = new Intent(mActivity.getApplicationContext(), PrepareRequestTokenActivity.class);
                mActivity.startActivity(i); */                
            }
        }
    }
}
