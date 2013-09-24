package com.jivesoftware.spark.organization;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TreeXMLParse {

	//广播时,获取该组下所有用户
	public  CheckTreeNode xmlBroadCastUserAndGroup(String xmlDoc,String label,String name) {
        StringReader read = new StringReader(xmlDoc);
        InputSource source = new InputSource(read);
        SAXBuilder sb = new SAXBuilder();
    	//CheckTreeNode treeRoot;
    	CheckTreeNode treeRoot = new CheckTreeNode(name,label,"1");
       
        try {
            Document doc = sb.build(source);
            Element root = doc.getRootElement();
            //得到根元素所有子元素的集合
            makeOrgTree(root,treeRoot);
          

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
        return treeRoot;
    }
	
	
	public CheckTreeNode makeOrgTree(Element eleNode,CheckTreeNode currentNode)
	{
      List eleNodeList = eleNode.getChildren();
      if( eleNodeList!=null)
      { 
        for(int i=0;i < eleNodeList.size();i++)
        {
        	Element e = (Element) eleNodeList.get(i);//循环依次得到子元素
        	String name = e.getAttributeValue("name");
        	String type = e.getAttributeValue("type");
        	String guid = e.getAttributeValue("guid");
        	//ExpandTreeNode node = new ExpandTreeNode(name,guid,type);
        	CheckTreeNode node = new CheckTreeNode(name,guid,type);
        	currentNode.add(node);
        	makeOrgTree(e,node);
        }
        
      }
      return currentNode;
      
	}
	
	//////////////////////////////////////
	
	
	  Element userLabelElement ;
	  String userLabel ;
	  Element userNameElement;
	  String userName ;
	  UserNode userNode;
	  
	public  List xmlUser(String xmlDoc) {
        StringReader read = new StringReader(xmlDoc);
        InputSource source = new InputSource(read);
        SAXBuilder sb = new SAXBuilder();
        List<UserNode> result = new ArrayList<UserNode>();
   
        try {
            Document doc = sb.build(source);
            Element root = doc.getRootElement();
            System.out.println(root.getName());
            //得到根元素所有子元素的集合
            List node = root.getChildren();
            if( node.size()==2 )
            { 
              Element et = (Element) node.get(0); //<block name='user'>
              List rowList = et.getChildren();  // <row>的list
              for(int i=0;i<rowList.size();i++)
              {
            	  Element e = (Element) rowList.get(i);//循环依次得到子元素            
                  //row 里面的
                  List l = e.getChildren();
                  if(l.size()==2)
                  {
                	   userNode = new UserNode();
                	   userLabelElement = (Element) l.get(1);
                	   userLabel = userLabelElement.getValue();
                	   userNameElement = (Element) l.get(0);
                	   userName = userNameElement.getValue();
                	   userNode.setUserName(userName);
                	   userNode.setUserLabel(userLabel);
                  }
                  result.add(userNode);
              }      
              
            }
          

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("用户个数:"+result.size());
        return result;
    }
	
	Element groupNameElement;
	String groupName;
	Element groupLabelElement ;
	String groupLabel;
	OrgNode orgNode;
	public  List xmlOrg(String xmlDoc) {
        StringReader read = new StringReader(xmlDoc);
        InputSource source = new InputSource(read);
        SAXBuilder sb = new SAXBuilder();
        List<OrgNode> result = new ArrayList<OrgNode>();
        
        
        try {
            Document doc = sb.build(source);
            Element root = doc.getRootElement();
            System.out.println(root.getName());
            //得到根元素所有子元素的集合
            List node = root.getChildren();
            if( node.size()==2 )
            { 
              Element et = (Element) node.get(1); //<block name='user'>
              List rowList = et.getChildren();  // <row>的list
              for(int i=0;i<rowList.size();i++)
              {
            	  Element e = (Element) rowList.get(i);//循环依次得到子元素            
                  //row 里面的
                  List l = e.getChildren();
                  if(l.size()==2)
                  {
                	   orgNode = new OrgNode();
                	   groupNameElement = (Element) l.get(0);
                	   groupName = groupNameElement.getValue();
                	   groupLabelElement = (Element) l.get(1);
                	   groupLabel = groupLabelElement.getValue();
                	   orgNode.setGroupName(groupName);
                	   orgNode.setGroupLabel(groupLabel);
                  }
                  result.add(orgNode);
              }      
              
            }
          
           
           

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("组织机构个数:"+result.size());
        return result;
    }
	
	/*  public static List xmlElements(String xmlDoc) {
    StringReader read = new StringReader(xmlDoc);
       InputSource source = new InputSource(read);
       SAXBuilder sb = new SAXBuilder();
       ArrayList<Roster> result = new ArrayList<Roster>();
     //  ArrayList<String> groupList = new ArrayList<String>();
     //  groupList.add("未分组的好友");
       try {
           Document doc = sb.build(source);
           Element root = doc.getRootElement();
           System.out.println(root.getName());
           //得到根元素所有子元素的集合
           List node = root.getChildren();
           if( node.size()>0)
           { 
             Element et = (Element) node.get(0); //<query>
             List itemsList = et.getChildren();  // <item>的list
             for(int i=0;i<itemsList.size();i++){
           	  Element e = (Element) itemsList.get(i);//循环依次得到子元素
                 Roster r = new Roster();
                 String jid = e.getAttributeValue("jid");
                 String name = e.getAttributeValue("name");
                 r.setJid(jid);
                 r.setNickname(name);
                 //该好友的组
                 List l = e.getChildren();
                 if(l.size()>0)
                 {
               	  Element gElement = (Element) l.get(0);
               	  String groupName = gElement.getValue();
               	  r.setGroupName(groupName);
               //	  groupList.add(groupName);
                 }
                 result.add(r);
             }      
             
           }
         
          
          

       } catch (JDOMException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return result;
   }*/
    

}
