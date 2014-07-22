package com.oldhawk.simbacafe;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SelectDateActivity extends Activity {
	private final MyClass myCls = new MyClass();
	private TextView etf=null;
	private TextView ett=null;
	private RadioGroup rg=null;
	private TextView clickedView=null;
	public static SelectDateActivity staticSelectDateActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectdate);
		
		staticSelectDateActivity=this;
		
		etf=(TextView)findViewById(R.id.selectdate_from);
		ett=(TextView)findViewById(R.id.selectdate_to);
		etf.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
		ett.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
		
		rg=(RadioGroup)findViewById(R.id.selectdate_group);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				LinearLayout ll=(LinearLayout)findViewById(R.id.selectdate_layout);
				final Calendar c = Calendar.getInstance(); 
				StringBuilder sb = new StringBuilder();
				sb.append(c.get(Calendar.YEAR)); 
				sb.append("-");
				sb.append(c.get(Calendar.MONTH)+1); 
				sb.append("-");
				sb.append(c.get(Calendar.DAY_OF_MONTH));
				if(checkedId==R.id.radioButtonToday){
					ll.setVisibility(View.GONE);
					etf.setText(sb);
					ett.setText(sb);
				}else if(checkedId==R.id.radioButtonSelect){
					ll.setVisibility(View.VISIBLE);
					etf.setText("");
					ett.setText(sb);
				}
			}			
		});
		
		RadioButton rb=(RadioButton)findViewById(R.id.radioButtonToday);
		rb.performClick();
		
		etf.setOnClickListener(new mOnClickListener());
		ett.setOnClickListener(new mOnClickListener());
	}
	
	class mOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			setClickedView(v);
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(SelectDateActivity.this.getFragmentManager(), "datePicker");
		}
	}
	private void setClickedView(View v){
		clickedView=(TextView)v;
	}
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override 
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker 
			final Calendar c = Calendar.getInstance(); 
			int year = c.get(Calendar.YEAR); 
			int month = c.get(Calendar.MONTH); 
			int day = c.get(Calendar.DAY_OF_MONTH); 
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day); 
		}
		public void onDateSet(DatePicker view, int year, int month, int day) {
			String s=year+"-"+(month+1)+"-"+day;
			SelectDateActivity.staticSelectDateActivity.setClickedViewText(s);
			
		}
	} 
	
	public void setClickedViewText(String s){
		if(clickedView!=null){
			clickedView.setText(s);
		}
	}
	
	public void ButtonClick(View v){
		Button b=(Button)v;
		Intent intent=getIntent();
		if(b.getId()==R.id.button_cancel){
			setResult(0, intent);
			finish();
		}else if(b.getId()==R.id.button_ok){
			if((etf.getText().toString().length()==0) || (ett.getText().toString().length()==0)){
	           	myCls.AlertToast(getString(R.string.selectdate_selecttips), Gravity.TOP, Toast.LENGTH_LONG, this);
	           	return;
			}else{
					intent.putExtra("datefrom", etf.getText().toString());
					intent.putExtra("dateto", ett.getText().toString());
					setResult(intent.getIntExtra("tag", 0), intent);
					finish();
			}
		}
	}
}
