package com.elmspace.pickle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	// Global variables
	TextView textView;
	EditText email, password;
	String Email, PassWord;

	// This object contains information which we get from the server
	DataFromDatabase userData = new DataFromDatabase();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// When SignUp activity calls back the MainActivity after signing up, it
		// will be sending a msg, which we are grabbing here
		try{
		Bundle Msg_from_SignUp_Activity = getIntent().getExtras();
		if (!Msg_from_SignUp_Activity.isEmpty()) {
			textView = (TextView) findViewById(R.id.textView);
			String msg = Msg_from_SignUp_Activity.getString("Msg");
			textView.setText(msg);
		}
		}catch (Exception ex){
			// Do nothing.
		}

		email = (EditText) findViewById(R.id.editText_email);
		password = (EditText) findViewById(R.id.editText_password);

		Button login = (Button) findViewById(R.id.button_login);

		// When the login button is clicked this will be trigered
		login.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// We are making a runnable to run on a different thread
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							// This is the method which will be run on the
							// thread
							GetText();
						} catch (Exception ex) {
							textView.setText(" url exeption! ");
						}
					}
				};
				// Make and run the thread
				Thread mythread = new Thread(runnable);
				mythread.start();
			}
		});

	}

	// *******************************************************************************************
	// *******************************************************************************************
	// This will redirect you to the signup activity
	public void signUp(View view) {
		Intent intent = new Intent(MainActivity.this, SignUp.class);
		startActivity(intent);
	}

	// *******************************************************************************************
	// *******************************************************************************************
	// This handler deals with the comminucation between the thread and our main
	// thread
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		// @Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String string = bundle.getString("sendBack");

			textView = (TextView) findViewById(R.id.textView);
			// If the password and username were correct, then we are directing
			// the user to the next activity
			if (string.equals("True")) {
				Intent intent = new Intent(MainActivity.this,
						FriendListActivity.class);
				startActivity(intent);
			} else {
				textView.setText(string);
			}
		}
	};

	// *******************************************************************************************
	// *******************************************************************************************
	// This method deals with sending and recieving data from server
	@SuppressLint("NewApi")
	public void GetText() throws UnsupportedEncodingException {
		// Get user defined values
		Email = email.getText().toString();
		PassWord = password.getText().toString();

		// These will contain the info we get from server
		String serverReply = null;
		String status = null;
		String username = null;
		String firstname = null;
		String lastname = null;
		String email = null;
		String friend = null;
		String friendon = null;
		String friendfirstname = null;
		int id = 0;

		// If the user entered empty user or password, yell at them
		if (!Email.isEmpty() && !PassWord.isEmpty()) {
			// Create data variable for sent values to server
			String data = URLEncoder.encode("Email", "UTF-8") + "="
					+ URLEncoder.encode(Email, "UTF-8");

			data += "&" + URLEncoder.encode("PassWord", "UTF-8") + "="
					+ URLEncoder.encode(PassWord, "UTF-8");

			// This is for reading the data from server
			BufferedReader reader = null;

			// Send data
			try {

				// Defined URL where to send data
				URL url = new URL(
						"http://elmspace.com/Pickle/Web/Login/Login.php");

				// Send POST data request
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the server response
				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + "\n");
				}
				// This will contain any information that comes back from the
				// server. (simple text or Json)
				serverReply = sb.toString();

				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// Here we will deal with the data we are getting from server

				// The "serverReply" is a JSON, so we are parsing it in the
				// following section
				JSONObject jObject = new JSONObject(serverReply.toString())
						.getJSONObject("posts");
				status = jObject.getString("status");
				username = jObject.getString("UserName");
				firstname = jObject.getString("FirstName");
				lastname = jObject.getString("LastName");
				email = jObject.getString("Email");
				friend = jObject.getString("Friend");
				id = jObject.getInt("ID");
				friendon = jObject.getString("FriendOn");
				friendfirstname = jObject.getString("FriendFirstName");

				if (status.equals("True")) {
					userData.setUserData(status, username, firstname, lastname,
							email, friend, id, friendon, friendfirstname);
				} else {
					userData.setUserData(status, null, null, null, null, null,
							0,null,null);
				}
				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

				// Here on, depending on the response from the server, we will
				// do different call backs to our handler
			} catch (Exception ex) {
				Message msg = handler.obtainMessage();
				Bundle bundle = new Bundle();
				String info = "Cant connect to the server!!!";
				bundle.putString("sendBack", info);
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				try {
					reader.close();
				} catch (Exception ex) {
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					String info = "Cant connect to the server!!!";
					bundle.putString("sendBack", info);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			}
			if (status.equals("True")) {
				Message msg = handler.obtainMessage();
				Bundle bundle = new Bundle();
				String info = "True";
				bundle.putString("sendBack", info);
				msg.setData(bundle);
				handler.sendMessage(msg);
			} else if (status.equals("False")) {
				Message msg = handler.obtainMessage();
				Bundle bundle = new Bundle();
				String info = "Wrong username or password.";
				bundle.putString("sendBack", info);
				msg.setData(bundle);
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				Bundle bundle = new Bundle();
				String info = "Something has gone wrong! That's embarrassing :$";
				bundle.putString("sendBack", info);
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		} else {
			Message msg = handler.obtainMessage();
			Bundle bundle = new Bundle();
			String info = "Please fill in all the required fields";
			bundle.putString("sendBack", info);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

}
