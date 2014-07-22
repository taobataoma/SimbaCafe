package com.oldhawk.simbacafe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


	
@SuppressLint("HandlerLeak")
public class SearchOneMemberActivity extends Activity {
	private MyClass myCls=new MyClass();
	private String memberno="";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchonemember);    //Activity的布局 
		
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		Intent intent=getIntent();
		memberno=intent.getStringExtra("memberno");
		SearchView sv=(SearchView)findViewById(R.id.searchView);
		sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				//myCls.AlertToast(query, SearchOneMemberActivity.this);
				if(query.length()>0){
		    		
		    		TextView no=(TextView)findViewById(R.id.member_no);
		    		no.setText(R.string.sonemember_no);
		    		
		    		TextView name=(TextView)findViewById(R.id.member_name);
		    		name.setText(R.string.sonemember_name);
		    		
		    		TextView sex=(TextView)findViewById(R.id.member_sex);
		    		sex.setText(R.string.sonemember_sex);
		    		
		    		TextView moible=(TextView)findViewById(R.id.member_mobile);
		    		moible.setText(R.string.sonemember_mobile);
		    		
		    		TextView join=(TextView)findViewById(R.id.member_join);
		    		join.setText(R.string.sonemember_joinday);
		    		
		    		TextView rate=(TextView)findViewById(R.id.member_rate);
		    		rate.setText(R.string.sonemember_rate);
		    		
		    		TextView over=(TextView)findViewById(R.id.member_over);
		    		over.setText(R.string.sonemember_money);
		    		
		    		TextView status=(TextView)findViewById(R.id.member_status);
		    		status.setText(R.string.sonemember_status);

		    		SearchOneMemberRunnable str=new SearchOneMemberRunnable();
	 				str.setMemberNo(query); 				
	 				Thread th=new Thread(str);
	 				th.start();
	 				
	 				InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	 				//im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	 				im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		if(memberno.length()>0){
			sv.setVisibility(View.GONE);
			sv.setQuery(memberno, true);
		}
    }
    
    class SearchOneMemberRunnable implements Runnable{  
 		private String memberNo="";
 		public void setMemberNo(String no){
 			this.memberNo=no;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.sonemember_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/public.php?method=searchonemember&memberno="+this.memberNo);
    	    	System.out.println("url="+url.toString());
    	    	byte[] entity = xml.getBytes("UTF-8");
    	    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	    	conn.setConnectTimeout(5000);
    	    	conn.setRequestMethod("POST");
    	    	conn.setDoOutput(true);
    	    	conn.setDoInput(true);
    	    	conn.setUseCaches(false);
    	    	
    	    	//指定发送的内容类型为xml
    	    	conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
    	    	conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
    	    	conn.setRequestProperty("Charset", "utf-8");
    	    	OutputStream outStream = conn.getOutputStream();
    	    	outStream.write(entity);
    	    	outStream.flush();
    	    	outStream.close();
    	    	
    	    	if(conn.getResponseCode() == 200){
    	    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
    	    		String resultString = "";
    	    		String readLine = "";
    	    		while((readLine=bufferedReader.readLine())!= null){
    	    			resultString += readLine;
    	    		}
    	    		bufferedReader.close();
    	    		conn.disconnect();
    	    		
    	    		System.out.println(resultString);
    	    		
    	    		if(resultString.equals("NULL")){
    	    			hintmsg=getString(R.string.sonemember_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
    	    			message = Message.obtain();
    	    			Bundle bd=new Bundle();
    	    			bd.putString("xmlstring", resultString);
    	    			bd.putString("cmd", "q_member_one");
    	    			message.setData(bd);
    	    			handler.sendMessage(message);
    	    		}
    	    	}else{
	    			hintmsg=getString(R.string.sonemember_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.sonemember_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	/*if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
    		if (event.getAction() == KeyEvent.ACTION_UP) {
 				InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
 				if(im.isActive()){
	 				im.hideSoftInputFromWindow(SearchOneMemberActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
 				}
    		}
    	}*/
    	return super.dispatchKeyEvent(event);    
    }

	public Handler handler=new Handler(){
 		@Override
		public void handleMessage(Message msg){
    		Bundle bd=msg.getData();
    		System.out.println(bd.getString("cmd"));
 			if(bd.getString("cmd").toString().equalsIgnoreCase("q_member_one")){			
    			initMemberInfo(bd.getString("xmlstring"));
  			}
 		}
	};
	
	public Handler msghandler=new Handler(){
 		public void handleMessage(Message msg){
    		String message=(String)msg.obj;
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, SearchOneMemberActivity.this);
    	}
   	};
        
    
    private void initMemberInfo(String xml){
		try{
			InputStream iss = new ByteArrayInputStream(xml.getBytes());  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(iss);
	    	
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	//System.out.println(nsList.getLength()+"");
    		Node n=nsList.item(0);
    		String t="";
    		
    		TextView no=(TextView)findViewById(R.id.member_no);
    		t=no.getText().toString();
    		t=t+n.getAttributes().getNamedItem("cardno").getNodeValue();
    		no.setText(t);
    		
    		TextView name=(TextView)findViewById(R.id.member_name);
    		t=name.getText().toString();
    		t=t+n.getAttributes().getNamedItem("name").getNodeValue();
    		name.setText(t);
    		
    		TextView sex=(TextView)findViewById(R.id.member_sex);
    		t=sex.getText().toString();
    		t=t+n.getAttributes().getNamedItem("sex").getNodeValue();
    		sex.setText(t);
    		
    		TextView moible=(TextView)findViewById(R.id.member_mobile);
    		t=moible.getText().toString();
    		t=t+n.getAttributes().getNamedItem("mobileno").getNodeValue();
    		moible.setText(t);
    		
    		TextView join=(TextView)findViewById(R.id.member_join);
    		t=join.getText().toString();
    		t=t+n.getAttributes().getNamedItem("creationtime").getNodeValue();
    		join.setText(t);
    		
    		TextView rate=(TextView)findViewById(R.id.member_rate);
    		t=rate.getText().toString();
    		t=t+n.getAttributes().getNamedItem("cardrate").getNodeValue();
    		rate.setText(t);
    		
    		TextView over=(TextView)findViewById(R.id.member_over);
    		t=over.getText().toString();
    		t=t+n.getAttributes().getNamedItem("cardover").getNodeValue()+getString(R.string.string_unit_money);
    		over.setText(t);
    		
    		TextView status=(TextView)findViewById(R.id.member_status);
    		t=status.getText().toString();
    		if(n.getAttributes().getNamedItem("cardstatus").getNodeValue().equals("0")){
    			t=t+getString(R.string.member_card_status_ok);
    		}else{
    			t=t+getString(R.string.member_card_status_err);
    		}
    		status.setText(t);
    		
    		myCls.CancelToast();
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }

    @Override
	public boolean onNavigateUp() {
    	finish();
		return super.onNavigateUp();
	}
}