package com.olmo.proyecto;

import com.olmo.proyecto.modelos.Noticia;
import com.olmo.proyecto.modelos.NoticiaDB;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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
        Noticia noticia = noticiaDB.getNoticia(getShownIndex());
        noticiaDB.close();

        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4, getActivity().getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        text.setText(Html.fromHtml(noticia.getContenido()));
        return scroller;
    }
}
