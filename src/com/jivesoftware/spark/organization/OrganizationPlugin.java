package com.jivesoftware.spark.organization;

import org.jivesoftware.MainWindow;
import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SessionManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.filetransfer.FileTransferListener;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.TranscriptWindow;

import com.jivesoftware.spark.organization.TreePanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;

/**
 * Implements the Spark Plugin framework to display the different possibilities using
 * Spark.
 */
public class OrganizationPlugin implements Plugin {

    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    public void initialize() {
        System.out.println("Welcome To Spark");

        // Add Tab
        addTabToSpark();
   
        SearchManager searchManager = SparkManager.getSearchManager();
        searchManager.addSearchService(new SearchMe());
    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    public void shutdown() {

    }

    /**
     * Return true if the Spark can shutdown on users request.
     *
     * @return true if Spark can shutdown on users request.
     */
    public boolean canShutDown() {
        return true;
    }

    private void addTabToSpark() {
        // Get Workspace UI from SparkManager
        Workspace workspace = SparkManager.getWorkspace();

        // Retrieve the Tabbed Pane from the WorkspaceUI.
        SparkTabbedPane tabbedPane = workspace.getWorkspacePane();

        // Add own Tab.
        //  tabbedPane.addTab("BaoSight", null, new JButton("Hello"));
        //  tabbedPane.addTab(Res.getString("tab.tree"), SparkRes.getImageIcon(SparkRes.SMALL_ALL_CHATS_IMAGE), new TreePanel());
          tabbedPane.addTab("", SparkRes.getImageIcon("ORGANAZATION"), new TreePanel());
        //  ORGANAZATION
    }

    private void addContactListListener() {
    	
        // Get Workspace UI from SparkManager
        Workspace workspace = SparkManager.getWorkspace();

        // Retrieve the ContactList from the Workspace
        ContactList contactList = workspace.getContactList();

        // Create an action to add to the Context Menu
        final Action sayHelloAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Welcome to Spark");
            }
        };

        sayHelloAction.putValue(Action.NAME, "Say Hello To Me");

