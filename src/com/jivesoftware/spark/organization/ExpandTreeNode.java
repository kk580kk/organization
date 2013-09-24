package com.jivesoftware.spark.organization;

import javax.swing.tree.DefaultMutableTreeNode;


public class ExpandTreeNode extends DefaultMutableTreeNode {
	  private String txt;  //机构或者用户的GUID
	  private String type; 

	  public ExpandTreeNode(String name,String txt,String type)
	  {
	   super(name);
	   this.txt=txt;
	   this.type=type;
	  }  
	 
	 public void setText(String txt)
	 {
	  this.txt=txt;
	 }
	 
	 public String getText()
	 {
	  return txt;
	 }     
	 
	 public void setType(String type)
	 {
	  this.type=type;
	 }
	 
	 public String getType()
	 {
	  return type;
	 }     
     
}

