package com.jivesoftware.spark.organization;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jivesoftware.PasswordMD5;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smackx.LastActivityManager;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.util.GraphicUtils;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.sparkimpl.profile.VCardManager;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;


public class TreePanel extends JPanel {

    private JPanel xmainTreePanel = new JPanel();
    private JScrollPane treeScrollPane;
    private JScrollPane treeScroller;
    private JPanel panel;
    private ExpandTreeNode selectedNode;
    private ExpandTreeNode newNode;
    private TreeNode[] nodes;
    private TreePath path;
    JTree tree;

    private HttpURLConnection connect;
    private URL url;
    private InputStream is;
    private BufferedReader br;
    private String strLine;
    private String result;
    // 上面JTree对象对应的model
    DefaultTreeModel model;
    //  定义几个初始节点
    ExpandTreeNode root = new ExpandTreeNode("宝信软件", "00000000", "1");


    ChatManager chatManager;
    String serviceName;
    String xmlNodes;
    String xmlChildrenNodes;
    TreeXMLParse parse;
    List<UserNode> userList;
    List<OrgNode> orgList;
    UserNode uNode;
    OrgNode oNode;
    ExpandTreeNode oTreeNode;


    String urlStr;
    String name;
    String password;
    LocalPreferences localPreferences;
    String webUrl;
    String webPath;
    ConfigurationUtil config;

    JPopupMenu popup = new JPopupMenu();
    final PopupMenu p = new PopupMenu();
    final MenuItem m1;

    public TreePanel() {

        // System.out.println("!@#$%^:"+ SparkRes.getString("ORG"));
        //组织机构树根节点
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        new File(file, "spark.properties");
        try {
            file = new File(Spark.getSparkUserHome(), "/spark.properties");
            config = new ConfigurationUtil(file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        webUrl = config.getValue("weburl");
        webPath = SparkRes.getString("ORG");


        //	 String pathString = "http://localhost:8080/efmpx/EFMPX/IM/IM02.jsp?serviceName=JQueryOrgTree&top=top";
        String pathString = webUrl + webPath;


        String authenType = "CodedPwd";
        PasswordMD5 oPasswordMD5 = PasswordMD5.getInstance();
        name = oPasswordMD5.getAdminName();
        password = oPasswordMD5.passwordMD5;

        SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar now = Calendar.getInstance();
        String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());

        String cre = PasswordMD5.md5((minuteStr + password));

        //http://10.25.36.197:9080/efmpxNew/EFMPX/IM/IM02.jsp?serviceName=JQueryOrgTree&top=top&p_username=&p_password=&p_authen=CodedPwd
        urlStr = pathString + "&p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

        System.out.println("URL:" + urlStr);
        xmlNodes = getTreeChildren(urlStr);
        xmlNodes = xmlNodes.replaceAll("&", "&amp;");


        parse = new TreeXMLParse();

        orgList = parse.xmlOrg(xmlNodes);

        if (orgList != null) {
            for (int i = 0; i < orgList.size(); i++) {
                oNode = orgList.get(i);
                oTreeNode = new ExpandTreeNode(oNode.getGroupName(), oNode.getGroupLabel(), "1");
                root.add(oTreeNode);
            }
        }
        tree = new JTree(root);
        model = (DefaultTreeModel) tree.getModel();
        tree.setRootVisible(false);
        tree.setCellRenderer(new IconNodeRenderer());
        tree.setToggleClickCount(1);
        tree.putClientProperty("JTree.lineStyle", "None");
        tree.setBackground(new Color(240, 243, 253));
        UIManager.getDefaults().put("Tree.lineTypeDashed", true);
        tree.setUI(new ORGTreeUI());
        tree.setRowHeight(20);

        m1 = new MenuItem("cai");
        p.add(m1);
        tree.add(p);
        tree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    //	p.show(tree, e.getX(), e.getY());
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path == null) {
                        return;
                    }
                    tree.setSelectionPath(path);

                    selectedNode = (ExpandTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedNode == null) return;
                    ChatManager chatManager = SparkManager.getChatManager();//对话控制器
                    serviceName = SparkManager.getConnection().getServiceName();//域名

                    ContactItem item = new ContactItem(selectedNode.toString(), selectedNode.getText(), selectedNode.getText() + "@" + serviceName);
                    //组织的右键菜单
                    if (selectedNode.getType() != null && selectedNode.getType().equals("1")) {
                        showOrgPopup(tree, e, item, selectedNode);
                    }
                    //人员的右键菜单
                    if (selectedNode.getType() != null && selectedNode.getType().equals("2")) {
                        showPopup(tree, e, item);
                    }


                }
            }
        });

        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) return;
