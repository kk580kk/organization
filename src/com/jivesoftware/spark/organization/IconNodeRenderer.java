package com.jivesoftware.spark.organization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jivesoftware.resource.SparkRes;

import com.jivesoftware.spark.organization.ExpandTreeNode;
public class IconNodeRenderer extends DefaultTreeCellRenderer//继承该类 
{   
	 /** 
     * ID 
     */  
    private static final long   serialVersionUID    = 1L;  
  
    /** 
     * 重写父类DefaultTreeCellRenderer的方法 
     */  
    @Override  
    public Component getTreeCellRendererComponent(JTree tree, Object value,  
            boolean sel, boolean expanded, boolean leaf, int row,  
            boolean hasFocus)  
    {  
  
        //执行父类原型操作  
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,  
                row, hasFocus);  
  
        setText(value.toString());  
          
        if (sel)  
        {  
            setForeground(getTextSelectionColor());  
        }  
        else  
        {  
            setForeground(getTextNonSelectionColor());  
        }  
          
        //得到每个节点的TreeNode  
        ExpandTreeNode node = (ExpandTreeNode) value;  
          
        //得到每个节点的text  
        String str = node.getType();       
     
        String url ="src/plugins/examples/resources/images";
       
          
        //判断是哪个文本的节点设置对应的值（这里如果节点传入的是一个实体,则可以根据实体里面的一个类型属性来显示对应的图标）  
        if (str!=null &&str.equals("1"))  
        {  
        	 this.setIcon(SparkRes.getImageIcon("ORG_MESSAGE")); 
        } 
        if (str!=null &&str.equals("2"))  
        {  
        	 this.setIcon(SparkRes.getImageIcon("USER_MESSAGE")); 
        }
        
        this.setFont(new Font("宋体",Font.PLAIN,13));
        this.setBackground(new Color(240,243,253));
        this.setBackgroundNonSelectionColor(new Color(240,243,253));
        this.setBackgroundSelectionColor(Color.BLACK);

  
        return this;  
    }  
}


