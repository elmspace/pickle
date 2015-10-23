package com.elmspace.pickle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class FriendListActivity extends AppCompatActivity {

	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);

		DataFromDatabase newdata = new DataFromDatabase();
		String name = newdata.getUserFirstName();

		textView = (TextView) findViewById(R.id.textView1);
		textView.setText("Hello " + name);

	}

	public void sendMsgToMe(View view) {
		Intent intent = new Intent(FriendListActivity.this, MsgActivity.class);

		DataFromDatabase newdata = new DataFromDatabase();
		String friend = newdata.getUserFriend();

		intent.putExtra("Friend_User_Name", friend);
		startActivity(intent);
	}

}
