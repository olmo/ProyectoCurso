package com.olmo.proyecto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.olmo.proyectocurso.R;

public class TimelineFragment extends ListFragment implements ActionBar.TabListener {
	boolean mDualPane;
    int mCurCheckPosition = 0;
    private int tag_sel = 0;
    Twitter twitter;
    public final static String consumerKey = "4g1crGp8eKwKcKrPs4JOw";
	public final static String consumerSecret = "hClbc3ELOobmBq6moRxBTddYe9ed1BHOj0Dyno6TQ";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        twitter = new TwitterFactory().getInstance();
		AccessToken accessToken = new AccessToken(ProyectoCursoActivity.authtwitter, ProyectoCursoActivity.secrettwitter);
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(accessToken);
        
        ActionBar bar = getActivity().getActionBar();
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Must call in order to get callback to onCreateOptionsMenu()
        setHasOptionsMenu(true);
        
        bar.addTab(bar.newTab().setText("Todos").setTabListener(this));

        //populateList(tag_sel);
        displayTimeLine();

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
    public void onResume(){
    	super.onResume();
    	displayTimeLine();
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
        
    }
    
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        
    }
    
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
    
    void displayTimeLine() {
		new UpdateProgress().execute();
	}
    
    public class UpdateProgress extends AsyncTask<Void, Integer, Void>{
		int progress;
		List<twitter4j.Status> statuses = null;
	    
		@Override
		protected void onPostExecute(Void result){
			String[] items = new String[statuses.size()];
	        
	        int i=0;
	        for (twitter4j.Status status : statuses){
	    		items[i] = status.getText();
	        	i++;
	        }
			
			setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, items));
		}
		
		@Override
		protected void onPreExecute(){
			progress = 0;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values){
		}
		
		@Override
		protected Void doInBackground(Void... arg0){
			try{
				statuses = twitter.getHomeTimeline();
			}
			catch(TwitterException e){
				
			}
			
			return null;
		}
    }
   
}
