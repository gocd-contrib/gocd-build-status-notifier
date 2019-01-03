package com.tw.go.plugin;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.tw.go.plugin.provider.Provider;

import java.lang.reflect.Constructor;
import java.util.Properties;

@Extension
public class GerritBuildStatusNotifierPlugin extends BuildStatusNotifierPlugin {
    @Override
    protected Provider loadProvider() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/defaults.properties"));
            Class<?> providerClass = Class.forName(properties.getProperty("provider"));
            Constructor<?> constructor = providerClass.getConstructor();
            return (Provider) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("could not create provider", e);
        }
    }
}
