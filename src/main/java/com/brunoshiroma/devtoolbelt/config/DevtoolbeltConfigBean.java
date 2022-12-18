package com.brunoshiroma.devtoolbelt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DevtoolbeltConfigBean {

    private final String release;

    public DevtoolbeltConfigBean(@Value("${release}")String release) {
        this.release = release;
    }

    public String getRelease() {
        return release;
    }
}
