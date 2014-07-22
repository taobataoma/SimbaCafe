package com.oldhawk.simbacafe;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChaDanActivity extends Activity {
	private ListView listview;
	private chadanListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chadan);
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		Intent intent = getIntent();
		Bundle bd=intent.getBundleExtra("bundlePara");
		
		TextView tablenoTextView=(TextView)findViewById(R.id.chadan_status);
		tablenoTextView.setText(bd.getString("tableno")+tablenoTextView.getText());
		
		//System.out.println(bd.getString("xmlstring"));
		listview = (ListView)this.findViewById(R.id.chadan_listview);  
        adapter = new chadanListAdapter(this);  
        listview.setAdapter(adapter); 
		initAdapterArrayNode(bd.getString("xmlstring"));
	}

    @Override
	public boolean onNavigateUp() {
    	finish();
		return super.onNavigateUp();
	}
    
    private void initAdapterArrayNode(String xml){
		try{
			InputStream iss = new ByteArrayInputStream(xml.getBytes());  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(iss);
	    	
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	System.out.println(nsList.getLength()+"");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		adapter.arrNodes.add(n);
	    	}
	    	adapter.notifyDataSetChanged();
	    	updateTotalPrice();
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }
   	
   	private final void updateTotalPrice(){
        int total=0;
   		for(int i=0;i<adapter.arrNodes.size();i++){
   			Node n=adapter.arrNodes.get(i);
	   		int nums=Integer.parseInt(n.getAttributes().getNamedItem("nums").getNodeValue());
	        int price=Integer.parseInt(n.getAttributes().getNamedItem("price").getNodeValue());
	
	        total+=nums*price;
        }
		TextView tablenoTextView=(TextView)findViewById(R.id.chadan_status);
		tablenoTextView.setText(tablenoTextView.getText().toString()+total+getString(R.string.string_unit_money));
   	}
   	
    private class chadanListAdapter extends BaseAdapter {  
        
        private Context context;  
        private LayoutInflater inflater;  
        public ArrayList<Node> arrNodes;  
        public chadanListAdapter(Context con) {  
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
            
            String title=n.getAttributes().getNamedItem("menuidx").getNodeValue();
            title+="£º";
            title+=n.getAttributes().getNamedItem("itemname").getNodeValue();
           
            String desc=getString(R.string.select_commit_time);
            desc+="£º";
            desc+=n.getAttributes().getNamedItem("creationtime").getNodeValue();

            price=Integer.parseInt(n.getAttributes().getNamedItem("price").getNodeValue());
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
            
            if(n.getAttributes().getNamedItem("issale").getNodeValue().equals("1"))
            	tTitle.setTextColor(Color.rgb(200, 0, 0));
            else
            	tTitle.setTextColor(Color.rgb(0, 0, 0));
            if(n.getAttributes().getNamedItem("statusflag").getNodeValue().equals("1"))
            	tDesc.setTextColor(Color.rgb(0, 0, 200));
            else
            	tDesc.setTextColor(Color.rgb(153, 153, 153));
            
            return convertView;  
		}  
    }
}
