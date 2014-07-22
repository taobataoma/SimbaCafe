package com.oldhawk.simbacafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
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
import android.os.StrictMode;
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
public class LoginActivity extends Activity {
	private EditText usernameEditText;
	private EditText userpassEditText;
	private long exitTime = 0;
	private MyClass myCls=new MyClass();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);    //Activity的布局 

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
                .detectDiskReads()  
                .detectDiskWrites()  
                .detectNetwork()   // or .detectAll() for all detectable problems  
                .penaltyLog()  
                .build());  
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
                .detectLeakedSqlLiteObjects()  
                .detectLeakedClosableObjects()  
                .penaltyLog()  
                .penaltyDeath()  
                .build());  
        
        
        System.out.println("begin");
        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new exitButtonClickListener());
        Button loginButton = (Button)findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new loginButtonClickListener());

        usernameEditText = (EditText)findViewById(R.id.editTextUserName);
		userpassEditText = (EditText)findViewById(R.id.editTextUserPass);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
            if((System.currentTimeMillis()-exitTime) > 2000){  
            	myCls.AlertToast(getString(R.string.string_press_quit), Gravity.BOTTOM, Toast.LENGTH_LONG, LoginActivity.this);
                exitTime = System.currentTimeMillis();   
            } else {
            	ExitSystem();
            }
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.action_exit)
			ExitSystem();
    	if(item.getItemId()==R.id.action_about){
			MyClass cls=new MyClass();
			String hintmsg=getString(R.string.app_name2);
			hintmsg=hintmsg+"\n"+getString(R.string.string_copyright);
			hintmsg=hintmsg+"\n"+getString(R.string.string_version);
			String titleMsg=getString(R.string.action_about);
			cls.AlertDialog(titleMsg, hintmsg, Gravity.BOTTOM, LoginActivity.this);
    	}
    	if(item.getItemId()==R.id.action_updateconfig){
    		doConfigServer();
    	}
		return super.onOptionsItemSelected(item);
	}

    private void doConfigServer(){
		final AlertDialog.Builder altDB = new AlertDialog.Builder(this);
		altDB.setTitle(getString(R.string.config_title));
		altDB.setInverseBackgroundForced(true);
		// 创建内容显示区域view
		LayoutInflater inflater=LayoutInflater.from(this);    
	    final View tView=inflater.inflate(R.layout.dialog_configserver, null);
		EditText ets=(EditText)tView.findViewById(R.id.config_server);
		EditText etp=(EditText)tView.findViewById(R.id.config_port);
		IniFile ini=new IniFile();
		if(ini.GetConfigFileIsExist()){
			try{
				ini.IniReaderHasSection();
				ets.setText(ini.getValue("HTTPSERV", "serv", "http://192.168.0.1"));
				etp.setText(ini.getValue("HTTPSERV", "port", "80"));
			}catch(IOException e){
				e.printStackTrace();
			}
		}		
		
	    altDB.setPositiveButton(R.string.string_button_Cancel,
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
		altDB.setNegativeButton(R.string.string_button_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						EditText et=(EditText)tView.findViewById(R.id.config_server);
						EditText etn=(EditText)tView.findViewById(R.id.config_port);
                        
						try{
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
	                        field.setAccessible(true);
	                        if(et.getText().toString().trim().length()==0){  
	                        	myCls.AlertToast(getString(R.string.config_noserver), Gravity.TOP, Toast.LENGTH_LONG, LoginActivity.this);
	                            field.set(dialog,false);//不关闭
	                            return;
	                        }else if(etn.getText().toString().trim().length()==0){  
	                        	myCls.AlertToast(getString(R.string.config_noport), Gravity.TOP, Toast.LENGTH_LONG, LoginActivity.this);
	                            field.set(dialog,false);//不关闭
	                            return;
	                        }else{
					    		Message message;
					    		message = Message.obtain();
					    		message.arg1=2;
					    		Bundle bd=new Bundle();
					    		bd.putString("cmd", "change_config");
					    		bd.putString("server", et.getText().toString().trim());
					    		bd.putString("port",etn.getText().toString().trim());
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

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
    class exitButtonClickListener implements View.OnClickListener{
    	@Override
		public void onClick(View v){
    		ExitSystem();
    	}
    }
    
    private void ExitSystem(){
    	this.finish();
    	System.exit(0);
    }
    
    class loginButtonClickListener implements View.OnClickListener{
    	
    	@Override
    	public void onClick(View v){
    		IniFile ini=new IniFile();
    		if(!ini.GetConfigFileIsExist()){
        		String message=getString(R.string.string_update_config_tip);
        		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, LoginActivity.this);
    			return;
    		}
    		
    		String username=usernameEditText.getText().toString();
    		String userpass=userpassEditText.getText().toString();
    		
    		String hintmsg="";
    		if (username.trim().length()==0){
    			hintmsg=getString(R.string.string_inputusername);
    		}else if (userpass.trim().length()==0){
    			hintmsg=getString(R.string.string_inputuserpass);
    		}
    		if(hintmsg.trim().length()>0){	//没有输入用户名或密码，弹出提示要求输入 
    			myCls.AlertToast(hintmsg, Gravity.BOTTOM, Toast.LENGTH_LONG, LoginActivity.this);
    			//String titleMsg=getString(R.string.string_alertmsgLabel);
    			//cls.AlertDialog(titleMsg, hintmsg, Gravity.CENTER,MainActivity.this);

    			//cls.AlertMsg(hintmsg, MainActivity.this);
    			
    			//Intent alertIntent = new Intent();
    			//alertIntent.putExtra("hintmsg", hintmsg);
    			//System.out.println("hitmsg="+hintmsg);
    			//alertIntent.setClass(MainActivity.this, AlertMsg.class);
    			//startActivity(alertIntent);
    		}else{							//输入了用户名及密码，开始登陆
    			//doLogin(username.trim(),userpass.trim());
    			// 启动线程执行登录任务  
        		myCls.AlertToast(getString(R.string.string_logining), Gravity.BOTTOM, Toast.LENGTH_LONG, LoginActivity.this);
    			new Thread(loginRunnable).start();
    		}
    	}
    }
    
    Runnable loginRunnable = new Runnable(){  
 		@Override  
    	public void run() {
    		Looper.prepare();
    		String u=usernameEditText.getText().toString();
    		String p=userpassEditText.getText().toString();
    		System.out.println("begin to Login");
    		doLogin(u, p);  
    	}  
    };
    
	public Handler handler=new Handler(){
 		public void handleMessage(Message msg){
 			if(msg.arg1==1){
    			Intent intent = new Intent();
    			String exs=msg.obj.toString();
    			String[] s=exs.split(" ");
    			intent.putExtra("username", s[0]);
    			intent.putExtra("useraccess", Integer.parseInt(s[1]));
    			String u=usernameEditText.getText().toString();
    			intent.putExtra("loginname", u);
    			intent.setClass(LoginActivity.this, MainActivity.class);
    			startActivity(intent);
    			
 				myCls.CancelToast();
 				finish();
 				return;
 			}else if(msg.arg1==2){
 				try{
 		            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
 		            	File appHome = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"//simba//"); 
 		            	File subPath = new File(appHome+"//dataconfig//"); 
 		            	appHome.mkdir(); 
 		            	subPath.mkdir();
 		            	
		 				Bundle bd=msg.getData();
		 				String serv=bd.getString("server");
		 				String port=bd.getString("port");
		 				
		 				IniFile ini=new IniFile();
		 				//ini.IniReaderHasSection();
		 				ini.setValue("HTTPSERV", "serv", serv);
		 				ini.setValue("HTTPSERV", "port", port);
		 				ini.flush();
 		            }
	 				return;
 				}catch(IOException e){
 					e.printStackTrace();
 					return;
 				}
 			}
    		String message=(String)msg.obj;
    		myCls.AlertToast(message, Gravity.BOTTOM, Toast.LENGTH_LONG, LoginActivity.this);
    	}
   	};

    private void doLogin(String u, String p){
    	Message message;
    	String hintmsg;
    	//===============================
		/*message = Message.obtain();
		message.arg1=1;
		message.obj=u;
		handler.sendMessage(message);
		*/
		//===============================
		try{
	    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	    	URL url = new URL(myCls.getHttpServerUrl()+"/simba/public.php?method=login&user="+u+"&pass="+p);
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
	    			hintmsg=getString(R.string.string_login_faild_up);
	    			message = Message.obtain();
	    			message.obj=hintmsg;
	    			handler.sendMessage(message);
	    		}else if(resultString.startsWith("OK ")){
	    			message = Message.obtain();
	    			message.arg1=1;
	    			message.obj=resultString.substring(3);
	    			handler.sendMessage(message);
	    		}    		
	    	}else{
    			hintmsg=getString(R.string.string_login_faild_admin);
    			message = Message.obtain();
    			message.obj=hintmsg;
    			handler.sendMessage(message);
	    	}
    	}catch(SocketTimeoutException e){
    		System.out.println(e.toString());
			hintmsg=getString(R.string.string_login_nofar);
			message = Message.obtain();
			message.obj=hintmsg;
			handler.sendMessage(message);
    	}catch(ConnectException ex){
    		System.out.println(ex.toString());
			hintmsg=getString(R.string.string_login_check_net);
			message = Message.obtain();
			message.obj=hintmsg;
			handler.sendMessage(message);
    	}catch(Exception e){
    		System.out.println(e.toString());
			hintmsg=getString(R.string.string_login_faild_admin);
			message = Message.obtain();
			message.obj=hintmsg;
			handler.sendMessage(message);
    	}
    	

    	/*
    	//服务器位置
    	URL url = new URL("http://192.168.1.105");
    	//打开一个服务器的连接
    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    	//*****接下来是设置连接的属性等等*****
    	//设置超时
    	conn.setConnectTimeout(5000);
    	//设置 URL 请求的方法，GET POST HEAD OPTIONS PUT DELETE TRACE
    	//以上方法之一是合法的，具体取决于协议的限制。默认方法为 GET
    	conn.setRequestMethod("POST");
    	//设置发送字符串的编码格式等
    	conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
    	//请求连接属性设置完成后就可以通过IO流的方式发送数据给服务器了，
    	//可以用DataOutputStream或者OutputStreamWriter输出。
    	OutputStream os = conn.getOutputStream();
    	DataOutputStream dos = new DataOutputStream(os);
    	os.write(xml.getBytes("UTF-8")); //将xml字符串xml转化为字节格式，编码是UTF-8
    	dos.flush(); //清空此数据输出流
    	dos.close(); //关闭此输出流并释放与此流有关的所有系统资源。
    	*/
    	/*
    	try {
			Class.forName("com.mysql.jdbc.Driver");
			String urlString = "jdbc:mysql://192.168.1.105:3306/world?autoReconnect=true&failOverReadOnly=false";//&maxReconnects=5","simba","welcom123");
			String userString = "simba";
			String passString = "welcom123";
			System.out.println("connect to -> "+urlString);
			mysqlConn =  DriverManager.getConnection(urlString,userString,passString);
			System.out.println("connect success");
		} catch (SQLException ex) {
			System.out.println(ex.toString());
			//System.out.println("SQLException: " + ex.getMessage());
		    //System.out.println("SQLState: " + ex.getSQLState());
		    //System.out.println("VendorError: " + ex.getErrorCode());
		    
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: com.mysql.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("ERROR");
		}*/
		
    }
}
