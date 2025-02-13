package com.brunoshiroma.devtoolbelt;

import com.brunoshiroma.devtoolbelt.config.NativeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(NativeHints.class)
@SpringBootApplication
public class DevtoolbeltApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevtoolbeltApplication.class, args);
	}

}
