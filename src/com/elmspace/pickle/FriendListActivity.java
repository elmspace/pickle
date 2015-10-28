package com.elmspace.pickle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class FriendListActivity extends AppCompatActivity {

	TextView textView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);

		DataFromDatabase newdata = new DataFromDatabase();
		String name = newdata.getUserFirstName();

		textView = (TextView) findViewById(R.id.textView1);
		textView.setText("Hello " + name);

		String friendon = newdata.getFriendOn();
		String friendfirstname = newdata.getFriendFirstName();

		if (friendon.equals("True")) {
			Intent intent = new Intent(FriendListActivity.this,
					MsgActivity.class);
			intent.putExtra("Friend_User_Name", friendfirstname);
			startActivity(intent);
		} else {
			textView = (TextView) findViewById(R.id.textView2);
			textView.setText("Your friend has not signed up yet!");
		}

	}

}
