package com.jivesoftware.spark.organization;


import org.jivesoftware.spark.util.log.Log;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class OrganizationResources {
    private static PropertyResourceBundle prb;

    static ClassLoader cl = OrganizationResources.class.getClassLoader();

    static {
        prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/organization_i18n", Locale.getDefault());
    }

    public static final String getString(String propertyName) {
        try {
            return prb.getString(propertyName);
        } catch (Exception e) {
            Log.error(e);
            return propertyName;
        }
    }
}
