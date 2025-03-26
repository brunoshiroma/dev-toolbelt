# build the native part on docker, depends on host java build
FROM ubuntu:22.04 as builder

WORKDIR /tools

RUN apt update &&  apt install -y \
    wget \
    build-essential \
    zlib1g-dev


RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-23.0.2/graalvm-community-jdk-23.0.2_linux-x64_bin.tar.gz -O graalvm-ce-linux-amd64.tar.gz
RUN wget http://more.musl.cc/10/x86_64-linux-musl/x86_64-linux-musl-native.tgz -O x86_64-linux-musl-native.tgz
RUN wget https://zlib.net/current/zlib.tar.gz -O zlib.tar.gz

RUN mkdir -p /tools/graalvm-ce
RUN tar -zxvf graalvm-ce-linux-amd64.tar.gz -C /tools/graalvm-ce --strip-components=1
RUN mkdir -p /tools/musl
RUN tar -zxvf x86_64-linux-musl-native.tgz -C /tools/musl --strip-components=1
RUN mkdir -p /tools/zlib
RUN tar -zxvf zlib.tar.gz -C /tools/zlib --strip-components=1

RUN cd /tools/zlib && CC=/tools/musl/bin/gcc ./configure --prefix=/tools/musl --static && make && make install

ENV PATH=$PATH:/tools/graalvm-ce/bin
ENV PATH=$PATH:/tools/musl/bin

RUN groupadd -r dev && useradd --no-log-init -r -g dev dev

WORKDIR /app

COPY build/libs/devtoolbelt.jar .
COPY src/main/resources/META-INF BOOT-INF/classes

RUN rm -rf target/native
RUN mkdir -p target/native
RUN cd target/native
RUN /tools/graalvm-ce/bin/jar -xvf /app/devtoolbelt.jar
RUN cp -R META-INF BOOT-INF/classes
RUN native-image --no-fallback --static --libc=musl -H:-AddAllFileSystemProviders -H:Name=dev-toolbelt -cp BOOT-INF/classes:`find BOOT-INF/lib | tr '\n' ':'`

FROM alpine as runtime
WORKDIR /app
COPY --from=builder /app/dev-toolbelt /app/dev-toolbelt
ENTRYPOINT ["/app/dev-toolbelt"]
