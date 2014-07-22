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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TeJiaListActivity extends Activity {
	private ListView listview;
	private tejiaListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tejialist);
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		Intent intent = getIntent();
		Bundle bd=intent.getBundleExtra("bundlePara");
		
		//System.out.println(bd.getString("xmlstring"));
		listview = (ListView)this.findViewById(R.id.tejia_listview);  
        adapter = new tejiaListAdapter(this);  
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
	    	//System.out.println(nsList.getLength()+"");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		adapter.arrNodes.add(n);
	    	}
	    	adapter.notifyDataSetChanged();
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }
   	
    private class tejiaListAdapter extends BaseAdapter {  
        
        private Context context;  
        private LayoutInflater inflater;  
        public ArrayList<Node> arrNodes;  
        public tejiaListAdapter(Context con) {  
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
            	convertView = inflater.inflate(R.layout.view_menufinallist_item, null);  
            }
            Node n=arrNodes.get(position);
            
            TextView tTitle=(TextView)convertView.findViewById(R.id.menushowlist_title);
            TextView tDesc=(TextView)convertView.findViewById(R.id.menushowlist_desc);
            TextView tTips=(TextView)convertView.findViewById(R.id.menushowlist_tips);
            
            String title=n.getAttributes().getNamedItem("menuidx").getNodeValue();
            title+="£º";
            title+=n.getAttributes().getNamedItem("itemname").getNodeValue();
            
            String desc=getString(R.string.select_price_title);
            desc+="£º";
            desc+=n.getAttributes().getNamedItem("saleprice").getNodeValue();
            desc+=getString(R.string.string_unit_money);
            desc+="/";
            desc+=n.getAttributes().getNamedItem("unit").getNodeValue();

            String tips=getString(R.string.select_price_oldprice);
            tips+="£º";
            tips+=n.getAttributes().getNamedItem("price").getNodeValue();
            tips+=getString(R.string.string_unit_money);
            tips+="/";
            tips+=n.getAttributes().getNamedItem("unit").getNodeValue();
            
            tTitle.setText(title);
            tDesc.setText(desc);
            tTips.setText(tips);
            
            return convertView;  
		}  
    }
}
