package com.brunoshiroma.devtoolbelt.config;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;
import org.thymeleaf.context.WebEngineContext;

public class NativeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            final var getExchangeMethod = ReflectionUtils.findMethod(WebEngineContext.class, "getExchange");
            hints.reflection().registerMethod(getExchangeMethod, ExecutableMode.INVOKE);
    }
}
