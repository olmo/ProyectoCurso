package com.olmo.proyecto;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.olmo.proyecto.modelos.Feed;
import com.olmo.proyecto.modelos.FeedTagDB;
import com.olmo.proyecto.modelos.Noticia;
import com.olmo.proyecto.modelos.NoticiaDB;
import com.olmo.proyecto.modelos.Tag;
import com.olmo.proyecto.modelos.TagDB;
import com.olmo.proyectocurso.R;

public class TitulosFragment extends ListFragment implements ActionBar.TabListener {
	boolean mDualPane;
    int mCurCheckPosition = 0;
    private int tag_sel = 0;
    private ArrayList<Noticia> noticias;
    
    private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			//Object path = message.obj;
			if (message.arg1 == Activity.RESULT_OK ) {
				Toast.makeText(getActivity(),"Actualizado", Toast.LENGTH_SHORT).show();
				
				//populateList(tag_sel);
			} else {
				Toast.makeText(getActivity(), "Actualización fallida.", Toast.LENGTH_SHORT).show();
			}

		};
	};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        ActionBar bar = getActivity().getActionBar();
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Must call in order to get callback to onCreateOptionsMenu()
        setHasOptionsMenu(true);
        
        TagDB tagdb = new TagDB(this.getActivity());
        tagdb.open();
        ArrayList<Tag> tags = tagdb.getAll();
        tagdb.close();
        
        bar.addTab(bar.newTab().setText("Todos").setTabListener(this));
        for(Tag tag : tags){
        	bar.addTab(bar.newTab().setText(tag.getNombre()).setTabListener(this));
        }
        
        Intent intent = new Intent(getActivity(), ActualizarNoticiasService.class);
        Messenger messenger = new Messenger(handler);
		intent.putExtra("MESSENGER", messenger);
		getActivity().startService(intent);

        populateList(tag_sel);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }
    
    @Override
    public void onDestroyView (){
    	super.onDestroyView();
    	
    	 ActionBar bar = getActivity().getActionBar();
    	 bar.removeAllTabs();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	populateList(tag_sel);
    }
    
    public void populateList(int category) {
    	if(category==0){
	        NoticiaDB noticiaDB = new NoticiaDB(getActivity());
	        noticiaDB.open();
	        noticias = noticiaDB.getAll();
	        noticiaDB.close();
	        
	        String[] items = new String[noticias.size()];
	        
	        int i=0;
	        for (Noticia noticia : noticias){
	    		items[i] = noticia.getTitulo();
	        	i++;
	        }
	        
	        setListAdapter(new ArrayAdapter<String>(getActivity(),
	                android.R.layout.simple_list_item_activated_1, items));
    	}
    	else{
    		FeedTagDB feeddb = new FeedTagDB(this.getActivity());
    		feeddb.open();
    		ArrayList<Feed> feeds = feeddb.getFeeds(category);
    		feeddb.close();
    		
    		NoticiaDB noticiaDB = new NoticiaDB(getActivity());
	        noticiaDB.open();
	        noticias = noticiaDB.getAllfromFeeds(feeds);
	        noticiaDB.close();
	        
	        String[] items = new String[noticias.size()];
	        
	        int i=0;
	        for (Noticia noticia : noticias){
	    		items[i] = noticia.getTitulo();
	        	i++;
	        }
	        
	        setListAdapter(new ArrayAdapter<String>(getActivity(),
	                android.R.layout.simple_list_item_activated_1, items));
    	}
        
        tag_sel = category;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;
        
        if(noticias.size()>0){
	        if (mDualPane) {
	            // We can display everything in-place with fragments, so update
	            // the list to highlight the selected item and show the data.
	            getListView().setItemChecked(index, true);
	
	            // Check what fragment is currently shown, replace if needed.
	            ContenidoFragment details = (ContenidoFragment) getFragmentManager().findFragmentById(R.id.details);
	            
	            if (details == null || details.getShownIndex() != noticias.get(index).getId()) {
	                // Make new fragment to show this selection.
	                details = ContenidoFragment.newInstance(noticias.get(index).getId());
	
	                // Execute a transaction, replacing any existing fragment
	                // with this one inside the frame.
	                FragmentTransaction ft = getFragmentManager().beginTransaction();
	                ft.replace(R.id.details, details);
	                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	                ft.commit();
	            }
	
	        } else {
	            // Otherwise we need to launch a new activity to display
	            // the dialog fragment with selected text.
	            Intent intent = new Intent();
	            intent.setClass(getActivity(), ContenidoActivity.class);
	            intent.putExtra("index", noticias.get(index).getId());
	            startActivity(intent);
	        }
        }
    }
    
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    	if(getFragmentManager().findFragmentById(R.id.titles) instanceof TitulosFragment){
	        TitulosFragment titleFrag = (TitulosFragment) getFragmentManager().findFragmentById(R.id.titles);
	        titleFrag.populateList(tab.getPosition());
	        
	        if (mDualPane) {
	            titleFrag.showDetails(0);
	        }
    	}
    }

    /* These must be implemented, but we don't use them */
    
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
