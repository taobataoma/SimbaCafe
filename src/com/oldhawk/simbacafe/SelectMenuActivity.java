package com.oldhawk.simbacafe;

//import android.app.ActionBar;
import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class SelectMenuActivity extends Activity {
    private ListView listview;  
    private menuShowListAdapter adapter;  
	private SelectMenuXML smx=null;
	private String selectedIdx="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectmenu);
		
		smx=new SelectMenuXML(this);
		
        listview = (ListView)SelectMenuActivity.this.findViewById(R.id.select_listview_menulist);  
        adapter = new menuShowListAdapter(SelectMenuActivity.this);  
        listview.setAdapter(adapter);  
        listview.setOnItemClickListener(new MenuItemClickListener());

        EditText menuidx=(EditText)findViewById(R.id.select_edittext_menuidx);
        menuidx.addTextChangedListener(new editTextChangeListener());
        
        showActionBar();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==101){
			if(data.getStringExtra("selectidx").length()==0){
				ResetAllFields(null);
			}else{
				selectedIdx=data.getStringExtra("selectidx");
				System.out.println("selectedIdx="+selectedIdx);
				//EditText usertips=(EditText)findViewById(R.id.select_edittext_usertips);
				//usertips.setText(selectedIdx);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class editTextChangeListener implements TextWatcher{

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			Node n=smx.GetNodeByMenuItemIdx(s.toString(), SelectMenuActivity.this);

			TextView menuname=(TextView)findViewById(R.id.select_textview_menuname);
			TextView menunums=(TextView)findViewById(R.id.select_textview_menunums);
			EditText usertips=(EditText)findViewById(R.id.select_edittext_usertips);

			if(n!=null){
				menuname.setText(n.getAttributes().getNamedItem("name").getNodeValue());
				menunums.setText(1+"");
				usertips.setText("");
				
				if(n.getAttributes().getNamedItem("selectidx").getNodeValue().length()>1){
					Intent intent = new Intent();
					intent.putExtra("selectidx", n.getAttributes().getNamedItem("selectidx").getNodeValue());
					intent.putExtra("itemname", n.getAttributes().getNamedItem("name").getNodeValue());
					intent.setClass(SelectMenuActivity.this, SelectSubActivity.class);
					startActivityForResult(intent, 101);
					//startActivity(intent);
				}
			}else{
				menuname.setText("");
				menunums.setText("");
				usertips.setText("");
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}
		
	}
	private class MenuItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Node n=adapter.arrNodes.get(arg2);
			
			EditText menuidx=(EditText)findViewById(R.id.select_edittext_menuidx);
			menuidx.setText(n.getAttributes().getNamedItem("idx").getNodeValue());
		}
	}
	
	@Override
	public boolean onNavigateUp() {
		finish();
		return super.onNavigateUp();
	}
	
	private void showActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		// 添加菜单项监听器
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
			    //添加子类按钮
				final LinearLayout subtypelayout=(LinearLayout)findViewById(R.id.select_layout_subtype);
				subtypelayout.removeAllViewsInLayout();
				
				final ArrayList<String> stn=smx.GetMenuSubTypeArray(tab.getText().toString(), SelectMenuActivity.this);
				for( int i=0; i<stn.size(); i++){
					Button b=new Button(SelectMenuActivity.this);
				    if(i==0){
				    	b.setBackgroundResource(R.drawable.menusubtypecheckedbackground);
				    	initMenuListItem(stn.get(i));
				    }
				    else{
				    	b.setBackgroundResource(R.drawable.layoutrightborderbackground);
				    }

				    b.setText(stn.get(i));
				    LinearLayout.LayoutParams paButton = new LinearLayout.LayoutParams(180,68);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				    b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				    b.setPadding(15, 5, 5, 5);
				    b.setTextSize(16);
				    b.setLayoutParams(paButton);
				    b.setOnClickListener(new View.OnClickListener() {						
						@Override
						public void onClick(View v) {
							Button b=(Button)v;
							System.out.println(b.getText().toString());
							b.setBackgroundResource(R.drawable.menusubtypecheckedbackground);
						    LinearLayout.LayoutParams paButton = new LinearLayout.LayoutParams(180,68);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						    b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
						    b.setPadding(15, 5, 5, 5);
						    b.setTextSize(16);
						    b.setLayoutParams(paButton);
						    
							for(int i=0;i<subtypelayout.getChildCount();i++){
								Button b1=(Button)subtypelayout.getChildAt(i);
								if(b1!=b){
									b1.setBackgroundResource(R.drawable.layoutrightborderbackground);
								    LinearLayout.LayoutParams plButton = new LinearLayout.LayoutParams(180,68);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								    b1.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
								    b1.setPadding(15, 5, 5, 5);
								    b1.setTextSize(16);
								    b1.setLayoutParams(plButton);
								}
							}
							
							initMenuListItem(b.getText().toString());
						}
					});
				    subtypelayout.addView(b);
			    }
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}

			@Override
			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}
		};
		
		//添加菜品类型
		ArrayList<String> al=smx.GetMenuTypeArray(this);
		for(int i=0;i<al.size();i++){
			ActionBar.Tab tab=actionBar.newTab();
			tab.setText(al.get(i));
			tab.setTabListener(tabListener);
			actionBar.addTab(tab);
	    }
	}
	
	private void initMenuListItem(String subtype){
		//添加submenu 子项
        adapter.arrNodes.clear();
		ArrayList<Node> tn=smx.GetMenuSubTypeItemsArray(subtype, SelectMenuActivity.this);
		for( int i=0; i<tn.size(); i++){
		    Node n=tn.get(i);
            adapter.arrNodes.add(n);  
		}  
        adapter.notifyDataSetChanged();  
		
	}
	
    private class menuShowListAdapter extends BaseAdapter {  
        
        private Context context;  
        private LayoutInflater inflater;  
        public ArrayList<Node> arrNodes;  
        public menuShowListAdapter(Context con) {  
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
            	convertView = inflater.inflate(R.layout.view_menushowlist_item, null);  
            }
            Node n=arrNodes.get(position);
            
            TextView tTitle=(TextView)convertView.findViewById(R.id.menushowlist_title);
            TextView tDesc=(TextView)convertView.findViewById(R.id.menushowlist_desc);
            
            String title=n.getAttributes().getNamedItem("idx").getNodeValue();
            title+="：";
            title+=n.getAttributes().getNamedItem("name").getNodeValue();
            tTitle.setText(title);
            
            String desc=getString(R.string.select_price_title);
            desc+="：";
            if(n.getAttributes().getNamedItem("saleprice").getNodeValue().equals("0")){
            	desc+=n.getAttributes().getNamedItem("price").getNodeValue();
            	desc+=getString(R.string.string_unit_money);
            	desc+="/";
            	desc+=n.getAttributes().getNamedItem("unit").getNodeValue();
            }else{
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
            tDesc.setText(desc);
            
            return convertView;  
		}  
    }
    
    public void AddOneNums(View v){
    	TextView menunums=(TextView)findViewById(R.id.select_textview_menunums);
    	String si=menunums.getText().toString();
    	if(si.length()>0){
    		int i=Integer.parseInt(si);
    		if(v.getId()==R.id.select_button_jia){
    			i++;
    			menunums.setText(i+"");
    		}else if(v.getId()==R.id.select_button_jian){
    			i--;
    			if(i==0)	i++;
    			menunums.setText(i+"");
    		}
    	}
    }
    
    public void ResetAllFields(View v){
    	EditText menuidx=(EditText)findViewById(R.id.select_edittext_menuidx);
		TextView menuname=(TextView)findViewById(R.id.select_textview_menuname);
		TextView menunums=(TextView)findViewById(R.id.select_textview_menunums);
		EditText usertips=(EditText)findViewById(R.id.select_edittext_usertips);
		menuidx.setText("");
		menuname.setText("");
		menunums.setText("");
		usertips.setText("");
    }
    
    public void AddMenuItemToFinalList(View v){
    	EditText menuidx=(EditText)findViewById(R.id.select_edittext_menuidx);
    	Node n=smx.GetNodeByMenuItemIdx(menuidx.getText().toString(), SelectMenuActivity.this);
    	if(n!=null){
    		TextView menunums=(TextView)findViewById(R.id.select_textview_menunums);
    		EditText usertips=(EditText)findViewById(R.id.select_edittext_usertips);
    		if (n.getAttributes().getNamedItem("nums")==null){
    			Attr attNums=n.getOwnerDocument().createAttribute("nums");
    			n.getAttributes().setNamedItem(attNums);
    		}
    		if (n.getAttributes().getNamedItem("usertips")==null){
    			Attr attTips=n.getOwnerDocument().createAttribute("usertips");
    			n.getAttributes().setNamedItem(attTips);
    		}
    		n.getAttributes().getNamedItem("nums").setNodeValue(menunums.getText().toString());
    		n.getAttributes().getNamedItem("usertips").setNodeValue(usertips.getText().toString());
    		
    		if(selectedIdx.length()>0){
    			selectedIdx=n.getAttributes().getNamedItem("subitemidx").getNodeValue()+","+selectedIdx;
    			n.getAttributes().getNamedItem("subitemidx").setNodeValue(selectedIdx);    			
    		}
    		
    		DianDanActivity ac=DianDanActivity.staticDianDanActivity;
     		
    		Message message;
    		message = Message.obtain();
    		message.arg1 = 1;
    		message.obj = n;
    		ac.handler.sendMessage(message); 
    		
    		finish();
    	}
    }
}
