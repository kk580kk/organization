package com.jivesoftware.spark.organization;

import org.jivesoftware.spark.PluginManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: SaintKnight
 * Date: 13-9-6
 * Time: 下午3:44
 * Creator:Huang Jie
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationPreference implements Preference {
    private final static String ContactGPreferenceName = "组织构架联系人";
//    private OrganizationProperties _props;
    private LocalPreferences _props;
    private OrganizationPreferencePanel _prefPanel;

    public OrganizationPreference() {
//        _props = OrganizationProperties.getInstance(); //There are something wrong, read is good but write is bad
        _props = SettingsManager.getLocalPreferences();
        try {
            if (EventQueue.isDispatchThread()) {
                _prefPanel = new OrganizationPreferencePanel();
            } else {
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        _prefPanel = new OrganizationPreferencePanel();
                    }
                });
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public String getTitle() {
        return ContactGPreferenceName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Icon getIcon() {
        ClassLoader cl = getClass().getClassLoader();
        return new ImageIcon(cl.getResource("orgcontacts-logo.png"));
    }

    public String getTooltip() {
        return ContactGPreferenceName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getListName() {
        return ContactGPreferenceName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getNamespace() {
        return ContactGPreferenceName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JComponent getGUI() {
        return _prefPanel;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void load() {
        _prefPanel.setContactsUrl(_props.getWebURL());
        _prefPanel.setGroupUrl(_props.getService());
        //To change body of implemented methods use File | Settings | File Templates.
    }

    PluginManager pluginManager;

    public void commit() {
        _props.setWebURL(_prefPanel.getContactsUrl());
        _props.setService(_prefPanel.getGroupUrl());

        getData();

        SettingsManager.saveSettings();

        //刷新重载plugin
        pluginManager = PluginManager.getInstance();
        Plugin plugin = pluginManager.getPlugin(OrganizationPlugin.class);
        plugin.shutdown();
        pluginManager.removePlugin(plugin);
        plugin = new OrganizationPlugin();
        plugin.initialize();
        pluginManager.registerPlugin(plugin);

//        SparkManager.getWorkspace().loadPlugins();
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isDataValid() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getErrorMessage() {
        return "FAAAAAAAAAAAQ";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getData() {
        return _props;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void shutdown() {
        //do Nothing
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
