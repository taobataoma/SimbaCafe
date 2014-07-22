package com.oldhawk.simbacafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	private final MyClass myCls = new MyClass();
	private long exitTime = 0;
	private PublicValue pubValue=new PublicValue();
	public static MainActivity staticMainActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		staticMainActivity=this;
		// 显示登录的用户名于标题栏上
		Intent intent = getIntent();
		String loginname = intent.getStringExtra("loginname");
		String username = intent.getStringExtra("username");
		int useraccess = intent.getIntExtra("useraccess", 0);
		this.setTitle(this.getTitle() + " - " + username);
		
		pubValue.SetUserName(username);
		pubValue.SetLoginName(loginname);
		pubValue.SetUserAccess(useraccess);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_exit){
			ExitSystem();
		}else if (item.getItemId() == R.id.action_about) {
			MyClass cls = new MyClass();
			String hintmsg = getString(R.string.app_name2);
			hintmsg = hintmsg + "\n" + getString(R.string.string_copyright);
			hintmsg = hintmsg + "\n" + getString(R.string.string_version);
			String titleMsg = getString(R.string.action_about);
			cls.AlertDialog(titleMsg, hintmsg, Gravity.BOTTOM,
					MainActivity.this);
		}else if(item.getItemId() == R.id.action_updatetables){
			doUpdateTables();
		}else if(item.getItemId() == R.id.action_updatemenus){
			doUpdateMenus();
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
            if((System.currentTimeMillis()-exitTime) > 2000){  
            	myCls.AlertToast(getString(R.string.string_press_quit), Gravity.BOTTOM, Toast.LENGTH_LONG, MainActivity.this);
                exitTime = System.currentTimeMillis();   
            } else {
            	ExitSystem();
            }
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    private void ExitSystem(){
    	this.finish();
    	System.exit(0);
    }
    
    public void funLayoutClick(View v){
    	if(checkConfigFile()==false)
    		return;
    	
    	if(Integer.parseInt(v.getTag().toString())==0){			//台位状态
    		Intent intent=new Intent();
    		intent.putExtra("statusTitle", getString(R.string.tablestatus_status));
    		intent.putExtra("cmdstring", "NULL");
    		intent.putExtra("cmd", "STATUS");
    		intent.setClass(this, TableStatusActivity.class);
    		startActivity(intent);
    	}else if(Integer.parseInt(v.getTag().toString())==1){			//修改密码
    		doChangePassword();
    	}else if(Integer.parseInt(v.getTag().toString())==2){	//我的提成
    		doSelectDate(2);
    	}else if(Integer.parseInt(v.getTag().toString())==3){	//店内业绩
    		if(!getUserAccess(4)){
    			return;
    		}
    		doSelectDate(3);	
    	}else if(Integer.parseInt(v.getTag().toString())==4){	//签单查询
    		if(!getUserAccess(4)){
    			return;
    		}
    		doSelectDate(4);
    	}else if(Integer.parseInt(v.getTag().toString())==5){	//免单查询
    		if(!getUserAccess(4)){
    			return;
    		}
    		doSelectDate(5);
    	}else if(Integer.parseInt(v.getTag().toString())==6){	//会员查询
    		if(!getUserAccess(4)){
    			return;
    		}
    		Intent intent=new Intent();
    		intent.setClass(this, MemberListActivity.class);
    		startActivity(intent);
    	}else if(Integer.parseInt(v.getTag().toString())==7){	//支付查询
    		if(!getUserAccess(4)){
    			return;
    		}
    		doSelectDate(7);
    	}

    }
    
    private void doUpdateTables(){
		final AlertDialog.Builder altDB = new AlertDialog.Builder(this);
		altDB.setTitle(getString(R.string.string_confimdialog_title));
		altDB.setInverseBackgroundForced(true);
		altDB.setMessage(getString(R.string.string_action_updatetables));
	    altDB.setPositiveButton(R.string.string_button_Cancel,null);
		altDB.setNegativeButton(R.string.string_button_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						System.out.println("begin update tables");
						new Thread(UpdateTablesConfigRunnable).start();
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
    
    Runnable UpdateTablesConfigRunnable = new Runnable(){  
 		@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/getconfig.php?method=gettables");
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
    	    		
    	    		if(resultString.equals("NULL")){
    	    			hintmsg=getString(R.string.string_updatetables_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
        	    		System.out.println(resultString);
        	    		writeXMLToFile("tableconfig.xml",resultString,"TABLE");
    	    		}    		
    	    	}else{
	    			hintmsg=getString(R.string.string_updatetables_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_update_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    private void doUpdateMenus(){
		final AlertDialog.Builder altDB = new AlertDialog.Builder(this);
		altDB.setTitle(getString(R.string.string_confimdialog_title));
		altDB.setInverseBackgroundForced(true);
		altDB.setMessage(getString(R.string.string_action_updatemenus));
	    altDB.setPositiveButton(R.string.string_button_Cancel,null);
		altDB.setNegativeButton(R.string.string_button_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						System.out.println("begin update menus");
						new Thread(UpdateMenusConfigRunnable).start();
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
   
    private boolean getUserAccess(int acc){
    	if(pubValue.GetUserAccess()>=acc){
    		return true;
    	}else{
    		myCls.AlertToast(getString(R.string.string_noaccess), this);
    		return false;
    	}
    }
    
    Runnable UpdateMenusConfigRunnable = new Runnable(){  
 		@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/getconfig.php?method=getmenus");
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
    	    		
    	    		if(resultString.equals("NULL")){
    	    			hintmsg=getString(R.string.string_updatemenus_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
        	    		System.out.println(resultString);
        	    		writeXMLToFile("menuconfig.xml",resultString,"MENU");
    	    		}    		
    	    	}else{
	    			hintmsg=getString(R.string.string_updatemenus_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_update_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
   
    public void writeXMLToFile(String filename, String XML, String type){
    	Message message;
    	String hintmsg;
    	try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            	File appHome = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"//simba//"); 
            	File subPath = new File(appHome+"//dataconfig//"); 
            	appHome.mkdir(); 
            	subPath.mkdir(); 
            	// 写入
            	File file = new File(subPath.toString(), filename);
            	FileOutputStream fos = new FileOutputStream(file);
            	fos.write(XML.getBytes("UTF-8"));
            	fos.close();
    			
            	if(type=="TABLE")
            		hintmsg=getString(R.string.string_updatetables_ok);
            	else
            		hintmsg=getString(R.string.string_updatemenus_ok);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }  
    }
    
    private void doSelectDate(int tag){
		Intent intent = new Intent();
		intent.putExtra("tag", tag);
		intent.setClass(this, SelectDateActivity.class);
		this.startActivityForResult(intent,tag);
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode>0){
			System.out.println("datefrom="+data.getStringExtra("datefrom"));
			System.out.println("dateto="+data.getStringExtra("dateto"));
    	}
		if(resultCode==2){				//我的提成
			data.setClass(this, MyResultActivity.class);
			startActivity(data);
		}else if(resultCode==3){		//店内业绩
			data.setClass(this, AllResultActivity.class);
			startActivity(data);
		}else if(resultCode==4){		//签单查询
			data.setClass(this, QianDanActivity.class);
			startActivity(data);
		}else if(resultCode==5){		//免单查询
			data.setClass(this, MianDanActivity.class);
			startActivity(data);
		}else if(resultCode==7){		//支付查询
			data.setClass(this, PayTypeActivity.class);
			startActivity(data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doChangePassword(){
		final AlertDialog.Builder altDB = new AlertDialog.Builder(this);
		altDB.setTitle(getString(R.string.editpass_title));
		altDB.setInverseBackgroundForced(true);
		// 创建内容显示区域view
		LayoutInflater inflater=LayoutInflater.from(this);    
	    final View tView=inflater.inflate(R.layout.dialog_editpassword, null);

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
		altDB.setNegativeButton(R.string.select_button_editok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						EditText et=(EditText)tView.findViewById(R.id.editpass_oldpass);
						EditText etn=(EditText)tView.findViewById(R.id.editpass_newpass);
						EditText etnt=(EditText)tView.findViewById(R.id.editpass_newpasstoo);
                        
						try{
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
	                        field.setAccessible(true);
	                        if(et.getText().toString().trim().length()==0){  
	                        	myCls.AlertToast(getString(R.string.editpass_nooldpass), Gravity.TOP, Toast.LENGTH_LONG, MainActivity.this);
	                            field.set(dialog,false);//不关闭
	                            return;
	                        }else if(etn.getText().toString().trim().length()==0){  
	                        	myCls.AlertToast(getString(R.string.editpass_nonewpass), Gravity.TOP, Toast.LENGTH_LONG, MainActivity.this);
	                            field.set(dialog,false);//不关闭
	                            return;
	                        }else if(!etn.getText().toString().trim().equals(etnt.getText().toString().trim())){  
	                        	myCls.AlertToast(getString(R.string.editpass_notsamepass), Gravity.TOP, Toast.LENGTH_LONG, MainActivity.this);
	                            field.set(dialog,false);//不关闭
	                            return;
	                        }else{
					    		Message message;
					    		message = Message.obtain();
					    		Bundle bd=new Bundle();
					    		bd.putString("cmd", "change_password");
					    		bd.putString("oldpass", et.getText().toString().trim());
					    		bd.putString("newpass",etn.getText().toString().trim());
					    		message.setData(bd);
					    		handler.sendMessage(message);
	                        	field.set(dialog, true);//关闭
	                        }
						}catch(Exception ex){
							ex.printStackTrace();
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
    }
    
    public void DoMainFunction(View v){
    	if(checkConfigFile()==false)
    		return;

    	Button b=(Button)v;
    	String capString=b.getText().toString();
    	if(capString.equalsIgnoreCase(getString(R.string.string_com_1))){		//点单
        	//myCls.GetTableNo("请选择台号",Gravity.CENTER, MainActivity.this, handler,"string_com_1");
    		Intent intent=new Intent();
    		intent.putExtra("statusTitle", getString(R.string.string_cmdtitle_diandan));
    		intent.putExtra("cmdstring", "string_com_1");
    		intent.putExtra("cmd", "MAIN");
    		intent.setClass(this, TableStatusActivity.class);
    		this.startActivity(intent);    		
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_2))){	//崔单
        	//myCls.GetTableNo("请选择台号",Gravity.CENTER, MainActivity.this, handler,"string_com_2");
    		Intent intent=new Intent();
    		intent.putExtra("statusTitle", getString(R.string.string_cmdtitle_cuidan));
    		intent.putExtra("cmdstring", "string_com_2");
    		intent.putExtra("cmd", "MAIN");
    		intent.setClass(this, TableStatusActivity.class);
    		this.startActivity(intent);    		
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_3))){	//查单
        	//myCls.GetTableNo("请选择台号",Gravity.CENTER, MainActivity.this, handler,"string_com_3");
    		Intent intent=new Intent();
    		intent.putExtra("statusTitle", getString(R.string.string_cmdtitle_chadan));
    		intent.putExtra("cmdstring", "string_com_3");
    		intent.putExtra("cmd", "MAIN");
    		intent.setClass(this, TableStatusActivity.class);
    		this.startActivity(intent);    		
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_4))){	//换台
    		//myCls.GetTableNo("请选择原台号",Gravity.CENTER, MainActivity.this, handler,"string_com_4");
    		Intent intent=new Intent();
    		intent.putExtra("statusTitle", getString(R.string.string_cmdtitle_huantai));
    		intent.putExtra("cmdstring", "string_com_4");
    		intent.putExtra("cmd", "MAIN");
    		intent.setClass(this, TableStatusActivity.class);
    		this.startActivity(intent);    		
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_5))){	//查预订
			SearchOrderRunnable sor=new SearchOrderRunnable();
			Thread th=new Thread(sor);
			th.start();
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_6))){	//查会员
    		Intent intent=new Intent();
    		intent.putExtra("memberno", "");
    		intent.setClass(this, SearchOneMemberActivity.class);
    		this.startActivity(intent);
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_7))){	//查特价
			SearchSaleMenuRunnable ssmr=new SearchSaleMenuRunnable();
			Thread th=new Thread(ssmr);
			th.start();
    	}else if(capString.equalsIgnoreCase(getString(R.string.string_com_8))){	//查活动
			SearchActivityRunnable sar=new SearchActivityRunnable();
			Thread th=new Thread(sar);
			th.start();
    	}
    }
    
	public Handler handler=new Handler(){
 		@Override
		public void handleMessage(Message msg){
    		Bundle bd=msg.getData();
    		System.out.println(bd.getString("cmd")+",tableno="+bd.getString("tableno"));
 			if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_1")){			//点单
    			Intent intent = new Intent();
    			intent.putExtra("bundlePara", bd);
    			intent.setClass(MainActivity.this, DianDanActivity.class);
    			startActivity(intent);
  			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_2")){	//崔单
 				bd.putString("loginname", pubValue.GetLoginName());
 				HastenTableRunnable htr=new HastenTableRunnable();
 				htr.setDataBundle(bd); 				
 				Thread th=new Thread(htr);
 				th.start();
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_3")){	//查单
  				SearchTableRunnable str=new SearchTableRunnable();
 				str.setDataBundle(bd); 				
 				Thread th=new Thread(str);
 				th.start();
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_31")){	//查单
 				myCls.CancelToast();
 				Intent intent = new Intent();
    			intent.putExtra("bundlePara", bd);
    			intent.setClass(MainActivity.this, ChaDanActivity.class);
    			startActivity(intent);
			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_4")){	//换台
	    		//myCls.GetTableNo("原台号"+bd.getString("tableno")+"，请选择目标台号",Gravity.CENTER, MainActivity.this, handler,"string_com_41",bd.getString("tableno"));
	    		Intent intent=new Intent();
	    		String title=getString(R.string.string_cmdtitle_huantai_old);
	    		title+=bd.getString("tableno");
	    		title+=getString(R.string.string_cmdtitle_huantai_new);
	    		intent.putExtra("statusTitle", title);
	    		intent.putExtra("cmdstring", "string_com_41");
	    		intent.putExtra("cmd", "MAIN");
	    		intent.putExtra("oldtableno", bd.getString("tableno"));
	    		intent.setClass(MainActivity.this, TableStatusActivity.class);
	    		startActivity(intent);    		
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_41")){	//换台
 				System.out.println(bd.getString("oldtableno")+"->"+bd.getString("tableno"));
 				ChangeTableRunnable ctr=new ChangeTableRunnable();
 				ctr.setDataBundle(bd); 				
 				Thread th=new Thread(ctr);
 				th.start();
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_51")){	//查预订
 				myCls.CancelToast();
 				Intent intent = new Intent();
    			intent.putExtra("bundlePara", bd);
    			intent.setClass(MainActivity.this, YuDingListActivity.class);
    			startActivity(intent);
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_71")){	//查特价
 				myCls.CancelToast();
 				Intent intent = new Intent();
    			intent.putExtra("bundlePara", bd);
    			intent.setClass(MainActivity.this, TeJiaListActivity.class);
    			startActivity(intent);
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("string_com_81")){	//查活动
 				myCls.CancelToast();
 				Intent intent = new Intent();
    			intent.putExtra("bundlePara", bd);
    			intent.setClass(MainActivity.this, SaleListActivity.class);
    			startActivity(intent);
 			}else if(bd.getString("cmd").toString().equalsIgnoreCase("change_password")){	//修改密码
 				System.out.println("change password:"+bd.getString("oldpass")+"=>"+bd.getString("newpass"));
 				bd.putString("loginname", pubValue.GetLoginName());
 				UpdatePasswordRunnable upr=new UpdatePasswordRunnable();
 				upr.setDataBundle(bd); 				
 				Thread th=new Thread(upr);
 				th.start();
 			}
 		}
   	};
    
    class UpdatePasswordRunnable implements Runnable{  
 		private Bundle bd;
 		public void setDataBundle(Bundle b){
 			this.bd=b;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_chgpass_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/public.php?method=chgpassword&loginname="+bd.getString("loginname")+"&oldpass="+bd.getString("oldpass")+"&newpass="+bd.getString("newpass"));
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
    	    		
    	    		if(resultString.equals("FAILD")){
    	    			hintmsg=getString(R.string.string_chgpassword_oldpass_iswrong);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else if(resultString.equals("OK")){
    	    			hintmsg=getString(R.string.string_chgpassword_ok);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}    		
    	    	}else{
	    			hintmsg=getString(R.string.string_chgpassword_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_update_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    class ChangeTableRunnable implements Runnable{  
 		private Bundle bd;
 		public void setDataBundle(Bundle b){
 			this.bd=b;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_chgtable_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=chgtable&oldtableno="+bd.getString("oldtableno")+"&newtableno="+bd.getString("tableno"));
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
    	    		
    	    		if(resultString.equals("OK")){
    	    			hintmsg=getString(R.string.string_chgtable_ok);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else if(resultString.equals("NULL")){
    	    			hintmsg=bd.getString("oldtableno")+getString(R.string.string_chgtabl_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else if(resultString.equals("FULL")){
    	    			hintmsg=bd.getString("tableno")+getString(R.string.string_chgtabl_full);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}    		
    	    	}else{
	    			hintmsg=getString(R.string.string_chgtable_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_chgtable_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    class HastenTableRunnable implements Runnable{  
 		private Bundle bd;
 		public void setDataBundle(Bundle b){
 			this.bd=b;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_cuidan_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=hastentable&user="+bd.getString("loginname")+"&tableno="+bd.getString("tableno"));
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
    	    		
    	    		if(resultString.equals("OK")){
    	    			hintmsg=getString(R.string.string_hastentable_ok);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else if(resultString.equals("NULL")){
    	    			hintmsg=bd.getString("tableno")+getString(R.string.string_hastentable_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}    		
    	    	}else{
	    			hintmsg=getString(R.string.string_hastentable_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_hastentable_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    class SearchTableRunnable implements Runnable{  
 		private Bundle bd;
 		public void setDataBundle(Bundle b){
 			this.bd=b;
 		}
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_chadan_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=searchtable&tableno="+bd.getString("tableno"));
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
    	    			hintmsg=bd.getString("tableno")+getString(R.string.string_searchtable_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
    	    			message = Message.obtain();
    	    			bd.putString("xmlstring", resultString);
    	    			bd.putString("cmd", "string_com_31");
    	    			message.setData(bd);
    	    			handler.sendMessage(message);
    	    		}
    	    	}else{
	    			hintmsg=getString(R.string.string_searchtable_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_searchtable_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    class SearchSaleMenuRunnable implements Runnable{  
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_chasale_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=searchsale");
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
    	    			hintmsg=getString(R.string.string_searchsale_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
    	    			message = Message.obtain();
    	    			Bundle bd=new Bundle();
    	    			bd.putString("xmlstring", resultString);
    	    			bd.putString("cmd", "string_com_71");
    	    			message.setData(bd);
    	    			handler.sendMessage(message);
    	    		}
    	    	}else{
	    			hintmsg=getString(R.string.string_searchsale_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_searchsale_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
    
    class SearchOrderRunnable implements Runnable{  
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
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=searchorder");
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
    	    			Bundle bd=new Bundle();
    	    			bd.putString("xmlstring", resultString);
    	    			bd.putString("cmd", "string_com_51");
    	    			message.setData(bd);
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
    
    class SearchActivityRunnable implements Runnable{  
    	@Override  
    	public void run() {
 	    	Message message;
 	    	String hintmsg;
 	    	
			hintmsg=getString(R.string.string_searchactivity_wait);
			message = Message.obtain();
			message.obj=hintmsg;
			msghandler.sendMessage(message);
 			
    		Looper.prepare();
    		try{
    	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/menu.php?method=searchactivity");
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
    	    			hintmsg=getString(R.string.string_searchactivity_null);
    	    			message = Message.obtain();
    	    			message.obj=hintmsg;
    	    			msghandler.sendMessage(message);
    	    		}else{
    	    			message = Message.obtain();
    	    			Bundle bd=new Bundle();
    	    			bd.putString("xmlstring", resultString);
    	    			bd.putString("cmd", "string_com_81");
    	    			message.setData(bd);
    	    			handler.sendMessage(message);
    	    		}
    	    	}else{
	    			hintmsg=getString(R.string.string_searchactivity_faild);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			msghandler.sendMessage(message);
    	    	}
        	}catch(Exception e){
        		System.out.println(e.toString());
    			hintmsg=getString(R.string.string_searchactivity_check_net);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			msghandler.sendMessage(message);
        	}
    	}  
    };
   	
	public Handler msghandler=new Handler(){
 		public void handleMessage(Message msg){
    		String message=(String)msg.obj;
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, MainActivity.this);
    	}
   	};
   	
   	private boolean checkConfigFile(){
   		TableNoXML tnx=new TableNoXML(this);
   		if(!tnx.GetConfigFileIsExist()){
    		String message=getString(R.string.string_update_tips_table);
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, MainActivity.this);
   			return false;
   		}
   		SelectMenuXML smx=new SelectMenuXML(this);
   		if(!smx.GetConfigFileIsExist()){
    		String message=getString(R.string.string_update_tips_menu);
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, MainActivity.this);
  			return false;
   		}
   		return true;
   	}
   	
}
