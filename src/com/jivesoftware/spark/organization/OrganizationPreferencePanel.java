package com.jivesoftware.spark.organization;

import org.jivesoftware.spark.component.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;


public class OrganizationPreferencePanel extends JPanel {

    private static final long serialVersionUID = -4217756610718587907L;
    private JTextField contactsUrl;
    private JTextField groupUrl;

    public OrganizationPreferencePanel() {
        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        contents.setBackground(new Color(0, 0, 0, 0));
        this.setLayout(new VerticalFlowLayout());
        contents.setBorder(BorderFactory.createTitledBorder(OrganizationResources.getString("orgcontacts.settings")));

        add(contents);

        contactsUrl = new JTextField();
        groupUrl = new JTextField();

        Insets in = new Insets(5, 5, 5, 5);

        contents.add(new JLabel(OrganizationResources.getString("organization.url")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
        contents.add(contactsUrl, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

        contents.add(new JLabel(OrganizationResources.getString("service.url")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));
        contents.add(groupUrl, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, in, 0, 0));

    }

    public String getContactsUrl() {
        return contactsUrl.getText();
    }

    public void setContactsUrl(String url) {
        contactsUrl.setText("" + url);
    }

    public String getGroupUrl() {
        return groupUrl.getText();
    }

    public void setGroupUrl(String name) {
        groupUrl.setText(name);
    }
}
