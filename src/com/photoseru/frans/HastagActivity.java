package com.photoseru.frans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.photoseru.frans.GetEventsActivity.AddEvent;
import com.photoseru.frans.config.Config;
import com.photoseru.frans.helper.AlertDialogManager;
import com.photoseru.frans.helper.ConnectionDetector;
import com.photoseru.frans.helper.JSONParser;
import com.photoseru.frans.helper.SessionManager;
import com.photoseru.frans.universalimageloader.HomeActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HastagActivity extends ListActivity {
	// Connection detector
	ConnectionDetector cd;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> hashtag;

	// albums JSONArray
	JSONArray albums = null;

	// albums JSON url
	private static final String url_hashtag = Config.hashtag_api;

	SessionManager session;
	String idevent = "";
	// ALL JSON node names
	private static final String TAG_ID_HASHTAG = "idHashtag";
	private static final String TAG_NAME_HASHTAG = "hashtag";
	private static final String TAG_DESCRIP = "Descrip";
	private Button button;
	private Button buttonphto;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		session = new SessionManager(getApplicationContext());
		HashMap<String, String> user = session.getUserDetails();
		String name = user.get(SessionManager.NAME);
		String email = user.get(SessionManager.EMAIL);
		String id = user.get(SessionManager.ID);

		Bundle extras = getIntent().getExtras();
		// Extract data using passed keys
		idevent = extras.getString("idevent");

		// get the action bar
		ActionBar actionBar = getActionBar();
		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("PhotoSeru :List Hashtag");

		cd = new ConnectionDetector(getApplicationContext());

		// Check for internet connection
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(HastagActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Hashmap for ListView
		hashtag = new ArrayList<HashMap<String, String>>();

		// Loading Albums JSON in Background Thread
		new LoadAlbums().execute();

		// get listview
		ListView lv = getListView();

		/**
		 * Listview item click listener TrackListActivity will be lauched by
		 * passing album id
		 * */
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// on selecting a single album
				// TrackListActivity will be launched to show tracks inside the
				// album
				Intent i = new Intent(getApplicationContext(),
						HomeActivity.class);

				// send album id to tracklist activity to get list of songs
				// under that album
				String idhastag = ((TextView) view.findViewById(R.id.album_id))
						.getText().toString();
				i.putExtra("idhashtag", idhastag);
				i.putExtra("idevent", idevent);

				startActivity(i);
			}
		});

		button = (Button) findViewById(R.id.btnAddEvent);
		button.setText("Out Event");

		// add button listener
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						GetEventsActivity.class);
				// send album id to tracklist activity to get list of songs
				// under that album

				i.putExtra("idevent", idevent);

				startActivity(i);
				finish();

			}

		});

		

	}

	/**
	 * Background Async Task to Load all Albums by making http request
	 * */
	class LoadAlbums extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HastagActivity.this);
			pDialog.setMessage("Listing Hashtag ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Albums JSON
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("idEvent", idevent));
			// getting JSON string from URL
			String json = jsonParser
					.makeHttpRequest(url_hashtag, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("Albums JSON: ", "> " + json);

			try {
				albums = new JSONArray(json);

				if (albums != null) {
					// looping through All albums

					for (int i = 0; i < albums.length(); i++) {
						JSONObject c = albums.getJSONObject(i);

						// Storing each json item values in variable
						String id = c.getString(TAG_ID_HASHTAG);
						String name = c.getString(TAG_NAME_HASHTAG);
						String desc = c.getString(TAG_DESCRIP);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_ID_HASHTAG, id);
						map.put(TAG_NAME_HASHTAG, name);
						map.put(TAG_DESCRIP, desc);

						// adding HashList to ArrayList
						hashtag.add(map);
					}
				} else {
					Log.d("Albums: ", "null");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all albums
			pDialog.dismiss();

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							HastagActivity.this, hashtag,
							R.layout.list_item_albums, new String[] {
									TAG_ID_HASHTAG, TAG_NAME_HASHTAG,
									TAG_DESCRIP }, new int[] { R.id.album_id,
									R.id.album_name, R.id.songs_count });

					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}