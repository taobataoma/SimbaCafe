package com.oldhawk.simbacafe;

import android.app.Application;


public class PublicValue extends Application {
	private static String LoginName="";
	private static String userName="";
	private static int userAccess=0;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	public void SetLoginName(String v){
		LoginName=v;
	}
	public String GetLoginName(){
		return LoginName;
	}
	
	public void SetUserName(String v){
		userName=v;
	}
	public String GetUserName(){
		return userName;
	}
	
	public void SetUserAccess(int v){
		userAccess=v;
	}
	
	public int GetUserAccess(){
		return userAccess;
	}
}