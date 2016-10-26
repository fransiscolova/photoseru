/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.photoseru.frans.universalimageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;






import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.nostra13.universalimageloader.utils.L;
import com.photoseru.frans.R;
import com.photoseru.frans.config.Config;
import com.photoseru.frans.helper.JSONParser;
import com.photoseru.frans.universalimageloader.Constants.Extra;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends BaseActivity {
	private ProgressDialog pDialog;
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	JSONParser jsonParser = new JSONParser();
	private static final String url_event = Config.photo_api;
	ArrayList<HashMap<String, String>> hashtag;
	JSONArray albums = null;
	String[] IMAGESDATA;
	int icounter = 0;
	String idevent;
	String id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		
		
		
		Bundle extras = getIntent().getExtras();
		// Extract data using passed keys
		  idevent = extras.getString("idevent");			

		

		new AddEvent().execute();

	}

	@Override
	public void onBackPressed() {
		imageLoader.stop();
		super.onBackPressed();
	}

	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open(TEST_FILE_NAME);
					FileOutputStream fos = new FileOutputStream(
							testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}

	/**
	 * Background Async Task to Load all Albums by making http request
	 * */
	class AddEvent extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HomeActivity.this);
			pDialog.setMessage("Get in galery ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Albums JSON
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			IMAGESDATA=null;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			
			
			params.add(new BasicNameValuePair("idevent", idevent));
			

			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(url_event, "GET", params);

			
			try {
				albums = new JSONArray(json);
				
				if (albums != null) {
					// looping through All albums
					
					IMAGESDATA = new String[albums.length()];
					for (int i = 0; i < albums.length(); i++) {
						icounter = 1;
						JSONObject c = albums.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();

						// Storing each json item values in variable
						String url = Config.photo_folder
								+ c.getString("photoname");

					//	Log.d("test masuk: ", "> " + url);

						IMAGESDATA[i] = url;

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
			Intent intent = new Intent(getApplicationContext(),
					ImageGridActivity.class);
			intent.putExtra(Extra.IMAGES, IMAGESDATA);
			intent.putExtra("idevent", idevent);
			startActivity(intent);
			finish();

		}

	}
}