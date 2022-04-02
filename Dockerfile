FROM ubuntu:20.04 as builder

WORKDIR /tools

RUN apt update &&  apt install -y \
    wget \
    build-essential \
    zlib1g-dev


RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.3.0/graalvm-ce-java17-linux-amd64-21.3.0.tar.gz -O graalvm-ce-linux-amd64.tar.gz
RUN wget http://more.musl.cc/10/x86_64-linux-musl/x86_64-linux-musl-native.tgz -O x86_64-linux-musl-native.tgz
RUN wget https://zlib.net/zlib-1.2.12.tar.gz -O zlib.tar.gz

RUN mkdir -p /tools/graalvm-ce
RUN tar -zxvf graalvm-ce-linux-amd64.tar.gz -C /tools/graalvm-ce --strip-components=1
RUN mkdir -p /tools/musl
RUN tar -zxvf x86_64-linux-musl-native.tgz -C /tools/musl --strip-components=1
RUN mkdir -p /tools/zlib
RUN tar -zxvf zlib.tar.gz -C /tools/zlib --strip-components=1

RUN cd /tools/zlib && CC=/tools/musl/bin/gcc ./configure --prefix=/tools/musl --static && make && make install

ENV PATH=$PATH:/tools/graalvm-ce/bin
ENV PATH=$PATH:/tools/musl/bin

RUN gu install native-image

RUN groupadd -r dev && useradd --no-log-init -r -g dev dev

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean
RUN ./gradlew buildGitHubActions

# https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#_with_code_native_image_code
RUN rm -rf target/native
RUN mkdir -p target/native
RUN cd target/native
RUN ls -la /app/build/libs
RUN /tools/graalvm-ce/bin/jar -xvf /app/build/libs/devtoolbelt.jar
RUN cp -R META-INF BOOT-INF/classes
RUN PATH=$PATH:/tools/musl/bin /usr/bin/native-image --no-fallback --static --libc=musl -H:Name=dev-toolbelt -cp BOOT-INF/classes:`find BOOT-INF/lib | tr '\n' ':'`

FROM alpine as runtime
WORKDIR /app
COPY --from=builder /app/dev-toolbelt /app/dev-toolbelt
ENTRYPOINT ["/app/dev-toolbelt"]