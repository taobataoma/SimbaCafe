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
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SaleListActivity extends Activity {
	private ArrayList<View> arrayPageViews;
	private ArrayList<Button> arrayPointerList;
	private ViewPager viewPager;
	private GuidePageAdapter adapter;
	private LinearLayout PointerLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salelist);
        ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		arrayPageViews=new ArrayList<View>();
		arrayPointerList=new ArrayList<Button>();
		
		Intent intent = getIntent();
		Bundle bd=intent.getBundleExtra("bundlePara");
		
		//System.out.println(bd.getString("xmlstring"));
		PointerLayout=(LinearLayout)this.findViewById(R.id.pointLayout);
		viewPager = (ViewPager)this.findViewById(R.id.salePages);
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		initAdapterArray(bd.getString("xmlstring"));
		adapter = new GuidePageAdapter();  
		viewPager.setAdapter(adapter); 
    }
	
    private void initAdapterArray(String xml){
		try{
			InputStream iss = new ByteArrayInputStream(xml.getBytes());  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(iss);
	    	
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	//System.out.println(nsList.getLength()+"");
	    	LayoutInflater inflater = getLayoutInflater();
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		//adapter.arrNodes.add(n);
	    		//add a view
	    		View v=inflater.inflate(R.layout.view_saleactivityitem, null);
	    		TextView tv=(TextView)v.findViewById(R.id.item_title);
	    		tv.setText(n.getAttributes().getNamedItem("title").getNodeValue());
	    		TextView tv1=(TextView)v.findViewById(R.id.item_content);
	    		String content=n.getAttributes().getNamedItem("content").getNodeValue();
	    		String startt=getString(R.string.salelist_starttime);
	    		startt+=n.getAttributes().getNamedItem("starttime").getNodeValue();
	    		String endt=getString(R.string.salelist_endtime);
	    		endt+=n.getAttributes().getNamedItem("endtime").getNodeValue();
	    		tv1.setText(startt+"\n"+endt+"\n\n"+content);
	    		arrayPageViews.add(v);
	    		//add a pointer
	    		Button b=new Button(this);
			    LinearLayout.LayoutParams paButton = new LinearLayout.LayoutParams(20,20);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			    paButton.leftMargin=3;
			    paButton.rightMargin=3;
			    b.setLayoutParams(paButton);
			    if(i==0){
			    	b.setBackgroundResource(R.drawable.layoutpointerselectedbackground);
			    }else {
			    	b.setBackgroundResource(R.drawable.layoutpointernormalbackground);
				}
	    		arrayPointerList.add(b);
	    		PointerLayout.addView(b);
	    	}
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}    	
    }
    
    class GuidePageAdapter extends PagerAdapter{
        //销毁position位置的界面
        @Override
        public void destroyItem(View v, int position, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager)v).removeView(arrayPageViews.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }
        
        //获取当前窗体界面数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayPageViews.size();
        }

        //初始化position位置的界面
        @Override
        public Object instantiateItem(View v, int position) {
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(arrayPageViews.get(position));
            return arrayPageViews.get(position);  
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }



        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
    @Override
	public boolean onNavigateUp() {
    	finish();
		return super.onNavigateUp();
	}

    class GuidePageChangeListener implements OnPageChangeListener{

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<arrayPointerList.size();i++){
            	Button b=arrayPointerList.get(i);
            	if(i==position){
            		b.setBackgroundResource(R.drawable.layoutpointerselectedbackground);
            	}else {
            		b.setBackgroundResource(R.drawable.layoutpointernormalbackground);
				}
            }
        }
    }
    
}
