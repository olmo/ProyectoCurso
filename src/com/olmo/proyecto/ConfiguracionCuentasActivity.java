package com.olmo.proyecto;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.olmo.proyectocurso.R;

public class ConfiguracionCuentasActivity extends Activity implements Callback{
	static String googleAuthKey = "";
	Cuentas cuentas = null;
	Handler hand= new Handler(this);
	
	Twitter twitter;
	RequestToken requestToken;
	public final static String consumerKey = "4g1crGp8eKwKcKrPs4JOw";
	public final static String consumerSecret = "hClbc3ELOobmBq6moRxBTddYe9ed1BHOj0Dyno6TQ";
	private final String CALLBACKURL = "proyectocurso://main";
	private String authtwitter = "";
	private String secrettwitter = "";
	private String verifier = "";
	
	private Button btnReader;
	private Button btnTwitter;
	private Button btnTerminar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.configuracion_cuentas);
        
        SharedPreferences prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
		
		googleAuthKey = prefs.getString("googleAuthKey", "");
		authtwitter = prefs.getString("authtwitter", "");
		secrettwitter = prefs.getString("secrettwitter", "");
		
		btnReader = (Button)findViewById(R.id.button1);
		btnTwitter = (Button)findViewById(R.id.button2);
		btnTerminar = (Button)findViewById(R.id.button3);
		
		if (!googleAuthKey.equals("")){
			((TextView) this.findViewById(R.id.textView2)).setText(R.string.greader_conf);
			btnReader.setEnabled(false);
		}
		else {
			btnReader.setOnClickListener(new OnClickListener() {  
				public void onClick(View v) {
					startGReader();
				}
			});
		}
		
        if(!authtwitter.equals("")){
        	((TextView) this.findViewById(R.id.textView3)).setText(R.string.twitter_conf);
        	btnTwitter.setEnabled(false);
        }
        else{
        	btnTwitter.setOnClickListener(new OnClickListener() {  
				public void onClick(View v) {
					startTwitter();
				}
			});
        	
        }
        
        btnTerminar.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {
		        startActivity(new Intent(ConfiguracionCuentasActivity.this, ProyectoCursoActivity.class));
			}
		});
        
    }
    
    public boolean handleMessage(Message msg) {
		googleAuthKey = msg.getData().getString("googleAuthKey");
		if(!googleAuthKey.equals("")){
			SharedPreferences prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
			Editor mEditor = prefs.edit();
			mEditor.putString("googleAuthKey", googleAuthKey);
			mEditor.commit();
			((TextView) this.findViewById(R.id.textView2)).setText(R.string.greader_conf);
		}
		return false;
	}
    
    void startGReader(){
    	cuentas = new Cuentas(this,hand,this);
    }
    
    void startTwitter(){
    	new Thread(new Runnable() {
            public void run() {
            	OAuthLogin();
            }
            
    	}).start();
    }
    
    void OAuthLogin() {
		try {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			requestToken = twitter.getOAuthRequestToken(CALLBACKURL);
			String authUrl = requestToken.getAuthenticationURL();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (TwitterException ex) {
			Log.e("in Main.OAuthLogin", ex.getMessage());
		}
	}
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		try {
			verifier = uri.getQueryParameter("oauth_verifier");
			new Thread(new Runnable() {
	            public void run() {
	            	getTokens();
	            }
	          }).start();
			((TextView) this.findViewById(R.id.textView3)).setText(R.string.twitter_conf);
		} catch (Exception ex) {
			Log.e("Main.onNewIntent", "" + ex.getMessage());
		}

	}
    
    private void getTokens(){
    	try{
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,verifier);
			authtwitter = accessToken.getToken();
			secrettwitter = accessToken.getTokenSecret();
			
			SharedPreferences prefs = getSharedPreferences("preferencias", MODE_PRIVATE);
			Editor mEditor = prefs.edit();
			mEditor.putString("authtwitter", authtwitter);
			mEditor.putString("secrettwitter", secrettwitter);
			mEditor.commit();
    	}
    	catch(TwitterException e){
    		Log.e("asdf", e.getMessage());
    	}
    }
}
