# Simple Developer Toolbelt

# Uses:
 * Springboot 2.6.6
 * Java 17
 * Graalvm 21.3.0
 * Gradle 7.3.3

## Java native binary
Project uses [graalvm](https://www.graalvm.org/) + [springboot native AoT](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/) to generate native [musl](https://musl.libc.org/) binary to run on alpine container.    
Docker image size ~85MB    
Application startup less than 1000ms

## Tools:
 * Http Request Client Ip
