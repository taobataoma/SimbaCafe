package com.oldhawk.simbacafe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class MyResultActivity extends Activity {
	private ListView resultListView;
	private MyClass myCls=new MyClass();
	private resultListAdapter adapter;
	private Date fromDate,toDate;
	private PublicValue pubValue=new PublicValue();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myresult);
		
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
    
		initDateString();
		
		resultListView=(ListView)findViewById(R.id.listview_result);
        adapter = new resultListAdapter(this);  
        resultListView.setAdapter(adapter); 
        resultListView.setOnItemClickListener(new listItemClickListener());
        
        ListResultRunnable str=new ListResultRunnable();
		Thread th=new Thread(str);
		th.start();
	}
	
	private class listItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			final Node n=adapter.arrNodes.get(arg2);
			Intent intent=new Intent();
    		intent.putExtra("mydate", n.getAttributes().getNamedItem("cdate").getNodeValue());
    		intent.setClass(MyResultActivity.this, SearchOneMyResultActivity.class);
    		startActivity(intent);
		}
	}

	private void initDateString(){
		TextView dateTextView=(TextView)findViewById(R.id.myresult_status);
		Intent intent=getIntent();
		
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
			fromDate = sdf.parse(intent.getStringExtra("datefrom"));
			toDate = sdf.parse(intent.getStringExtra("dateto"));
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
	
	public Handler msghandler=new Handler(){
 		public void handleMessage(Message msg){
    		String message=(String)msg.obj;
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, MyResultActivity.this);
    	}
   	};

	public Handler handler=new Handler(){
 		@Override
		public void handleMessage(Message msg){
    		Bundle bd=msg.getData();
    		System.out.println(bd.getString("cmd"));
 			if(bd.getString("cmd").toString().equalsIgnoreCase("q_myresult_list")){			
    			initResultInfo(bd.getString("xmlstring"));
  			}
 		}
	};
	
    private void initResultInfo(String xml){
		try{
			InputStream iss = new ByteArrayInputStream(xml.getBytes());  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(iss);
	    	
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	//System.out.println(nsList.getLength()+"");
	    	int count=0;
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		count=count+Integer.parseInt(n.getAttributes().getNamedItem("award").getNodeValue());
	    		adapter.arrNodes.add(n);
	    	}
	    	adapter.notifyDataSetChanged();
    		
	    	TextView tv=(TextView)findViewById(R.id.myresult_count);
	    	String t=getString(R.string.myresult_count);
	    	t=t+count+getString(R.string.string_unit_money);

	    	tv.setText(t);
	    	
    		myCls.CancelToast();
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }
	
    private class resultListAdapter extends BaseAdapter {  
        
        private Context context;  
        private LayoutInflater inflater;  
        public ArrayList<Node> arrNodes;  
        public resultListAdapter(Context con) {  
            super();  
            this.context = con;  
            inflater = LayoutInflater.from(context);  
            arrNodes = new ArrayList<Node>();  
        }  
        @Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return arrNodes.size();  
        }  
        @Override  
        public Object getItem(int arg0) {  
            // TODO Auto-generated method stub  
            return arg0;  
        }  
        @Override  
        public long getItemId(int arg0) {  
            // TODO Auto-generated method stub  
            return arg0;  
        }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
            // TODO Auto-generated method stub  
			if(convertView == null){  
            	convertView = inflater.inflate(R.layout.view_resultlist_item, null);  
            }
            Node n=arrNodes.get(position);
            
            TextView tdate=(TextView)convertView.findViewById(R.id.result_date);
            TextView tcount=(TextView)convertView.findViewById(R.id.result_item);
            String t=n.getAttributes().getNamedItem("cdate").getNodeValue();
            tdate.setText(t);
            t=n.getAttributes().getNamedItem("award").getNodeValue();
            t=t+getString(R.string.string_unit_money);
            tcount.setText(t);
            return convertView;  
		}  
    }
	
    class ListResultRunnable implements Runnable{  
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.myresult_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    			String fd=(String) DateFormat.format("yyyy-MM-dd", fromDate.getTime());
    			String td=(String) DateFormat.format("yyyy-MM-dd", toDate.getTime());
    			
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/public.php?method=getmyresult&from="+fd+"&to="+td+"&login="+pubValue.GetLoginName());
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
    	    			hintmsg=getString(R.string.myresult_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
    	    			message = Message.obtain();
    	    			Bundle bd=new Bundle();
    	    			bd.putString("xmlstring", resultString);
    	    			bd.putString("cmd", "q_myresult_list");
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
}
