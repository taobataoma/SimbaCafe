package com.oldhawk.simbacafe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MianDanActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miandan);
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		initDateString();
	}

	private void initDateString(){
		TextView dateTextView=(TextView)findViewById(R.id.miandan_status);
		Intent intent=getIntent();
		
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
			Date fromDate = sdf.parse(intent.getStringExtra("datefrom"));
			Date toDate = sdf.parse(intent.getStringExtra("dateto"));
			if(fromDate.getTime()>toDate.getTime()){
				Date tempDate=fromDate;
				fromDate=toDate;
				toDate=tempDate;
			}
			String s=dateTextView.getText().toString();
			s+=sdf.format(fromDate);
			
			if(fromDate.getTime()!=toDate.getTime())
				s+=getString(R.string.string_trans_to)+sdf.format(toDate);
			
			dateTextView.setText(s);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    @Override
	public boolean onNavigateUp() {
    	finish();
		return super.onNavigateUp();
	}
}
