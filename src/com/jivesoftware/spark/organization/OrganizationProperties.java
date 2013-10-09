package com.jivesoftware.spark.organization;

import org.jivesoftware.Spark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class OrganizationProperties {

    private Properties props;
    private File configFile;

    public static final String REMOTE_URL = "weburl";

    private static final Object LOCK = new Object();

    private static OrganizationProperties ins = null;

    public static OrganizationProperties getInstance() {
        synchronized (LOCK) {
            if (ins == null) {
                ins = new OrganizationProperties();
            }
            return ins;
        }
    }

    private OrganizationProperties() {
        this.props = new Properties();

        try {
            props.load(new FileInputStream(getConfigFile()));
        } catch (IOException e) {
            // Can't load ConfigFile
        }

    }

    private File getConfigFile() {
        if (configFile == null)
            configFile = new File(Spark.getSparkUserHome(), "/spark.properties");
        return configFile;
    }

    public void save() {
        try {
            props.store(new FileOutputStream(getConfigFile()),
                    "Storing orgcontacts properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRemoteURL() {
        return props.getProperty(REMOTE_URL, "");
    }

    public void setRemoteURL(String url) {
        props.setProperty(REMOTE_URL, url);
    }



}
