package com.oldhawk.simbacafe;

import java.util.ArrayList;

import org.w3c.dom.Node;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SelectSubActivity extends Activity {
	private final MyClass myCls = new MyClass();
	//private PublicValue pubValue=new PublicValue();
	private SelectMenuXML smx=null;
	//private long delTime = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectsub);
		
		smx=new SelectMenuXML(this);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		initSubList();
	}
    
	private void initSubList(){
		Intent intent=this.getIntent();
		String selectidx=intent.getStringExtra("selectidx");
		System.out.println("selectidx:"+selectidx);
		
		LinearLayout sublist=(LinearLayout)findViewById(R.id.linearLayoutSubList);
		
		String[] subs=selectidx.split(",");
		for(int i=0;i<subs.length;i++){
			String onesub=subs[i];
			String snum=onesub.substring(onesub.length()-1, onesub.length());
			onesub=onesub.substring(1, onesub.length()-2);
			String[] sitems=onesub.split("[|]");
			System.out.println("onesub:"+onesub+",length:"+sitems.length+",snum:"+snum);
			
			TextView subtitle=new TextView(this);
			subtitle.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			subtitle.setText("下面菜品"+sitems.length+"选"+snum+":");
			subtitle.setTextSize(16);
			subtitle.setTextColor(Color.rgb(0, 170, 221));
			subtitle.setPadding(5,5,5,5);			
			sublist.addView(subtitle);
			
			ListView subview=new ListView(this);
			subview.setTag(snum);
			int t=dip2px(this, 42);
			subview.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,t*sitems.length));
			sublist.addView(subview);
			
			menuSubListAdapter adapter = new menuSubListAdapter(SelectSubActivity.this);  
	        subview.setAdapter(adapter); 
	        subview.setOnItemClickListener(new MenuItemClickListener());
			for(int j=0;j<sitems.length;j++){
				adapter.arrItems.add(sitems[j]);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	public static int dip2px (Context context, float dpValue ){
		final float scale = context. getResources(). getDisplayMetrics(). density;
		return(int)(dpValue * scale + 0.5f );
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent=getIntent();
			intent.putExtra("selectidx","");
			setResult(101, intent);
			finish();
		}
		//return super.onKeyDown(keyCode, event);
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

	private class MenuItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		}
	}
	
    private class menuSubListAdapter extends BaseAdapter {  
        
        private Context context;  
        private LayoutInflater inflater;  
        public ArrayList<String> arrItems;
        private boolean[] hasChecked;
        
        public menuSubListAdapter(Context con) {  
            super();  
            this.context = con;  
            inflater = LayoutInflater.from(context);  
            arrItems = new ArrayList<String>();
            //hasChecked = new boolean[getCount()];    
        }  
        @Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
            hasChecked = new boolean[getCount()];    
			super.notifyDataSetChanged();
		}
		@Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return arrItems.size();  
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
        private void checkedChange(int checkedID, boolean isChecked) {    
            hasChecked[checkedID] = isChecked;    
        }    
        public boolean hasChecked(int checkedID) {    
            return hasChecked[checkedID];    
        }
        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
            // TODO Auto-generated method stub  
			if(convertView == null){  
            	convertView = inflater.inflate(R.layout.view_selectsublist_item, null);  
            }
            String sIdx=arrItems.get(position);
            final int checkid=position;
            
            CheckBox cBox=(CheckBox)convertView.findViewById(R.id.checkbox_item);
			Node n=smx.GetNodeByMenuItemIdx(sIdx, SelectSubActivity.this);
            
            String title=n.getAttributes().getNamedItem("idx").getNodeValue();
            title+="：";
            title+=n.getAttributes().getNamedItem("name").getNodeValue();
            
            cBox.setText(title);
            
            cBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {    
                @Override   
                public void onCheckedChanged(CompoundButton buttonView,    
                        boolean isChecked) {    
                    //记录物品选中状态
                	/*if(isChecked){
                    	myCls.AlertToast("true", Gravity.BOTTOM, Toast.LENGTH_LONG, SelectSubActivity.this);
                	}else{
                		myCls.AlertToast("false", Gravity.BOTTOM, Toast.LENGTH_LONG, SelectSubActivity.this);
                	}*/
                		
                    checkedChange(checkid,isChecked);    
                }    
            });    
       
            return convertView;  
		}  
    }
    public void cancelSelectSubIdx(View v){
		try{
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
    }
    
    public void selectSubEnd(View v){
    	String sidx="";
    	LinearLayout sublist=(LinearLayout)findViewById(R.id.linearLayoutSubList);
    	
    	for(int j=1;j<sublist.getChildCount();j=j+2){
    		ListView lv=(ListView)sublist.getChildAt(j);
    		if(lv!=null){
    			menuSubListAdapter adapter = (menuSubListAdapter)lv.getAdapter();
    			Integer count=0;
		    	
				for(int i=0;i<adapter.arrItems.size();i++){
					if(adapter.hasChecked(i)){
						count+=1;
						if(sidx.equals("")){
							sidx+=adapter.arrItems.get(i);
						}else{
							sidx+=","+adapter.arrItems.get(i);
						}
					}
				}
				
				if(count!=Integer.parseInt(lv.getTag().toString())){
					String hintString=getString(R.string.selectsub_snums);
		        	myCls.AlertToast(hintString, Gravity.BOTTOM, Toast.LENGTH_LONG, SelectSubActivity.this);
		        	return;
				}
    		}
    	}
    	
		Intent intent=this.getIntent();
		intent.putExtra("selectidx",sidx);
		setResult(101, intent);
		finish();
    }
}
