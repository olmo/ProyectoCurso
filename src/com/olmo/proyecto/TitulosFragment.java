package com.olmo.proyecto;

import java.util.ArrayList;

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

import com.olmo.proyecto.modelos.Noticia;
import com.olmo.proyecto.modelos.NoticiaDB;
import com.olmo.proyectocurso.R;

public class TitulosFragment extends ListFragment {
	boolean mDualPane;
    int mCurCheckPosition = 0;
    
    private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			//Object path = message.obj;
			if (message.arg1 == Activity.RESULT_OK ) {
				Toast.makeText(getActivity(),"Actualizado", Toast.LENGTH_SHORT).show();
				
				NoticiaDB noticiaDB = new NoticiaDB(getActivity());
		        noticiaDB.open();
		        ArrayList<Noticia> noticias = noticiaDB.getAll();
		        noticiaDB.close();
		        
		        String[] items = new String[noticias.size()];
		        
		        int i=0;
		        for (Noticia noticia : noticias){
		    		items[i] = noticia.getTitulo();
		        	i++;
		        }
		        
		        setListAdapter(new ArrayAdapter<String>(getActivity(),
		                android.R.layout.simple_list_item_activated_1, items));
			} else {
				Toast.makeText(getActivity(), "Actualización fallida.", Toast.LENGTH_SHORT).show();
			}

		};
	};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        Intent intent = new Intent(getActivity(), ActualizarNoticiasService.class);
        Messenger messenger = new Messenger(handler);
		intent.putExtra("MESSENGER", messenger);
		getActivity().startService(intent);

        // Populate list with our static array of titles.
        NoticiaDB noticiaDB = new NoticiaDB(getActivity());
        noticiaDB.open();
        ArrayList<Noticia> noticias = noticiaDB.getAll();
        noticiaDB.close();
        
        String[] items = new String[noticias.size()];
        
        int i=0;
        for (Noticia noticia : noticias){
    		items[i] = noticia.getTitulo();
        	i++;
        }
        
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, items));
        
        /*setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES));*/

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
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
        
        NoticiaDB noticiaDB = new NoticiaDB(getActivity());
        noticiaDB.open();
        ArrayList<Noticia> noticias = noticiaDB.getAll();
        noticiaDB.close();
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
}
