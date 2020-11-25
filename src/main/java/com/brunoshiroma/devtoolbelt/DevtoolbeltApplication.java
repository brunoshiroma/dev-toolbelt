package com.brunoshiroma.devtoolbelt;

import com.brunoshiroma.devtoolbelt.config.DevtoolbeltConfigBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DevtoolbeltConfigBean.class)
public class DevtoolbeltApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevtoolbeltApplication.class, args);
	}

}
