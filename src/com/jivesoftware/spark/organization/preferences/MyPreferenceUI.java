package com.jivesoftware.spark.organization.preferences;

import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.FlowLayout;

/**
 * Demonstrates a simple panel used to display a UI that can be used as
 * the Preference UI in the Preferences Dialog. This panel shows a simple
 * UI with accessors for setting the preference values / persistence.
 */
public class MyPreferenceUI extends JPanel {

    private JCheckBox showChatHistory;

    /**
     * Creates the default panel using FlowLayout as the Layout. But
     * GridBagLayout is the really only true layout :)
     */
    public MyPreferenceUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        buildUI();
    }

    private void buildUI() {
        showChatHistory = new JCheckBox();

        // Use Mnemonics for the CheckBox using ResourceUtils.
        ResourceUtils.resButton(showChatHistory, "&Show Chat History in Chat Window");

        // Add Button
        add(showChatHistory);
    }

    /**
     * Sets the UI based on previous preferences.
     *
     * @param show true if Chat History to show up.
     */
    public void setShowChatHistory(boolean show) {
        showChatHistory.setSelected(show);
    }

    /**
     * Returns true if Chat History should be shown.
     *
     * @return true if history shown.
     */
    public boolean isChatHistoryShown() {
        return showChatHistory.isSelected();
    }


}
