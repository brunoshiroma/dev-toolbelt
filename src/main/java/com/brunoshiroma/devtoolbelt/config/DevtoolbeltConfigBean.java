package com.brunoshiroma.devtoolbelt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties
public class DevtoolbeltConfigBean {

    private final String release;

    public DevtoolbeltConfigBean(String release) {
        this.release = release;
    }

    public String getRelease() {
        return release;
    }
}
