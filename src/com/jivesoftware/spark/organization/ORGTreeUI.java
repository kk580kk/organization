package com.jivesoftware.spark.organization;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

class ORGTreeUI extends BasicTreeUI {

	// 实现去除JTree的垂直线和水平线
	@Override
	protected void paintVerticalLine(Graphics g, JComponent c, int x, int top,int bottom) {
	}

	@Override
	protected void paintHorizontalLine(Graphics g, JComponent c, int y,int left, int right) {
	}

    @Override
    protected void paintExpandControl(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
    	
    	
    }
	
}