//			 		
                //获取选中节点
                selectedNode = (ExpandTreeNode) tree.getLastSelectedPathComponent();
                //如果节点为空，直接返回
                if (selectedNode == null) return;
                System.out.println("selectedNode的Label:" + selectedNode.getText());

                if (selectedNode.getType() != null && selectedNode.getType().equals("2")) {

                    //聊天窗口弹出,要进行判断是组织机构还是人员
                    ChatManager chatManager = SparkManager.getChatManager();//对话控制器
                    serviceName = SparkManager.getConnection().getServiceName();//域名
                    chatManager.activateChat(selectedNode.getText() + "@" + serviceName, selectedNode.toString());//弹出对话框
                    //如果是人员则返回
                    return;
                }


                //	机构,先清除节点的子节点
                if (!selectedNode.isLeaf())
                    deleteChildNode(selectedNode);

                //重新查询子节点
                //     String pathString = "http://localhost:8080/efmpx/EFMPX/IM/IM02.jsp?serviceName=JQueryOrgTree&labelParent="+selectedNode.getText();
                File file = new File(Spark.getSparkUserHome());
                if (!file.exists()) {
                    file.mkdirs();
                }
                new File(file, "spark.properties");
                try {
                    file = new File(Spark.getSparkUserHome(), "/spark.properties");
                    config = new ConfigurationUtil(file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                webUrl = config.getValue("weburl");
                webPath = SparkRes.getString("ORG_LEAF");
                String pathString = webUrl + webPath + selectedNode.getText();
                String authenType = "CodedPwd";
                PasswordMD5 oPasswordMD5 = PasswordMD5.getInstance();
                name = oPasswordMD5.getAdminName();
                password = oPasswordMD5.passwordMD5;

                SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
                Calendar now = Calendar.getInstance();
                String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());

                String cre = PasswordMD5.md5((minuteStr + password));

                urlStr = pathString + "&p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

                xmlNodes = getTreeChildren(urlStr);
                xmlNodes = xmlNodes.replaceAll("&", "&amp;");


                userList = parse.xmlUser(xmlNodes);
                //System.out.println(xmlChildrenNodes);
                orgList = parse.xmlOrg(xmlNodes);
                if (userList != null) {
                    for (UserNode anUserList : userList) {
                        uNode = anUserList;
                        oTreeNode = new ExpandTreeNode(uNode.getUserName(), uNode.getUserLabel(), "2");

                        selectedNode.add(oTreeNode);
                        nodes = model.getPathToRoot(oTreeNode);
                        path = new TreePath(nodes);
                        tree.scrollPathToVisible(path);
                        tree.updateUI();
                    }
                }
                if (orgList != null) {
                    for (OrgNode anOrgList : orgList) {
                        oNode = anOrgList;
                        oTreeNode = new ExpandTreeNode(oNode.getGroupName(), oNode.getGroupLabel(), "1");

                        selectedNode.add(oTreeNode);
                        nodes = model.getPathToRoot(oTreeNode);
                        path = new TreePath(nodes);
                        tree.scrollPathToVisible(path);
                        tree.updateUI();
                    }
                }


            }
        };
        tree.addMouseListener(ml);


        setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 243, 253));
        treeScroller = new JScrollPane(tree);// 滚动条
        treeScroller.setBorder(BorderFactory.createEmptyBorder());
        panel.add(treeScroller, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
                5, 5, 5), 0, 0));// 设置边界
        add(panel, BorderLayout.CENTER);
    }


    public void showOrgPopup(Component component, MouseEvent e, final ContactItem item, final ExpandTreeNode selectedNode) {
        if (item.getJID() == null) {
            return;
        }
        popup = new JPopupMenu();
        JMenuItem broadMenu;
        broadMenu = new JMenuItem("广播组织机构消息", SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));


        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent et) {
                BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
                final JFrame frame = new JFrame("广播组织机构消息");
                JButton broadbtn = new JButton("广播");
                frame.setLayout(new BorderLayout());
                frame.setIconImage(SparkManager.getApplicationImage().getImage());
                frame.setBackground(Color.WHITE);
                JPanel panel = new JPanel();
                panel.add(broadbtn);

                final JTextArea jtaBroad = new JTextArea(5, 20);


                File file = new File(Spark.getSparkUserHome());
                if (!file.exists()) {
                    file.mkdirs();
                }
                new File(file, "spark.properties");
                try {
                    file = new File(Spark.getSparkUserHome(), "/spark.properties");
                    ConfigurationUtil config = new ConfigurationUtil(file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String webUrl = config.getValue("weburl");
                String webPath = SparkRes.getString("ORG_LEAF");
                String pathString = webUrl + webPath + selectedNode.getText();
                String authenType = "CodedPwd";
                PasswordMD5 oPasswordMD5 = PasswordMD5.getInstance();
                String name = oPasswordMD5.getAdminName();
                String password = oPasswordMD5.passwordMD5;

                SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
                Calendar now = Calendar.getInstance();
                String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());
                String cre = PasswordMD5.md5((minuteStr + password));
                String urlStr = pathString + "&p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType + "&broad=broad";
                String xmlNodes = getTreeChildren(urlStr);

                TreeXMLParse parse = new TreeXMLParse();
                final CheckTreeNode root = parse.xmlBroadCastUserAndGroup(xmlNodes, selectedNode.getText(), selectedNode.toString());
                final JTree tree = new JTree(root);
                tree.setCellRenderer(new CheckTreeRenderer());
                tree.setRowHeight(25);
                tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                tree.setToggleClickCount(1000);
                tree.putClientProperty("JTree.lineStyle", "Angled");
                tree.addMouseListener(new NodeSelectionListener(tree));
                expandTree(tree);

				 
				 
/*				 final JTree tree = new JTree(root);
                  DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				 // tree.setCellRenderer(new IconNodeRenderer());  
				  tree.setToggleClickCount(1);
				  tree.putClientProperty("JTree.lineStyle" , "None");
		          tree.setBackground(new Color(240,243,253));
		          UIManager.getDefaults().put("Tree.lineTypeDashed", true);
				  tree.setUI(new ORGTreeUI());
				  tree.setRowHeight(20);
				  //展开树
				  expandTree(tree);
				  tree.setCellRenderer(new CheckTreeRenderer());
				  tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				  tree.setExpandsSelectedPaths(false);
				
				  tree.addMouseListener(new MouseAdapter(){
					  @Override
					public void mouseClicked(MouseEvent e) {//得到被选的节点
							int x = e.getX();
							int y = e.getY();
							int row = tree.getRowForLocation(x, y);
							
							TreePath path = tree.getPathForRow(row);
							//判断是否单击了节点
							if (path != null) {
								//取得被单击的节点
								CheckTreeNode node = (CheckTreeNode) path.getLastPathComponent();
								
								//System.out.println("checkClick:"+node.isClickCheck());
								
								boolean isSelected = !(node.isSelected());
								//设置被单击的节点CheckBox,使其状态与原来状态相反
								node.setSelected(isSelected);
								//如果节点是被选中,则展开子节点
								//if (isSelected) {
									tree.expandPath(path);
									//	tree.collapsePath(path);
								//}
								//如果被选节点不是叶节点,将其所有子节点设置为CheckBox状态设成与其相同的状态
//								if(!node.isLeaf()){
//									node.getNextNode();
//									Enumeration enu= node.children();
//									while (enu.hasMoreElements()) {
//										CheckTreeNode n = (CheckTreeNode) enu.nextElement();
//										n.setSelected(node.isSelected());
//										if(!n.isLeaf()){
//											Enumeration enuc= n.children();
//											while (enuc.hasMoreElements()) {
//												CheckTreeNode c = (CheckTreeNode) enuc.nextElement();
//												c.setSelected(node.isSelected());
//											}
//										}
//									}
//								} 
							
								selectNodeChildren(node,isSelected);
								
								//刷新树(这步是必须的,否则将看不到上面所有设置的效果);
								//((DefaultTreeModel) tree.getModel()).nodeChanged(node); 
								//tree.revalidate();
								//tree.repaint();	
							}
					   
					}//mouseclick
				  });
				  	*/
                broadbtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String content = jtaBroad.getText();
                        String serviceName = SparkManager.getConnection().getServiceName();//域名
                        List<CheckTreeNode> list = new ArrayList<CheckTreeNode>();
                        list = getCheckNode(root, list);
                        for (CheckTreeNode c : list) {
                            //System.out.println(c +"     " +c.getText());
                            XMPPConnection connection = SparkManager.getConnection();
                            final Message newMessage = new Message();
                            newMessage.setBody(content);
                            newMessage.setTo(c.getText() + "@" + serviceName);
                            newMessage.setType(Message.Type.chat);
                            connection.sendPacket(newMessage);
                        }

                        frame.dispose();

                    }
                });


                final JScrollPane broadPane = new JScrollPane(jtaBroad);
                broadPane.setBorder(BorderFactory.createTitledBorder("消息内容"));
                broadPane.getVerticalScrollBar().setBlockIncrement(200);
                broadPane.getVerticalScrollBar().setUnitIncrement(20);
                final JScrollPane treePane = new JScrollPane(tree);
                treePane.setBorder(BorderFactory.createTitledBorder(Res.getString("message.send.to.these.people")));
                treePane.getVerticalScrollBar().setBlockIncrement(200);
                treePane.getVerticalScrollBar().setUnitIncrement(20);


                //  创建一个水平排列的Box容器
                Box box = new Box(BoxLayout.X_AXIS);
                //将两个多行文本域放在Box容器中
                broadPane.setPreferredSize(new Dimension(200, 400));
                box.add(broadPane);
                box.add(treePane);
                //将按钮所在Panel、Box容器添加到Frame窗口中
                frame.add(panel, BorderLayout.SOUTH);
                frame.add(box, BorderLayout.CENTER);

                frame.pack();
                frame.setSize(800, 600);

                GraphicUtils.centerWindowOnScreen(frame);
                frame.setVisible(true);


            }
        };
        broadMenu.addActionListener(listener);
        popup.add(broadMenu);
        popup.addSeparator();
        popup.show(component, e.getX(), e.getY());

    }

    public List<CheckTreeNode> getCheckNode(CheckTreeNode root, List<CheckTreeNode> list) {
        if (root.isLeaf() && root.isSelected() && root.getType().equals("2"))
            list.add(root);

        if (!root.isLeaf()) {
            Enumeration enu = root.children();
            while (enu.hasMoreElements()) {
                CheckTreeNode n = (CheckTreeNode) enu.nextElement();
                if (n.isLeaf() && n.isSelected() && n.getType().equals("2"))
                    list.add(n);
                if (!n.isLeaf()) {
                    getCheckNode(n, list);
                }
            }
        }
        return list;
    }

    public void showPopup(Component component, MouseEvent e, final ContactItem item) {
        if (item.getJID() == null) {
            return;
        }
        popup = new JPopupMenu();
        JMenuItem chatMenu;
        chatMenu = new JMenuItem(Res.getString("menuitem.start.a.chat"), SparkRes.getImageIcon(SparkRes.SMALL_MESSAGE_IMAGE));


        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent et) {
                ChatManager chatManager = SparkManager.getChatManager();//对话控制器
                serviceName = SparkManager.getConnection().getServiceName();//域名
                chatManager.activateChat(item.getJID(), item.getAlias());//弹出对话框

            }
        };
        chatMenu.addActionListener(listener);
        // Add Start Chat Menu
        popup.add(chatMenu);

        // Add Send File Action
        Action sendAction = new AbstractAction() {
            private static final long serialVersionUID = -7519717310558205566L;

            public void actionPerformed(ActionEvent actionEvent) {
                SparkManager.getTransferManager().sendFileTo(item);
            }
        };

        sendAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.DOCUMENT_16x16));
        sendAction.putValue(Action.NAME, Res.getString("menuitem.send.a.file"));

        if (item.getPresence() != null) {
            popup.add(sendAction);
        }

        popup.addSeparator();


        Action viewProfile = new AbstractAction() {
            private static final long serialVersionUID = -2562731455090634805L;

            public void actionPerformed(ActionEvent e) {
                VCardManager vcardSupport = SparkManager.getVCardManager();
                String jid = item.getJID();
                vcardSupport.viewProfile(jid, SparkManager.getWorkspace());
            }
        };
        viewProfile.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.PROFILE_IMAGE_16x16));
        viewProfile.putValue(Action.NAME, Res.getString("menuitem.view.profile"));

        popup.add(viewProfile);


        popup.addSeparator();

        Action lastActivityAction = new AbstractAction() {
            private static final long serialVersionUID = -4884230635430933060L;

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String client = "";
                    if (item.getPresence().getType() != Presence.Type.unavailable) {
                        client = item.getPresence().getFrom();
                        if ((client != null) && (client.lastIndexOf("/") != -1)) {
                            client = client.substring(client.lastIndexOf("/"));
                        } else client = "/";
                    }

                    LastActivity activity = LastActivityManager.getLastActivity(SparkManager.getConnection(), item.getJID() + client);
                    long idleTime = (activity.getIdleTime() * 1000);
                    String time = ModelUtil.getTimeFromLong(idleTime);
                    JOptionPane.showMessageDialog(null, Res.getString("message.idle.for", time), Res.getString("title.last.activity"), JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, Res.getString("message.unable.to.retrieve.last.activity", item.getJID()), Res.getString("title.error"), JOptionPane.ERROR_MESSAGE);
                }

            }
        };

        lastActivityAction.putValue(Action.NAME, Res.getString("menuitem.view.last.activity"));
        lastActivityAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER1_STOPWATCH));


        Action subscribeAction = new AbstractAction() {
            private static final long serialVersionUID = -7754905015338902300L;

            public void actionPerformed(ActionEvent e) {
                String jid = item.getJID();
                Presence response = new Presence(Presence.Type.subscribe);
                response.setTo(jid);

                SparkManager.getConnection().sendPacket(response);
            }
        };

        subscribeAction.putValue(Action.SMALL_ICON, SparkRes.getImageIcon(SparkRes.SMALL_USER1_INFORMATION));
        subscribeAction.putValue(Action.NAME, Res.getString("menuitem.subscribe.to"));

        Roster roster = SparkManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null && entry.getType() == RosterPacket.ItemType.from) {
            popup.add(subscribeAction);
        } else if (entry != null && entry.getType() != RosterPacket.ItemType.both && entry.getStatus() == RosterPacket.ItemStatus.SUBSCRIPTION_PENDING) {
            popup.add(subscribeAction);
        }

        // Fire Context Menu Listener
        // fireContextMenuListenerPopup(popup, item);

        // ContactGroup group = getContactGroup(item.getGroupName());

        System.out.println("右键菜单");
        popup.show(component, e.getX(), e.getY());

    }//////

    public void deleteChildNode(ExpandTreeNode selectedNode) {
        if (selectedNode != null) {
            selectedNode.removeAllChildren();

//    	    nodes = model.getPathToRoot(selectedNode);
//			path = new TreePath(nodes);
//			tree.scrollPathToVisible(path);
//			tree.updateUI();
        }
    }


    public String getTreeChildren(String path) {

        String detail = "";
        try {
            URL url = new URL(path);

            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setDoOutput(true);
            connect.setRequestMethod("GET");
            connect.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connect.connect();

            InputStream is = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    is, "utf-8"));
            String strLine = null;

            while ((strLine = br.readLine()) != null) {
                detail += strLine;
            }
            br.close();
            is.close();
            connect.disconnect();
        } catch (MalformedURLException e) {
            //:这里需要做一些错误处理工作。这里是服务器连接不能错误内容。
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(SparkManager.getWorkspace(),"组织框架树的数据源无法连接","Error:组织框架树",JOptionPane.ERROR_MESSAGE);
                }
            });
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
        System.out.println(path + "   " + detail);
        return detail;

    }

    /**
     * 展开一棵树
     *
     * @param tree
     */
    private void expandTree(JTree tree) {
        // 根节点

        TreeNode node = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(node), true);
    }

    /**
     * 完全展开一棵树或关闭一棵树
     *
     * @param tree   JTree
     * @param parent 父节点
     * @param expand true 表示展开，false 表示关闭
     */
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() > 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    private void selectNodeChildren(CheckTreeNode node, boolean isSelected) {
        if (!node.isLeaf()) {
            //node.getNextNode();
            Enumeration enu = node.children();
            while (enu.hasMoreElements()) {
                CheckTreeNode n = (CheckTreeNode) enu.nextElement();
                n.setSelected(isSelected);
                if (!n.isLeaf()) {
                    selectNodeChildren(n, isSelected);
                }
            }
        }
    }

    class NodeSelectionListener extends MouseAdapter {
        JTree tree;

        NodeSelectionListener(JTree tree) {
            this.tree = tree;
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            if (path != null) {
                CheckTreeNode node = (CheckTreeNode) path.getLastPathComponent();
                boolean isSelected = !node.isSelected();
                node.setSelected(isSelected);
                selectNodeChildren(node, isSelected);
                if (node.getSelectionMode() == CheckTreeNode.DIG_IN_SELECTION) {
                    if (isSelected) {
                        //tree.expandPath(path);
                    } else {
                        //tree.collapsePath(path);
                    }
                }
                ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
                // I need revalidate if node is root.  but why?

                tree.revalidate();
                tree.repaint();

            }
        }
    }


}

