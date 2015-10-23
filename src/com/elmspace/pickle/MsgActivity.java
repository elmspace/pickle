package com.elmspace.pickle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MsgActivity extends AppCompatActivity {

	String MyUserName_global, FriendUserName_global, Msg;
	TextView textView;
	EditText editText_msg;
	Handler mHandler = new Handler();

	DataFromDatabase msgs_object = new DataFromDatabase();

	// ******************************************************************************************
	//
	// This the handler which will communicate with the GetMsgs() method which
	// is on a different thread
	// It is responsible for updating the activity which is one the main thread
	//
	// ******************************************************************************************

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		// @Override

		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();

			String[] msgs_text = new String[5];
			String[] msgs_sender = new String[5];

			msgs_object.getUserData_Msgs(msgs_text, msgs_sender);

			textView = (TextView) findViewById(R.id.TextMsgView);
			textView.setText("");
			textView.setText(" " + "\n");
			for (int i = 4; i > -1; i--) {
				textView.append(msgs_sender[i] + " : " + msgs_text[i] + "\n");
				textView.append(" " + "\n");
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);

		// create a DataFromDatabase object so we can get user info
		DataFromDatabase myUserInfo = new DataFromDatabase();
		MyUserName_global = myUserInfo.getUserName();
		FriendUserName_global = myUserInfo.getUserFriend();

		// This is where we get the information sent by the activity that
		// initiated this one
		/*
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			FriendUserName_global = extras.getString("Friend_User_Name");
		}
		*/

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// Here we will send the msg user typed to the server
		Button sendMsg = (Button) findViewById(R.id.buttonSendMsg);
		editText_msg = (EditText) findViewById(R.id.editTextMsg);

		sendMsg.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Runnable runnable2 = new Runnable() {
					@Override
					public void run() {
						try {
							SendMsgs();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				Thread mythread2 = new Thread(runnable2);
				mythread2.start();
			}
		});
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		int delay = 1000; // delay for 1 sec.
		int period = 10000; // repeat every 10 sec.
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					GetMsgs();
				} catch (Exception ex) {
					// You should catch exceptions here
				}
			}
		}, delay, period);

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// This will run any activity on Activity start
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					GetMsgs();
				} catch (Exception ex) {
					// You should catch exceptions here
				}
			}
		};
		Thread mythread = new Thread(runnable);
		mythread.start();
	}

	// ******************************************************************************************
	@SuppressLint("HandlerLeak")
	Handler handler2 = new Handler() {
		// @Override

		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String string = bundle.getString("sendBack");

			editText_msg.setText("");
			if (string.equals("True")) {
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							GetMsgs();
						} catch (Exception ex) {
							// You should catch exceptions here
						}
					}
				};
				Thread mythread = new Thread(runnable);
				mythread.start();
			} else {

			}
		}

	};

	// ******************************************************************************************

	// ******************************************************************************************
	// The following method calls the server, getting all the msgs needed to
	// populate the activity
	//
	//
	// ******************************************************************************************
	@SuppressLint("NewApi")
	public void GetMsgs() throws UnsupportedEncodingException {

		DataFromDatabase myUserInfo = new DataFromDatabase();
		String User = myUserInfo.getUserName();
		String ReplyTo = myUserInfo.getUserFriend();
		
		// These will contain the info we get from server
		String serverReply = null;
		String[] msgs_text = new String[5];
		String[] msgs_sender = new String[5];

		// Create data variable for sent values to server

		String data = URLEncoder.encode("MyUserName", "UTF-8") + "="
				+ URLEncoder.encode(User, "UTF-8");

		data += "&" + URLEncoder.encode("FriendUserName", "UTF-8") + "="
				+ URLEncoder.encode(ReplyTo, "UTF-8");

		BufferedReader reader = null;

		// Send data
		try {

			// Defined URL where to send data
			URL url = new URL(
					"http://elmspace.com/Pickle/Web/ReadMsgs/ReadMsgs.php");

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
			// server. (simple text or JSON)
			serverReply = sb.toString();

			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// Here we will deal with the data we are getting from server

			// The "serverReply" is a JSON, so we are parsing it in the
			// following section
			JSONObject jObject = new JSONObject(serverReply.toString())
					.getJSONObject("posts");

			msgs_text[0] = jObject.getString("M0");
			msgs_text[1] = jObject.getString("M1");
			msgs_text[2] = jObject.getString("M2");
			msgs_text[3] = jObject.getString("M3");
			msgs_text[4] = jObject.getString("M4");

			msgs_sender[0] = jObject.getString("S0");
			msgs_sender[1] = jObject.getString("S1");
			msgs_sender[2] = jObject.getString("S2");
			msgs_sender[3] = jObject.getString("S3");
			msgs_sender[4] = jObject.getString("S4");

			msgs_object.setUserData_Msgs(msgs_text, msgs_sender);

			Message msg = handler.obtainMessage();
			Bundle bundle = new Bundle();
			String info = "True";
			bundle.putString("sendBack", info);
			msg.setData(bundle);
			handler.sendMessage(msg);

			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		} catch (Exception ex) {
			// You should catch the exception for now empty
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
				// You should catch the exception for now empty
			}
		}

	}

	// ******************************************************************************************

	// ******************************************************************************************
	// The following method calls the server, getting all the msgs needed to
	// populate the activity
	//
	//
	// ******************************************************************************************
	@SuppressLint("NewApi")
	public void SendMsgs() throws UnsupportedEncodingException {

		// Getting the string user has typed to send to server
		Msg = editText_msg.getText().toString();
		// Getting the username of the user who is sending the msg
		DataFromDatabase myUserInfo = new DataFromDatabase();
		String User = myUserInfo.getUserName();
		String ReplyTo = myUserInfo.getUserFriend();

		// These will contain the info we get from server
		String serverReply = null;

		// Create data variable for sent values to server

		String data = URLEncoder.encode("MyUserName", "UTF-8") + "="
				+ URLEncoder.encode(User, "UTF-8");

		data += "&" + URLEncoder.encode("Msg", "UTF-8") + "="
				+ URLEncoder.encode(Msg, "UTF-8");

		data += "&" + URLEncoder.encode("ReplyTo", "UTF-8") + "="
				+ URLEncoder.encode(ReplyTo, "UTF-8");

		BufferedReader reader = null;

		// Send data
		try {

			// Defined URL where to send data
			URL url = new URL(
					"http://elmspace.com/Pickle/Web/WriteMsgs/WriteMsgs.php");

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

			serverReply = sb.toString();

			Message msg = handler2.obtainMessage();
			Bundle bundle = new Bundle();
			String info = "True";
			bundle.putString("sendBack", info);
			msg.setData(bundle);
			handler2.sendMessage(msg);

		} catch (Exception ex) {

		} finally {
			try {
				reader.close();
			} catch (Exception ex) {

			}
		}

	}
	// ******************************************************************************************

}
