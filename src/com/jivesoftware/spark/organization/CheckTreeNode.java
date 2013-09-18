package com.jivesoftware.spark.organization;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jivesoftware.spark.component.CheckNode;


//结点数据类

public class CheckTreeNode extends CheckNode {
	private int selectionMode = 0;
	private boolean isSelected = false;
	private Icon icon = null;
	
	private String txt;  //机构或者用户的GUID
	private String type; 

	private boolean clickCheck;
	public boolean isClickCheck() {
		return clickCheck;
	}

	public void setClickCheck(boolean clickCheck) {
		this.clickCheck = clickCheck;
	}

	public CheckTreeNode(Object userObject,String txt,String type) {
		this(userObject, true, false,txt,type);
	}

	public CheckTreeNode(Object userObject, boolean allowsChildren,
			boolean isSelected,String txt,String type) {
		super(userObject, allowsChildren,isSelected);
		this.isSelected = isSelected;
		this.txt=txt;
		this.type=type;
	}

	public int getSelectionMode() {
		return selectionMode;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;

	      /*
	         Enumeration<CheckNode> nodeEnum = children.elements();
	         while (nodeEnum.hasMoreElements()) {
	             CheckNode node = nodeEnum.nextElement();
	              node.setSelected(isSelected);
	       }*/
	        
	}

	public boolean isSelected() {
		return isSelected;
	}
	
	public void setIcon(Icon icon){
		this.icon = icon;
	}
	
	public Icon getIcon(){
		return icon;
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