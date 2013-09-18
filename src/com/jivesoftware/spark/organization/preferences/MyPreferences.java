/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package com.jivesoftware.spark.organization.preferences;


import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.resource.SparkRes;

import javax.swing.Icon;
import javax.swing.JComponent;

public class MyPreferences implements Preference {

    private MyPreferenceUI ui;

    public MyPreferences() {
        ui = new MyPreferenceUI();
    }


    public String getTitle() {
        return "Example Preferences";
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.ADD_IMAGE_24x24);
    }

    public String getTooltip() {
        return "Example tooltip in preference dialog";
    }

    public String getListName() {
        return "Examples";
    }

    public String getNamespace() {
        return "EXAMPLE";
    }

    public JComponent getGUI() {
        return ui;
    }

    public void load() {
        // Would load persisted information from file or server and
        // set the UI appropriatly.
        ui.setShowChatHistory(true);
    }

    public void commit() {
        // Would persist the current state of the preferences.
        boolean showChatHistory = ui.isChatHistoryShown();

    }

    public boolean isDataValid() {
        return true;
    }

    public String getErrorMessage() {
        return null;
    }

    public Object getData() {
        return null;
    }

    public void shutdown() {
        // Do nothing.
    }
}
