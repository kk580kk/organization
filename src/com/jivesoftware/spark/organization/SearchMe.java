package com.jivesoftware.spark.organization;


import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.search.Searchable;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * A simple example of how to integrate ones own search into Spark.
 */
public class SearchMe implements Searchable {

    /**
     * The icon to show in the search box.
     *
     * @return the icon.
     */
    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.SMALL_AGENT_IMAGE);
    }

    /**
     * Returns the name of this search object that is displayed in the drop down box.
     *
     * @return the name.
     */
    public String getName() {
        return "Searches Nothing Really";
    }

    /**
     * Returns the text that should be displayed in grey when this searchable object
     * is initially selected.
     *
     * @return the text.
     */
    public String getDefaultText() {
        return "Click to search me.";
    }

    /**
     * Returns the text to display in the tooltip.
     *
     * @return the tooltip text.
     */
    public String getToolTip() {
        return "Shows an example of integrating ones own search into Spark.";
    }

    /**
     * Is called when a user hits "Enter" key.
     *
     * @param query the query the user is searching for.
     */
    public void search(String query) {
        JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Nothing Found :(");
    }
}
