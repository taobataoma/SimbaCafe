package com.oldhawk.simbacafe;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Node;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyClass {
	public Toast pToast;

	/*
	 * 弹出一个自定义view的非模态消息框 hintmsg: 显示的消息内容 con: 传入的context
	 */
	public void AlertMsg(String hintmsg, Context con) {
		Intent alertIntent = new Intent();
		alertIntent.putExtra("hintmsg", hintmsg);
		System.out.println("hitmsg=" + hintmsg);
		alertIntent.setClass(con, AlertMsgActivity.class);
		con.startActivity(alertIntent);
	}

	/*
	 * 弹出一个toast hingmsg: 显示的内容 loca: 弹出的位置，Gravity.CENTER sTime:
	 * 持续显示的时间长短,Toast.LENGTH_SHORT,Toast.LENGTH_LONG con: 传入的context
	 */
	public void AlertToast(String hintmsg, int loca, int sTime, Context con) {
		if (pToast != null)
			pToast.cancel();
		pToast = Toast.makeText(con, hintmsg, sTime);
		pToast.setGravity(loca, 0, 200);
		// LinearLayout toastView = (LinearLayout) toast.getView();
		// toastView.setOrientation(LinearLayout.HORIZONTAL);
		// ImageView imageCodeProject = new ImageView(con);
		// imageCodeProject.setImageResource(R.drawable.app_logo);
		// toastView.addView(imageCodeProject, 0);
		// toastView.setGravity(Gravity.CENTER);
		pToast.show();
	}
	public void AlertToast(String hintmsg, Context con){
		AlertToast(hintmsg,Gravity.CENTER,Toast.LENGTH_LONG,con);
	}
	
	/*
	 * 关闭已弹出的toast
	 */
	public void CancelToast() {
		if (pToast != null)
			pToast.cancel();
	}

	/*
	 * 弹出一个模态对话框 titleMsg: 标题 hingmsg: 显示的消息内容 loca: 弹出的位置，Gravity.CENTER 等 con:
	 * 传入的context
	 */
	public void AlertDialog(String titleMsg, String hintmsg, int loca, Context con) {
		// 创建title显示区域
		TextView cView = new TextView(con);
		cView.setText(titleMsg);
		cView.setTextSize(16);
		cView.setTextColor(Color.rgb(0, 0, 0));
		cView.setPadding(20, 20, 20, 20);
		LinearLayout lView = new LinearLayout(con);
		lView.setOrientation(LinearLayout.HORIZONTAL);
		ImageView imageCodeProject = new ImageView(con);
		imageCodeProject.setImageResource(R.drawable.app_logo);
		imageCodeProject.setMaxHeight(20);
		imageCodeProject.setPadding(0, 0, 0, 0);
		lView.addView(imageCodeProject, 0);
		lView.addView(cView, 1);
		lView.setGravity(Gravity.LEFT);

		// 创建内容显示区域view
		TextView tView = new TextView(con);
		tView.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		tView.setText(hintmsg);
		tView.setLineSpacing(10, 1);
		tView.setTextSize(16);
		tView.setTextColor(Color.rgb(0, 0, 0));
		tView.setPadding(20, 20, 20, 20);

		// 创建对话框
		AlertDialog.Builder altDialog = new AlertDialog.Builder(con);
		altDialog.setTitle(titleMsg);
		// setIcon(R.drawable.app_logo).
		// setMessage(hintmsg).
		//altDialog.setCustomTitle(lView); //change by setTitle()
		altDialog.setView(tView);
		altDialog.setInverseBackgroundForced(true);
		altDialog.setPositiveButton(R.string.string_button_OK,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		altDialog.create();
		AlertDialog dlg=altDialog.show();
	    Button b=(Button)dlg.getButton(DialogInterface.BUTTON_POSITIVE);
	    b.setTextSize(16);
		// 设置对话框的某些属性
		WindowManager.LayoutParams params = dlg.getWindow()
				.getAttributes();
		// params.width = 200;
		// params.height = 200 ;
		params.alpha = 1;// f;
		dlg.getWindow().setAttributes(params);
		dlg.getWindow().setGravity(loca);
		// dlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}
	public void AlertDialog(String titleMsg, String hintmsg, Context con) {
		AlertDialog(titleMsg, hintmsg, Gravity.CENTER , con);
	}

	public void GetTableNo(String titleMsg, int loca, Context con, final Handler handler, final String returnString) {
		GetTableNo(titleMsg, loca, con, handler, returnString, "");
	}	
	public void GetTableNo(String titleMsg, int loca, Context con, final Handler handler, final String returnString, final String oldTableNo) {
		final AlertDialog.Builder altDB = new AlertDialog.Builder(con);
		altDB.setTitle(titleMsg);
		//altDB.setIcon(R.drawable.app_logo);
		altDB.setInverseBackgroundForced(true);
		altDB.setPositiveButton(R.string.string_button_Cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
					}
				}).create();
		
		// 创建内容显示区域view
		LayoutInflater inflater=LayoutInflater.from(con);    
	    final View tView=inflater.inflate(R.layout.view_gettableno, null);

	    //设置dialog的view
	    altDB.setView(tView);
	    //显示对话框
	    final AlertDialog dlg=altDB.show();
	    Button b=(Button)dlg.getButton(DialogInterface.BUTTON_POSITIVE);
	    b.setTextSize(16);
	    
	    //添加台类按钮
	    final TableNoXML tnx=new TableNoXML(con);
	    ArrayList<String> al=tnx.GetTableTypeArray(con);
	    RadioGroup rg=(RadioGroup)tView.findViewById(R.id.radio_housetypelist);
	    
	    RadioButton firstButton=null;
	    for(int i=0;i<al.size();i++){
			RadioButton r=new RadioButton(con);
		    r.setText(al.get(i));
		    if(i==0)
		    	firstButton=r;
		    rg.addView(r);
	    }
	    
	    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			    //添加台号按钮
				LinearLayout layout=(LinearLayout)tView.findViewById(R.id.view_gettableno_linearlayout);
				layout.removeAllViewsInLayout();
				
				RadioButton rb=(RadioButton)group.findViewById(checkedId);
				ArrayList<Node> tn=tnx.GetTableNoArray(rb.getText().toString(), tView.getContext());
				for( int i=0; i<tn.size(); i++){
					Button b=new Button(group.getContext());
				    b.setBackgroundResource(R.drawable.tablenobackground);
				    Node n=tn.get(i);
				    String cap=n.getAttributes().getNamedItem("name").getNodeValue();
				    b.setText(cap);
				    b.setTag(n);
				    LinearLayout.LayoutParams paButton = new LinearLayout.LayoutParams(130,130);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				    paButton.leftMargin=1;
				    paButton.rightMargin=1;
				    b.setLayoutParams(paButton);
				    b.setOnClickListener(new View.OnClickListener() {						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
				    		Button b=(Button)v;
				    		Message message;
				    		message = Message.obtain();
				    		
				    		Node n=(Node)b.getTag();
				    		Bundle bd=new Bundle();
				    		
				    		bd.putString("cmd", returnString);
				    		bd.putString("tableno",b.getText().toString());
				    		bd.putString("oldtableno", oldTableNo);
				    		bd.putString("mincons", n.getAttributes().getNamedItem("mincons").getNodeValue().toString());
				    		bd.putString("constype", n.getAttributes().getNamedItem("constype").getNodeValue().toString());
				    		bd.putString("maxuser", n.getAttributes().getNamedItem("maxuser").getNodeValue().toString());
				    		
				    		message.setData(bd);
				    		handler.sendMessage(message);
				    		dlg.dismiss();
						}
					});
				    layout.addView(b);
			    }
			}
	    	
	    });

		firstButton.performClick();
	}
	
   	public String getHttpServerUrl(){
   		try{
	   		IniFile f=new IniFile();
	   		f.IniReaderHasSection();
	   		String serv=f.getValue("HTTPSERV", "serv","http://192.168.0.1");
	   		String port=f.getValue("HTTPSERV", "port","80");
	   		
	   		return serv+":"+port;
   		}catch(IOException e){
   			e.printStackTrace();
   			return "http://192.168.0.1:80";
   		}
   	}
}
