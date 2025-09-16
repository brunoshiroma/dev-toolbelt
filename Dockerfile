# build the native part on docker, depends on host java build
FROM container-registry.oracle.com/graalvm/native-image:25-muslib AS builder

RUN microdnf makecache && microdnf install findutils -y

RUN groupadd -r dev && useradd --no-log-init -r -g dev dev

WORKDIR /app

COPY build/libs/devtoolbelt.jar .
COPY src/main/resources/META-INF BOOT-INF/classes

RUN rm -rf target/native
RUN mkdir -p target/native
RUN cd target/native
RUN jar -xvf /app/devtoolbelt.jar
RUN cp -R META-INF BOOT-INF/classes
RUN native-image -Os --no-fallback --static --libc=musl -H:-AddAllFileSystemProviders -H:Name=dev-toolbelt -cp BOOT-INF/classes:`find BOOT-INF/lib | tr '\n' ':'`

FROM alpine AS runtime
WORKDIR /app
COPY --from=builder /app/dev-toolbelt /app/dev-toolbelt
ENTRYPOINT ["/app/dev-toolbelt"]