        // Add own Tab.
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if (object instanceof ContactItem) {
                    popup.add(sayHelloAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });

    }

    /**
     * Adds a ContextMenuListener to a ChatWindow within a ChatRoom.
     */
    private void addContactListenerToChatRoom() {
        // Retrieve a ChatManager from SparkManager
        ChatManager chatManager = SparkManager.getChatManager();

        final ContextMenuListener listener = new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                final TranscriptWindow chatWindow = (TranscriptWindow)object;
                Action clearAction = new AbstractAction() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            chatWindow.insert("My own text :)");
                        }
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                };

                clearAction.putValue(Action.NAME, "Insert my own text");
                popup.add(clearAction);
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        };

        // Add a ChatRoomListener to the ChatManager to allow for notifications
        // when a room is being opened. Note: I will use a ChatRoomListenerAdapter for brevity.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                room.getTranscriptWindow().addContextMenuListener(listener);
            }

            public void chatRoomLeft(ChatRoom room) {
                room.getTranscriptWindow().removeContextMenuListener(listener);
            }
        });
    }

    /**
     * Adds a new menu and child menu item to Spark.
     */
    private void addMenuToSpark() {
        // Retrieve the MainWindow UI from Spark.
        final MainWindow mainWindow = SparkManager.getMainWindow();

        // Create new Menu
        JMenu myPluginMenu = new JMenu("My Plugin Menu");

        // Create Action to test Menu install.
        Action showMessage = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(mainWindow, "Yeah, It works.");
            }
        };

        // Give the menu item a name.
        showMessage.putValue(Action.NAME, "Check if it works");

        // Add to Menu
        myPluginMenu.add(showMessage);

        // Add Menu To Spark
        mainWindow.getJMenuBar().add(myPluginMenu);
    }

    /**
     * Adds a button to each Chat Room that is opened.
     */
    private void addChatRoomButton() {
        // Retrieve ChatManager from the SparkManager
        ChatManager chatManager = SparkManager.getChatManager();

        // Create a new ChatRoomButton.
        final ChatRoomButton button = new ChatRoomButton("Push Me");

        // Add to a new ChatRoom when the ChatRoom opens.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                room.getToolBar().addChatRoomButton(button);
            }

            public void chatRoomLeft(ChatRoom room) {
                room.getToolBar().removeChatRoomButton(button);
            }
        });
    }

    /**
     * Listen for incoming transfer requests and either handle them yourself, or pass them
     * off to be handled by the next listener. If no one handles it, then Spark will handle it.
     */
    private void addTransferListener() {

        // Retrieve SparkTransferManager from the SparkManager.
        SparkTransferManager transferManager = SparkManager.getTransferManager();

        transferManager.addTransferListener(new FileTransferListener() {
            public boolean handleTransfer(FileTransferRequest request) {
                // If I wanted to handle it, take the request, accept it and get the inputstream.

                // Otherwise, return false.
                return false;
            }
        });
    }

    /**
     * Sends a file to a user in your ContactList.
     */
    private void sendFile() {
        // Retrieve SparkTransferManager from the SparkManager.
        SparkTransferManager transferManager = SparkManager.getTransferManager();

        // In order to send a file to a person, you will need to know their full Jabber
        // ID.

        // Retrieve the Jabber ID for a user via the UserManager. This can
        // return null if the user is not in the ContactList or is offline.
        UserManager userManager = SparkManager.getUserManager();
       // String jid = userManager.getJIDFromNickname("Matt");
        
      //  if (jid != null) {
       //     transferManager.sendFile(new File("MyFile.txt"), jid);
      //  }
    }

    /**
     * Controls the UI of a ContactItem.
     */
    private void handleUIAndEventsOfContactItem() {

        ContactList contactList = SparkManager.getWorkspace().getContactList();

        ContactItem item = contactList.getContactItemByJID("paul@jivesoftware.com/spark");

        ContactItemHandler handler = new ContactItemHandler() {


            public boolean handlePresence(ContactItem item, Presence presence) {
                return false;
            }

            public Icon getIcon(String jid) {
                return null;
            }

            public Icon getTabIcon(Presence presence) {
                return null;
            }

            public boolean handleDoubleClick(ContactItem item) {
                return false;
            }

        };

        SparkManager.getChatManager().addContactItemHandler(handler);
    }

    /**
     * Allows a plugin to be notified when the Spark users changes their
     * presence.
     */
    private void addPersonalPresenceListener() {
        SessionManager sessionManager = SparkManager.getSessionManager();

        sessionManager.addPresenceListener(new PresenceListener() {

            /**
             * Spark user changed their presence.
             *
             * @param presence the new presence.
             */
            public void presenceChanged(Presence presence) {

            }
        });
    }

    /**
     * Installs a new MessageFilter.
     */
    private void installMessageFilter() {
        // Retrieve the ChatManager from SparkManager
        ChatManager chatManager = SparkManager.getChatManager();

        MessageFilter messageFilter = new MessageFilter() {


            public void filterOutgoing(ChatRoom room, Message message) {
                String currentBody = message.getBody();
                currentBody = currentBody.replaceAll("bad words", "good words");
                message.setBody(currentBody);
            }

            public void filterIncoming(ChatRoom room, Message message) {
                String currentBody = message.getBody();
                currentBody = currentBody.replaceAll("bad words", "good words");
                message.setBody(currentBody);
            }


        };

        chatManager.addMessageFilter(messageFilter);

        // Just remember to remove your filter if need be.
    }

    /**
     * Creates a person to person Chat Room and makes it the active chat.
     */
    private void createPersonToPersonChatRoom() {

        // Get the ChatManager from Sparkmanager
        ChatManager chatManager = SparkManager.getChatManager();

        // Create the room.
        ChatRoom chatRoom = chatManager.createChatRoom("don@jivesoftware.com", "Don The Man", "The Chat Title");

        // If you wish to make this the active chat room.

        // Get the ChatRooms UI (This is the container for all ChatRooms)
        ChatContainer chatContainer = chatManager.getChatContainer();

        // Ask the ChatRooms container to make this chat the active chat.
        chatContainer.activateChatRoom(chatRoom);
    }

    /**
     * Creates a person to person Chat Room and makes it the active chat.
     */
    private void createConferenceRoom() {

        // Get the ChatManager from Sparkmanager
        ChatManager chatManager = SparkManager.getChatManager();

        Collection serviceNames = null;

        // Get the service name you wish to use.
        try {
            serviceNames = MultiUserChat.getServiceNames(SparkManager.getConnection());
        }
        catch (XMPPException e) {
            e.printStackTrace();
        }

        // Create the room.
        ChatRoom chatRoom = chatManager.createConferenceRoom("BusinessChat", (String)serviceNames.toArray()[0]);

        // If you wish to make this the active chat room.

        // Get the ChatRooms UI (This is the container for all ChatRooms)
        ChatContainer chatContainer = chatManager.getChatContainer();

        // Ask the ChatRooms container to make this chat the active chat.
        chatContainer.activateChatRoom(chatRoom);
    }

    public void uninstall() {
        // Remove all resources and components.
    }
}

