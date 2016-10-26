package com.photoseru.frans;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.photoseru.frans.RegisterUser.Register;
import com.photoseru.frans.config.Config;
import com.photoseru.frans.helper.JSONParserObj;
import com.photoseru.frans.helper.SessionManager;




import com.photoseru.frans.universalimageloader.HomeActivity;
import com.photoseru.frans.universalimageloader.UILApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends Activity implements AnimationListener {

	EditText txtName;
	EditText txtPrice;
	EditText txtDesc;
	EditText txtCreatedAt;
	Button btnSave;
	Button btnDelete;

	

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParserObj jsonParser = new JSONParserObj();

	// single product url
	private static final String url_login = Config.login_api;

	SessionManager session;

	Animation animBounce;
	ImageView imgPoster;

	EditText name;
	EditText email;
	TextView register;

	String type;
	String username = "";
	String useremail = "";
	String userid = "";

	int success;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().hide();
		session = new SessionManager(getApplicationContext());
		
		if(session.isLoggedIn()){
			Intent i = new Intent(getApplicationContext(),
					GetEventsActivity.class);
			startActivity(i);
			
		}
		
		// load the animation
		imgPoster = (ImageView) findViewById(R.id.logo_antique);
		animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.sequential);
		animBounce.setAnimationListener(this);
		imgPoster.startAnimation(animBounce);

		Button btlog = (Button) findViewById(R.id.btnLogin);
		name = (EditText) findViewById(R.id.name);
		email = (EditText) findViewById(R.id.email);

		register = (TextView) findViewById(R.id.link_to_register);
		// Listening to register new account link
		btlog.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				new LoginUser().execute();
			}
		});

		register.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent i = new Intent(getApplicationContext(),
						RegisterUser.class);
				startActivity(i);
			}
		});
		
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				 .memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.writeDebugLogs() // Remove for release app
				.build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	/**
	 * Background Async Task to Get complete product details
	 * */
	class LoginUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Login. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... args) {

			// updating UI from Background Thread
			// runOnUiThread(new Runnable() {
			// public void run() {
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", name.getText()
						.toString()));
				params.add(new BasicNameValuePair("email", email.getText()
						.toString()));

				
				// getting product details by making HTTP request
				// Note that product details url will use GET request
				JSONObject json = jsonParser.makeHttpRequest(url_login, "GET",
						params);

				// check your log for json response
				Log.d("Single Product Details", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// successfully received product details
					JSONArray userObj = json.getJSONArray(TAG_USER); // JSON
																		// Array

					// get first product object from JSON Array
					JSONObject user = userObj.getJSONObject(0);
					session.createLoginSession(user.getString("idUser"),
					user.getString("name"), user.getString("email"));

						
					Intent i = new Intent(getApplicationContext(),
							GetEventsActivity.class);
					startActivity(i);

					// closing this screen
					finish();
				} else {
					// product with pid not found
					success = 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// }
			// });

			return null;
		}

		/**
		 * d After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
			if (success == 0) {
				showAlert("Email or Name not found ");
			}

		}
	}

	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("Response from Servers")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do nothing
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}
}
