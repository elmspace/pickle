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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends Activity {

	// Global variables
	TextView textView;
	EditText firstname, lastname, email, password, re_password;
	String Email, PassWord, Re_PassWord, FirstName, LastName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		// Grabbing the information that has been inputed on the page
		firstname = (EditText) findViewById(R.id.editText_FirstName);
		lastname = (EditText) findViewById(R.id.editText_LastName);
		email = (EditText) findViewById(R.id.editText_Email);
		password = (EditText) findViewById(R.id.editText_Password);
		re_password = (EditText) findViewById(R.id.editText_RePassword);

		// Linking the button to the one on the pge
		Button login = (Button) findViewById(R.id.button_Submit);

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
							// Do nothing for now.
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
				Intent intent = new Intent(SignUp.this, MainActivity.class);
				intent.putExtra("Msg", "Congratulations, you may sign in now.");
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
		FirstName = firstname.getText().toString();
		LastName = lastname.getText().toString();
		Email = email.getText().toString();
		PassWord = password.getText().toString();
		Re_PassWord = re_password.getText().toString();

		// The reply from server is stored in here
		String serverReply = null;
		String status = null;

		int PassWord_Match = 1;
		/*
		 * Here check if both password and re-password are the same If both
		 * passwords mathc , set PassWord_Match = 1
		 */

		// If the user entered empty user or password, yell at them
		if (!FirstName.isEmpty() && !LastName.isEmpty() && !Email.isEmpty()
				&& !PassWord.isEmpty() && !Re_PassWord.isEmpty()
				&& PassWord_Match != 0) {

			// Create data variable for sending values to server
			String data = URLEncoder.encode("FirstName", "UTF-8") + "="
					+ URLEncoder.encode(FirstName, "UTF-8");
			data += "&" + URLEncoder.encode("LastName", "UTF-8") + "="
					+ URLEncoder.encode(LastName, "UTF-8");
			data += "&" + URLEncoder.encode("Email", "UTF-8") + "="
					+ URLEncoder.encode(Email, "UTF-8");
			data += "&" + URLEncoder.encode("PassWord", "UTF-8") + "="
					+ URLEncoder.encode(PassWord, "UTF-8");

			// This is for reading the data from server
			BufferedReader reader = null;

			// Send data
			try {

				// Defined URL where to send data
				URL url = new URL(
						"http://elmspace.com/Pickle/Web/SignUp/SignUp.php");

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
				String info = "Someone with this email has already signed up!";
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
			String info = "Please fill in all the required fields, and choose an appropriate password!";
			bundle.putString("sendBack", info);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

}
