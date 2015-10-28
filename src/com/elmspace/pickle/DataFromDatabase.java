package com.elmspace.pickle;

public class DataFromDatabase {

	// The user information is initialized here to null, or 0
	private static String UserName = null;
	private static String UserFirstName = null;
	private static String UserLastName = null;
	private static String UserFriend = null;
	private static String UserEmail = null;
	private static String UserStatus = null;
	private static int UserID = 0;
	private static String FriendOn = null;
	private static String FriendFirstName = null;

	// This method will set the private variables of this class
	public void setUserData(String setuserdata_UserStatus,
			String setuserdata_UserName, String setuserdata_UserFirstName,
			String setuserdata_UserLastName, String setuserdata_UserEmail,
			String setuserdata_UserFriend, int setuserdata_UserID, String setuserdata_FriendOn, String setuserdata_FriendFirstName) {

		UserStatus = setuserdata_UserStatus;
		UserFirstName = setuserdata_UserFirstName;
		UserLastName = setuserdata_UserLastName;
		UserFriend = setuserdata_UserFriend;
		UserEmail = setuserdata_UserEmail;
		UserID = setuserdata_UserID;
		UserName = setuserdata_UserEmail;
		FriendOn = setuserdata_FriendOn;
		FriendFirstName = setuserdata_FriendFirstName;

	}

	// Here are the methods that can give back the information about the user
	public String getUserStatus() {
		return UserStatus;
	}

	public String getUserName() {
		return UserName;
	}

	public String getUserFirstName() {
		return UserFirstName;
	}

	public String getUserLastName() {
		return UserLastName;
	}
	
	public String getUserFriend() {
		return UserFriend;
	}

	public String getUserEmail() {
		return UserEmail;
	}

	public int getUserID() {
		return UserID;
	}
	
	public String getFriendOn() {
		return FriendOn;
	}
	
	public String getFriendFirstName() {
		return FriendFirstName;
	}

	// Here we will make the methods and variables that take the info from
	// server for the Msgs
	// One thing, you probably wanna make the limit 5 a variable so, you can
	// change it in the future
	private static String[] Msgs_Text = new String[5];
	private static String[] Msgs_Sender = new String[5];

	// This method will set the Msg and sender variables
	public void setUserData_Msgs(String[] setData_Msgs_Text,
			String[] setData_Msgs_Sender) {
		int i;
		for (i = 0; i < 5; i++) {
			Msgs_Text[i] = setData_Msgs_Text[i];
			Msgs_Sender[i] = setData_Msgs_Sender[i];
		}
	}

	// Here we make a getter method
	public void getUserData_Msgs(String[] getData_Msgs_Text,
			String[] getData_Msgs_Sender) {
		int i;
		for (i = 0; i < 5; i++) {
			getData_Msgs_Text[i] = Msgs_Text[i];
			getData_Msgs_Sender[i] = Msgs_Sender[i];
		}
	}

}
