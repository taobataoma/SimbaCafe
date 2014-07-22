package com.oldhawk.simbacafe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class TableStatusActivity extends Activity {
	private final MyClass myCls = new MyClass();
	private ArrayList<Node> arrayTables=new ArrayList<Node>();
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tablestatus);
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		intent=getIntent();
		
		TextView tView=(TextView)findViewById(R.id.textview_tablestatus);
		tView.setText(intent.getStringExtra("statusTitle"));
		
		GetTableStatusRunnable htr=new GetTableStatusRunnable();
		Thread th=new Thread(htr);
		th.start();
		
		GetCurrTableCountRunnable gtc=new GetCurrTableCountRunnable();
		Thread thg=new Thread(gtc);
		thg.start();
	}

    @Override
	public boolean onNavigateUp() {
    	finish();
		return super.onNavigateUp();
	}
    
    class GetTableStatusRunnable implements Runnable{  
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_gettablestatus_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=tablestatus");
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
    	    			hintmsg=getString(R.string.string_gettablestatus_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else if(resultString.length()>0){
    	    			message = Message.obtain();
    	    			message.arg1=0;
    	    			message.obj=resultString;
    	    			handler.sendMessage(message);
    	    		}
    	    	}else{
	    			hintmsg=getString(R.string.string_gettablestatus_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_gettablestatus_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    class GetCurrTableCountRunnable implements Runnable{  
    	@Override  
    	public void run() {
 	    	Message message;
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=gettablecount");
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
    	    		
    	    		if(!resultString.equals("NULL") && resultString.length()>0){
    	    			message = Message.obtain();
    	    			message.arg1=2;
    	    			message.obj=resultString;
    	    			handler.sendMessage(message);
    	    		}
    	    	}
        	}catch(Exception e){
        		;
        	}
    	}  
    };
    
    private void initTableStatus(String xml){
		try{
			InputStream iss = new ByteArrayInputStream(xml.getBytes());  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(iss);
	    	
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		arrayTables.add(n);
	    		GridLayout grid=(GridLayout)this.findViewById(R.id.tablestatus_gridlayout);
	    		Button b=new Button(this);
			    
	    		GridLayout.LayoutParams paButton = new GridLayout.LayoutParams();//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			    paButton.height=120;
			    paButton.width=120;
	    		paButton.setMargins(10, 10, 10, 10);
			    b.setLayoutParams(paButton);
			    b.setText(n.getAttributes().getNamedItem("tablename").getNodeValue());
			    if(n.getAttributes().getNamedItem("tablestatus").getNodeValue().equals("1")){
				    //b.setBackgroundColor(Color.rgb(255,204,102));
				    b.setBackgroundResource(R.drawable.tablestatusfullbackground);
				    b.setTag(1);
			    }else{
				    //b.setBackgroundColor(Color.rgb(221,221,221));
				    if(n.getAttributes().getNamedItem("ordertime").getNodeValue().length()>0){
					    //b.setBackgroundColor(Color.rgb(102,204,255));
					    b.setBackgroundResource(R.drawable.tablestatusorderbackground);
					    b.setTag(2);
				    }else{
					    b.setBackgroundResource(R.drawable.tablestatusnullbackground);
					    b.setTag(0);
				    }
			    }
			    b.setOnClickListener(new buttonClickListner());
			    grid.addView(b);
	    	}
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }
    
    class SearchOneOrderRunnable implements Runnable{  
 		private Bundle bd;
 		public void setDataBundle(Bundle b){
 			this.bd=b;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_searchorder_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=searchoneorder&tableno="+bd.getString("tableno"));
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
    	    			hintmsg=getString(R.string.string_searchorder_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
    	    			message = Message.obtain();
    	    			message.arg1=1;
    	    			message.obj=resultString;
    	    			handler.sendMessage(message);
    	    		}
    	    	}else{
	    			hintmsg=getString(R.string.string_searchorder_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_searchorder_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };

    private void initOrderStatus(String xml){
		try{
			InputStream iss = new ByteArrayInputStream(xml.getBytes());  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(iss);
	    	
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	if(nsList.getLength()>0){
	    		Node n=nsList.item(0);
				MyClass cls = new MyClass();
				String hintmsg = getString(R.string.yudinglist_item_ordername)+"：";
				hintmsg+=n.getAttributes().getNamedItem("ordername").getNodeValue()+"\n";
				
				hintmsg+=getString(R.string.yudinglist_item_ordertime)+"：";
				hintmsg+=n.getAttributes().getNamedItem("ordertime").getNodeValue()+"\n";
				
				hintmsg+=getString(R.string.yudinglist_item_ordertel)+"：";
				hintmsg+=n.getAttributes().getNamedItem("ordertel").getNodeValue()+"\n";
				
				hintmsg+=getString(R.string.yudinglist_item_orderusers)+"：";
				hintmsg+=n.getAttributes().getNamedItem("orderusers").getNodeValue()+getString(R.string.string_unit_pepole);
				
				String titleMsg=n.getAttributes().getNamedItem("ordertablename").getNodeValue();
				titleMsg+=getString(R.string.yudinglist_item_orderdetail);
				cls.AlertDialog(titleMsg, hintmsg, Gravity.CENTER,
						TableStatusActivity.this);
	    	}
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }

    class buttonClickListner implements View.OnClickListener {

		@Override
		public void onClick(View v) {
	 	    Message message;
			Button b=(Button)v;
			
			if(intent.getStringExtra("cmd").equalsIgnoreCase("MAIN")){
				Node n=getTableStatusItem(b.getText().toString());				
				MainActivity mc=MainActivity.staticMainActivity;
	    		Bundle bd=new Bundle();
	    		
	    		bd.putString("cmd", intent.getStringExtra("cmdstring"));
	    		bd.putString("tableno",b.getText().toString());
	    		bd.putString("tablestatus", b.getTag().toString());
	    		bd.putString("oldtableno", intent.getStringExtra("oldtableno"));
	    		bd.putString("mincons", n.getAttributes().getNamedItem("mincons").getNodeValue().toString());
	    		bd.putString("constype", n.getAttributes().getNamedItem("constype").getNodeValue().toString());
	    		bd.putString("maxuser", n.getAttributes().getNamedItem("maxuser").getNodeValue().toString());
	    		
	    		message = Message.obtain();
	    		message.setData(bd);
	    		mc.handler.sendMessage(message);	    		
	    		TableStatusActivity.this.finish();
			}else if(Integer.parseInt(b.getTag().toString())==0){
				Node n=getTableStatusItem(b.getText().toString());				
				MainActivity mc=MainActivity.staticMainActivity;
	    		Bundle bd=new Bundle();
	    		
	    		bd.putString("cmd", "string_com_1");
	    		bd.putString("tableno",b.getText().toString());
	    		bd.putString("tablestatus", b.getTag().toString());
	    		bd.putString("oldtableno", intent.getStringExtra("oldtableno"));
	    		bd.putString("mincons", n.getAttributes().getNamedItem("mincons").getNodeValue().toString());
	    		bd.putString("constype", n.getAttributes().getNamedItem("constype").getNodeValue().toString());
	    		bd.putString("maxuser", n.getAttributes().getNamedItem("maxuser").getNodeValue().toString());
	    		
	    		message = Message.obtain();
	    		message.setData(bd);
	    		mc.handler.sendMessage(message);
			}else if(Integer.parseInt(b.getTag().toString())==1){
				MainActivity mc=MainActivity.staticMainActivity;
	    		Bundle bd=new Bundle();
	    		
	    		bd.putString("cmd", "string_com_3");
	    		bd.putString("tableno",b.getText().toString());
	    		
	    		message = Message.obtain();
	    		message.setData(bd);
	    		mc.handler.sendMessage(message);
			}else if(Integer.parseInt(b.getTag().toString())==2){
				SearchOneOrderRunnable sor=new SearchOneOrderRunnable();
				Bundle bd=new Bundle();
				bd.putString("tableno", b.getText().toString());
				sor.setDataBundle(bd);
				Thread th=new Thread(sor);
				th.start();
			}
		}

	}
    
    private Node getTableStatusItem(String tablename){
    	for(int i=0;i<arrayTables.size();i++){
    		Node n=arrayTables.get(i);
    		if(n.getAttributes().getNamedItem("tablename").getNodeValue().equals(tablename)){
    			return n;
    		}
    	}
    	return null;
    }
    
	public Handler handler=new Handler(){
 		@Override
		public void handleMessage(Message msg){
 			if(msg.arg1==0){
 				String str=(String)msg.obj;
 				myCls.CancelToast();
 				initTableStatus(str);
  			}else if(msg.arg1==1){
 				String str=(String)msg.obj;
 				myCls.CancelToast();
 				initOrderStatus(str);
  			}else if(msg.arg1==2){
  				String str=(String)msg.obj;
  				TextView tView=(TextView)findViewById(R.id.textview_tablestatus);
  				tView.setText(getString(R.string.tablestatus_status)+": ("+str+getString(R.string.string_unit_money)+")");
  			}
 		}
	};
	
  	public Handler msghandler=new Handler(){
 		public void handleMessage(Message msg){
    		String message=(String)msg.obj;
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, TableStatusActivity.this);
    	}
   	};
}
