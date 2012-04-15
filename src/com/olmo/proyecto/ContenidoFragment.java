package com.olmo.proyecto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.olmo.proyecto.modelos.Noticia;
import com.olmo.proyecto.modelos.NoticiaDB;

public class ContenidoFragment extends Fragment {
	/**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static ContenidoFragment newInstance(int index) {
        ContenidoFragment f = new ContenidoFragment();
        
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) {
            
            return null;
        }
        
        NoticiaDB noticiaDB = new NoticiaDB(getActivity());
        noticiaDB.open();
        final Noticia noticia = noticiaDB.getNoticia(getShownIndex());
        noticiaDB.close();
        final GReader greader = new GReader(getActivity());
        
        new Thread(new Runnable() {
            public void run() {
            	greader.marcarLeido(noticia.getId());
            }
          }).start();
        

        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getActivity().getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        //text.setText(Html.fromHtml(noticia.getContenido()));
        /*text.setText(Html.fromHtml(noticia.getContenido(), new ImageGetter() {
            @Override
	         public Drawable getDrawable(String source) {                  
	            Drawable d = null;
	            try {
					InputStream src = imageFetch(source);
					d = Drawable.createFromStream(src, "src");
					if(d != null){
					d.setBounds(0,0,d.getIntrinsicWidth(),
					d.getIntrinsicHeight());
	                        }
				} catch (MalformedURLException e) {
	                  e.printStackTrace(); 
	            } catch (IOException e) {
	                  e.printStackTrace();  
	            }
	
				return d;
	        }

        },null));*/
        
        URLImageParser p = new URLImageParser(text, this.getActivity());
        text.setText(Html.fromHtml(noticia.getContenido(), p, null));
        
        return scroller;
    }
    
    public InputStream imageFetch(String source)
            throws MalformedURLException,IOException {
		URL url = new URL(source);
		Object o = url.getContent();
		InputStream content = (InputStream)o;
		// add delay here (see comment at the end)     
		return content;
	}
    
    
    
    public class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
    
    
    public class URLImageParser implements ImageGetter {
        Context c;
        View container;

        /***
         * Construct the URLImageParser which will execute AsyncTask and refresh the container
         * @param t
         * @param c
         */
        public URLImageParser(View t, Context c) {
            this.c = c;
            this.container = t;
        }

        public Drawable getDrawable(String source) {
            URLDrawable urlDrawable = new URLDrawable();

            // get the actual source
            ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);

            asyncTask.execute(source);

            // return reference to URLDrawable where I will change with actual image from
            // the src tag
            return urlDrawable;
        }

        public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
            URLDrawable urlDrawable;

            public ImageGetterAsyncTask(URLDrawable d) {
                this.urlDrawable = d;
            }

            @Override
            protected Drawable doInBackground(String... params) {
                String source = params[0];
                return fetchDrawable(source);
            }

            @Override
            protected void onPostExecute(Drawable result) {
                // set the correct bound according to the result from HTTP call
                urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight()); 

                // change the reference of the current drawable to the result
                // from the HTTP call
                urlDrawable.drawable = result;

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();
            }

            /***
             * Get the Drawable from URL
             * @param urlString
             * @return
             */
            public Drawable fetchDrawable(String urlString) {
                try {
                    InputStream is = fetch(urlString);
                    Drawable drawable = Drawable.createFromStream(is, "src");
                    drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight()); 
                    return drawable;
                } catch (Exception e) {
                    return null;
                } 
            }

            private InputStream fetch(String urlString) throws MalformedURLException, IOException {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlString);
                HttpResponse response = httpClient.execute(request);
                return response.getEntity().getContent();
            }
        }
    }
    
    public class CustomImageGetter implements ImageGetter{
    	// NOTE: The directories must already exist
    	final static String CACHE_DIR = "/sdcard/DIC/image_cache/";
    	@Override
    	public Drawable getDrawable(String url) {
    		try{
    			// get name of image
    			String name = url.substring(url.lastIndexOf("/")+1);
    			// cache dir + name of image + the image save format
    			File f = new File(CACHE_DIR+name+".png");
    			if(!f.exists())// if it does not exist in the cache folder we need to download it
    				downloadImage(url,f);
    			Drawable d = Drawable.createFromPath(f.getAbsolutePath());
    			d.setBounds(0, 0, 32, 32);// make it the size of the image
    			return d;
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		return null;
    	}
    	private void downloadImage(String url,File f) throws IOException
        {
        	URL myFileUrl =new URL(url);   
        	HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            
            Bitmap bm = BitmapFactory.decodeStream(is);
            FileOutputStream out = new FileOutputStream(f);   
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);    
        }
    }
}
