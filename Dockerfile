FROM ubuntu:20.04 as builder

WORKDIR /tools

RUN apt update
RUN apt install wget -y
RUN apt install build-essential -y
RUN apt install zlib1g-dev -y
# RUN apt install musl-tools -y

# musl is only compatible with java 11, by now
RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.2.0/graalvm-ce-java11-linux-amd64-21.2.0.tar.gz -O graalvm-ce-linux-amd64.tar.gz

RUN mkdir -p /tools/graalvm-ce
RUN tar -zxvf graalvm-ce-linux-amd64.tar.gz -C /tools/graalvm-ce --strip-components=1
RUN ln -s /tools/graalvm-ce/bin/java /usr/bin/java
RUN ln -s /tools/graalvm-ce/bin/gu /usr/bin/gu
RUN ln -s /tools/graalvm-ce/bin/native-image /usr/bin/native-image

RUN gu install native-image

# RUN wget https://zlib.net/zlib-1.2.11.tar.gz -O zlib.tar.gz
# RUN mkdir -p /tools/zlib
# RUN tar -zxvf zlib.tar.gz -C /tools/zlib --strip-components=1
# WORKDIR /tools/zlib
# RUN CC=musl-gcc ./configure --static
# RUN make
# RUN make install


WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean
RUN ./gradlew nativeBuild
#RUN cp build/libs/*-plain.jar dev-toolbelt.jar

#RUN native-image --no-fallback --static -jar dev-toolbelt.jar -Dspring.native.remove-yaml-support=true -H:IncludeResources='templates/.*' -H:IncludeResources='static/.*' -H:IncludeResources='application.*properties\$' -H:Log=registerResource:verbose

RUN mkdir empty

FROM debian:11-slim as runtime
WORKDIR /app
COPY --from=builder /app/build/native/nativeBuild/devtoolbelt /app/dev-toolbelt
ENTRYPOINT ["/app/dev-toolbelt"]