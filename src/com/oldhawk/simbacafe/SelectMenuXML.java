package com.oldhawk.simbacafe;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class SelectMenuXML extends Application {
	private String filename=Environment.getExternalStorageDirectory().getAbsolutePath()+"/simba/dataconfig/menuconfig.xml";
	private Document document;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	public boolean GetConfigFileIsExist(){
    	File file=new File(filename);
    	return file.exists();
	}
	
	public SelectMenuXML(Context con){
		try{
			File file=new File(filename);
    		FileInputStream inputStream = new FileInputStream(file);  
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	document = builder.parse(inputStream);
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}
	}
    
	public ArrayList<String> GetMenuSubTypeArray(String type,Context con){
    	ArrayList<String> al=new ArrayList<String>();
    	try{
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("type");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		String v=n.getAttributes().getNamedItem("name").getNodeValue();
	    		if(v.equalsIgnoreCase(type)){
	    			NodeList nl=n.getChildNodes();
	    			for(int j=0;j<nl.getLength();j++){
	    				if(nl.item(j).getNodeName().equals("subtype")){
	    					al.add(nl.item(j).getAttributes().getNamedItem("name").getNodeValue());
	    				}
	    			}
	    			break;
	    		}
	    	}
	    	return al;
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}
    	return null;
	}
    
	public ArrayList<Node> GetMenuSubTypeItemsArray(String subtype,Context con){
    	ArrayList<Node> al=new ArrayList<Node>();
     	try{
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("subtype");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		String v=n.getAttributes().getNamedItem("name").getNodeValue();
	    		if(v.equalsIgnoreCase(subtype)){
	    			NodeList nl=n.getChildNodes();
	    			for(int j=0;j<nl.getLength();j++){
	    				if(nl.item(j).getNodeName().equals("item")){
	    					if(nl.item(j).getAttributes().getNamedItem("showinphone").getNodeValue().equals("1")){
	    						al.add(nl.item(j));
	    					}
	    				}
	    			}
	    			break;
	    		}
	    	}
	    	return al;
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}
    	return null;
	}
	
    public ArrayList<String> GetMenuTypeArray(Context con){
    	ArrayList<String> al=new ArrayList<String>();
    	try{
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("type");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		String v=n.getAttributes().getNamedItem("name").getNodeValue();
	    		al.add(v);
	    	}
	    	return al;
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}
    	return null;
    }
    
    public Node GetNodeByMenuItemIdx(String idx,Context con){
     	try{
	    	Element root = document.getDocumentElement();
	    	NodeList nsList=root.getElementsByTagName("item");
	    	for(int i=0;i<nsList.getLength();i++){
	    		Node n=nsList.item(i);
	    		String v=n.getAttributes().getNamedItem("idx").getNodeValue();
	    		if(v.equalsIgnoreCase(idx)){
	    			return n;
	    		}
	    	}
    	}catch(Exception ex){
    		System.out.println(ex.toString());
    	}
   	
    	return null;
    }
};