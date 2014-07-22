package com.oldhawk.simbacafe;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


@SuppressLint("HandlerLeak")
public class DianDanActivity extends Activity {
	private final MyClass myCls = new MyClass();
	private PublicValue pubValue=new PublicValue();
	public static DianDanActivity staticDianDanActivity;
	private ListView listview;  
	private menuFinalListAdapter adapter;  
	private long delTime = 0;
	private String tableno;
	private String maxuser;
	private int currUserNums = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diandan);
		
		staticDianDanActivity=this;

		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        
		listview = (ListView)DianDanActivity.this.findViewById(R.id.select_listview_finallist);  
        adapter = new menuFinalListAdapter(DianDanActivity.this);  
        listview.setAdapter(adapter); 
        listview.setOnItemClickListener(new MenuItemClickListener());
		
		// 显示登录的用户名于标题栏上
		this.setTitle(this.getTitle() + " - " + pubValue.GetUserName());
		
		TextView t=(TextView)findViewById(R.id.diandan_total);
		t.setText("0");
		
		Intent intent = getIntent();
		Bundle bd=intent.getBundleExtra("bundlePara");
		
		TextView tablenoTextView=(TextView)findViewById(R.id.textView_tableno);
		tablenoTextView.setText(tablenoTextView.getText()+bd.getString("tableno"));
		tableno=bd.getString("tableno");
		maxuser=bd.getString("maxuser");
		
		TextView minconsTextView=(TextView)findViewById(R.id.textView_mincons);
		if(bd.getString("constype").equalsIgnoreCase("all")){
			minconsTextView.setText(minconsTextView.getText().toString()+bd.getString("mincons")+getString(R.string.string_unit_money));
		}else if(bd.getString("constype").equalsIgnoreCase("per")){
			minconsTextView.setText(minconsTextView.getText().toString()+bd.getString("mincons")+"/"+getString(R.string.string_unit_pepole));
		}
		
		if(bd.getString("tablestatus").equals("0")){
			doConfigCurrUsers();
		}
	}
	
    private void doConfigCurrUsers(){
		final AlertDialog.Builder altDB = new AlertDialog.Builder(this);
		altDB.setTitle(getString(R.string.currusers_title));
		altDB.setInverseBackgroundForced(true);
		// 创建内容显示区域view
		LayoutInflater inflater=LayoutInflater.from(this);    
	    final View tView=inflater.inflate(R.layout.dialog_currusers, null);
	    
	    TextView ets=(TextView)tView.findViewById(R.id.select_textview_usernums);
	    ets.setText(maxuser);
	    
	    TextView inv=(TextView)tView.findViewById(R.id.currusers_input);
	    inv.setText(String.format(getString(R.string.currusers_input),tableno));
	    
		Button btjia=(Button)tView.findViewById(R.id.select_button_jia);
		Button btjian=(Button)tView.findViewById(R.id.select_button_jian);
		
		btjia.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView unums=(TextView)tView.findViewById(R.id.select_textview_usernums);
		    	String si=unums.getText().toString();
		    	if(si.length()>0){
		    		int i=Integer.parseInt(si);
		    		i++;
		    		unums.setText(i+"");
		    	}
			}
		});
		
		btjian.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView unums=(TextView)tView.findViewById(R.id.select_textview_usernums);
		    	String si=unums.getText().toString();
		    	if(si.length()>0){
		    		int i=Integer.parseInt(si);
	    			i--;
	    			if(i==0)	i++;
	    			unums.setText(i+"");
		    	}
			}
		});
		
		altDB.setNegativeButton(R.string.string_button_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						TextView ets=(TextView)tView.findViewById(R.id.select_textview_usernums);
						currUserNums=Integer.parseInt(ets.getText().toString());
						try {						 
	                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
	                        field.setAccessible(true);
	                        field.set(dialog,true);//关闭
	                     } catch (Exception e) {
	                        e.printStackTrace();
	                     } 
					}
				}).create();
	    //设置dialog的view
	    altDB.setView(tView);
	    //显示对话框
	    final AlertDialog dlg=altDB.show();
	    Button b=(Button)dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
	    b.setTextSize(18);
	    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
    }
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(adapter.arrNodes.size()>0){
				final AlertDialog.Builder altDB = new AlertDialog.Builder(DianDanActivity.this);
				altDB.setTitle(getString(R.string.diandan_dialog_title_exit));
				altDB.setInverseBackgroundForced(true);
				altDB.setMessage(getString(R.string.diandan_dialog_message_exit));
			    altDB.setPositiveButton(R.string.string_button_Cancel,null);
				altDB.setNegativeButton(R.string.string_button_OK,
						new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Message message;
					    		message = Message.obtain();
					    		message.arg1 = 0;
					    		handler.sendMessage(message);
					    		dialog.dismiss();
							}
						}).create();
			    AlertDialog dlg=altDB.show();
			    Button b=(Button)dlg.getButton(DialogInterface.BUTTON_POSITIVE);
			    b.setTextSize(16);
			    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
			    b=(Button)dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
			    b.setTextSize(16);
			    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
    		}else{
    	   		return super.onKeyDown(keyCode, event);
    		}
    	}
   		return false;
    }
    
	@Override
	public boolean onNavigateUp() {
		try{
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
		
		return super.onNavigateUp();
	}
	
    public void DoAddItemToMenuList(View v){
		Intent intent = new Intent();
		intent.setClass(DianDanActivity.this, SelectMenuActivity.class);
		startActivity(intent);
    }
    
    public void ReadyCommitMenu(View v){
    	String hintmsg;
    	Message message;
    	if(adapter.arrNodes.size()<=0){
			hintmsg=getString(R.string.diandan_toast_nomenuitem);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
    		return;
    	}
    	
		final AlertDialog.Builder altDB = new AlertDialog.Builder(DianDanActivity.this);
		altDB.setTitle(getString(R.string.diandan_dialog_title_commit));
		altDB.setInverseBackgroundForced(true);
		altDB.setMessage(getString(R.string.diandan_dialog_message_commit));
	    altDB.setPositiveButton(R.string.string_button_Cancel,null);
		altDB.setNegativeButton(R.string.string_button_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						DoCommitMenu();
			    		dialog.dismiss();
					}
				}).create();
	    AlertDialog dlg=altDB.show();
	    Button b=(Button)dlg.getButton(DialogInterface.BUTTON_POSITIVE);
	    b.setTextSize(16);
	    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
	    b=(Button)dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
	    b.setTextSize(16);
	    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
    	
    }
    
    public void DoCommitMenu(){
    	try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            
            Element rootElement = document.createElement("root");
            rootElement.setAttribute("tableno", tableno);
            rootElement.setAttribute("currusernums", currUserNums+"");
            document.appendChild(rootElement);
            
            for(int i=0;i<adapter.arrNodes.size();i++){
            	Node node=adapter.arrNodes.get(i);
            	Element item=document.createElement("item");
            	item.setAttribute("idx", node.getAttributes().getNamedItem("idx").getNodeValue());
            	item.setAttribute("nums", node.getAttributes().getNamedItem("nums").getNodeValue());
            	item.setAttribute("award", node.getAttributes().getNamedItem("award").getNodeValue());
            	item.setAttribute("usertips", node.getAttributes().getNamedItem("usertips").getNodeValue());
            	if(node.getAttributes().getNamedItem("saleprice").getNodeValue().equals("0")){
            		item.setAttribute("price", node.getAttributes().getNamedItem("price").getNodeValue());
            		item.setAttribute("issale", "0");
            	}else{
            		item.setAttribute("price", node.getAttributes().getNamedItem("saleprice").getNodeValue());
            		item.setAttribute("issale", "1");
            	}
            	item.setAttribute("canrate", node.getAttributes().getNamedItem("canrate").getNodeValue());
            	item.setAttribute("subitemidx", node.getAttributes().getNamedItem("subitemidx").getNodeValue());
            	item.setAttribute("freeitemidx", node.getAttributes().getNamedItem("freeitemidx").getNodeValue());
            	rootElement.appendChild(item);
            }
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Properties outFormat = new Properties();
            outFormat.setProperty(OutputKeys.INDENT, "yes");
            outFormat.setProperty(OutputKeys.METHOD, "xml");
            outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            outFormat.setProperty(OutputKeys.VERSION, "1.0");
            outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperties(outFormat);
            DOMSource domSource = new DOMSource(document.getDocumentElement());
            OutputStream output = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(domSource, result);
            String xmlString = output.toString();
            
            System.out.println(xmlString);
            
            Bundle bd=new Bundle();
            bd.putString("xml", xmlString);
            bd.putString("loginname", pubValue.GetLoginName());
            
            CommitMenuRunnable upr=new CommitMenuRunnable();
            upr.setDataBundle(bd); 				
			Thread th=new Thread(upr);
			th.start();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    class CommitMenuRunnable implements Runnable{  
 		private Bundle bd;
 		public void setDataBundle(Bundle b){
 			this.bd=b;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.diandan_commit_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=commit&user="+bd.getString("loginname"));
    	    	System.out.println("url="+url.toString());
    	    	byte[] entity = bd.getString("xml").getBytes("UTF-8");
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
    	    		
    	    		if(resultString.equals("OK")){
    	    			hintmsg=getString(R.string.diandan_commit_ok);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			myCls.CancelToast();
    	    			msghandler.sendMessage(message);
    	    			finish();
    	    		}else{
    	    			hintmsg=getString(R.string.diandan_commit_faild);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}    		
    	    	}else{
	    			hintmsg=getString(R.string.diandan_commit_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.diandan_commit_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
   		
	public Handler handler=new Handler(){
 		@Override
		public void handleMessage(Message msg){
 			if(msg.arg1==1){
 				Node n=(Node)msg.obj;
 				
 				adapter.arrNodes.add(0, n);  
 	            adapter.notifyDataSetChanged();  
 	            updateTotalPrice();
 			}if(msg.arg1==0){
 				finish();
 			}
     	}
   	};
   	
	public Handler msghandler=new Handler(){
 		public void handleMessage(Message msg){
    		String message=(String)msg.obj;
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, DianDanActivity.this);
    	}
   	};
   	
   	private final void updateTotalPrice(){
        int total=0;
   		for(int i=0;i<adapter.arrNodes.size();i++){
   			Node n=adapter.arrNodes.get(i);
	   		int nums=Integer.parseInt(n.getAttributes().getNamedItem("nums").getNodeValue());
	        int price=Integer.parseInt(n.getAttributes().getNamedItem("saleprice").getNodeValue());
	        if(price==0)
	         	price=Integer.parseInt(n.getAttributes().getNamedItem("price").getNodeValue());
	
	        total+=nums*price;
        }
        System.out.println("小计："+total+"");
        TextView t=(TextView)findViewById(R.id.diandan_total);
        t.setText(total+"");
   	}
   	
    private class menuFinalListAdapter extends BaseAdapter {  
        
        private Context context;  
        private LayoutInflater inflater;  
        public ArrayList<Node> arrNodes;  
        public menuFinalListAdapter(Context con) {  
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
            int price=0;
            int nums=0;
			if(convertView == null){  
            	convertView = inflater.inflate(R.layout.view_menufinallist_item, null);  
            }
            Node n=arrNodes.get(position);
            
            TextView tTitle=(TextView)convertView.findViewById(R.id.menushowlist_title);
            TextView tDesc=(TextView)convertView.findViewById(R.id.menushowlist_desc);
            TextView tTips=(TextView)convertView.findViewById(R.id.menushowlist_tips);
            
            String title=n.getAttributes().getNamedItem("idx").getNodeValue();
            title+="：";
            title+=n.getAttributes().getNamedItem("name").getNodeValue();
           
            String desc=getString(R.string.select_price_title);
            desc+="：";
            if(n.getAttributes().getNamedItem("saleprice").getNodeValue().equals("0")){
            	price=Integer.parseInt(n.getAttributes().getNamedItem("price").getNodeValue());
            	desc+=n.getAttributes().getNamedItem("price").getNodeValue();
            	desc+=getString(R.string.string_unit_money);
            	desc+="/";
            	desc+=n.getAttributes().getNamedItem("unit").getNodeValue();
            }else{
            	price=Integer.parseInt(n.getAttributes().getNamedItem("saleprice").getNodeValue());
            	desc+=n.getAttributes().getNamedItem("saleprice").getNodeValue();
            	desc+=getString(R.string.string_unit_money);
            	desc+="/";
            	desc+=n.getAttributes().getNamedItem("unit").getNodeValue();
            	desc+="，";
            	desc+=getString(R.string.select_oldprice);
            	desc+=n.getAttributes().getNamedItem("price").getNodeValue();
            	desc+=getString(R.string.string_unit_money);
            	desc+="/";
            	desc+=n.getAttributes().getNamedItem("unit").getNodeValue();
            }

            nums=Integer.parseInt(n.getAttributes().getNamedItem("nums").getNodeValue());
            title+=" (";
            title+=nums*price+getString(R.string.string_unit_money);
            title+="/";
            title+=n.getAttributes().getNamedItem("nums").getNodeValue();
            title+=n.getAttributes().getNamedItem("unit").getNodeValue();
            title+=")";
            
            String tips=getString(R.string.select_usertips);
            tips+=n.getAttributes().getNamedItem("usertips").getNodeValue();
            
            tTitle.setText(title);
            tDesc.setText(desc);
            tTips.setText(tips);
                        
            return convertView;  
		}  
    }
    
	private class MenuItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			final Node n=adapter.arrNodes.get(arg2);
			
			final AlertDialog.Builder altDB = new AlertDialog.Builder(DianDanActivity.this);
			altDB.setTitle(getString(R.string.diandan_dialog_title_edit));
			altDB.setInverseBackgroundForced(true);
			// 创建内容显示区域view
			LayoutInflater inflater=LayoutInflater.from(DianDanActivity.this);    
		    final View tView=inflater.inflate(R.layout.dialog_editmenuitem, null);

		    altDB.setPositiveButton(R.string.select_button_editcancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							try {						 
		                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
		                        field.setAccessible(true);
		                        field.set(dialog,true);//关闭
		                     } catch (Exception e) {
		                        e.printStackTrace();
		                     } 
						}
					});
		    altDB.setNeutralButton(R.string.select_button_editdel,
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					try {						 
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        if((System.currentTimeMillis()-delTime) > 2000){  
                        	myCls.AlertToast(getString(R.string.string_press_delete), Gravity.TOP, Toast.LENGTH_LONG, DianDanActivity.this);
                            delTime = System.currentTimeMillis();
                            field.set(dialog,false);//不关闭
                        } else {
        					adapter.arrNodes.remove(n);
        					adapter.notifyDataSetChanged();
        					updateTotalPrice();
                        	field.set(dialog,true);//删除并关闭
                        }
                     } catch (Exception e) {
                        e.printStackTrace();
                     } 
				}
			});
			altDB.setNegativeButton(R.string.select_button_editok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
						    TextView t=(TextView)tView.findViewById(R.id.select_textview_menunums);
						    TextView t1=(TextView)tView.findViewById(R.id.select_edittext_usertips);
							n.getAttributes().getNamedItem("nums").setNodeValue(t.getText().toString());
							n.getAttributes().getNamedItem("usertips").setNodeValue(t1.getText().toString());
							adapter.notifyDataSetChanged();
							updateTotalPrice();
							
							try {						 
		                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
		                        field.setAccessible(true);
		                        field.set(dialog,true);//关闭
		                     } catch (Exception e) {
		                        e.printStackTrace();
		                     } 
						}
					}).create();
		    //设置dialog的view
		    altDB.setView(tView);
		    //显示对话框
		    final AlertDialog dlg=altDB.show();
		    Button b=(Button)dlg.getButton(DialogInterface.BUTTON_POSITIVE);
		    b.setTextSize(16);
		    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
		    b=(Button)dlg.getButton(DialogInterface.BUTTON_NEGATIVE);
		    b.setTextSize(16);
		    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
		    b=(Button)dlg.getButton(DialogInterface.BUTTON_NEUTRAL);
		    b.setTextSize(16);
		    b.setBackgroundResource(R.drawable.publicalertbuttonbackground);
		    //填充值
		    TextView t=(TextView)tView.findViewById(R.id.select_edittext_menuidx);
		    t.setText(n.getAttributes().getNamedItem("idx").getNodeValue());
		    TextView t1=(TextView)tView.findViewById(R.id.select_textview_menuname);
		    t1.setText(n.getAttributes().getNamedItem("name").getNodeValue());
		    TextView t2=(TextView)tView.findViewById(R.id.select_textview_menunums);
		    t2.setText(n.getAttributes().getNamedItem("nums").getNodeValue());
		    TextView t3=(TextView)tView.findViewById(R.id.select_edittext_usertips);
		    t3.setText(n.getAttributes().getNamedItem("usertips").getNodeValue());	
		    
		    Button ba=(Button)tView.findViewById(R.id.select_button_jia);
		    Button bj=(Button)tView.findViewById(R.id.select_button_jian);
		    
		    ba.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView t=(TextView)tView.findViewById(R.id.select_textview_menunums);
					int num=Integer.parseInt(t.getText().toString());
				    num++;
					t.setText(num+"");
				}
			});
		    
		    bj.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView t=(TextView)tView.findViewById(R.id.select_textview_menunums);
					int num=Integer.parseInt(t.getText().toString());
				    num--;
				    if(num==0)
				    	num=1;
					t.setText(num+"");
				}
			});
		}
	}
}
