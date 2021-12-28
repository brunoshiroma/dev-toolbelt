FROM ubuntu:20.04 as builder

WORKDIR /tools

RUN apt update
RUN apt install wget -y
RUN apt install build-essential -y
RUN apt install zlib1g-dev -y

RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.2.0/graalvm-ce-java16-linux-amd64-21.2.0.tar.gz -O graalvm-ce-linux-amd64.tar.gz

RUN mkdir -p /tools/graalvm-ce
RUN tar -zxvf graalvm-ce-linux-amd64.tar.gz -C /tools/graalvm-ce --strip-components=1
RUN ln -s /tools/graalvm-ce/bin/java /usr/bin/java
RUN ln -s /tools/graalvm-ce/bin/gu /usr/bin/gu
RUN ln -s /tools/graalvm-ce/bin/native-image /usr/bin/native-image

RUN gu install native-image


WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean
RUN ./gradlew nativeBuild

FROM ubuntu:20.04 as runtime
WORKDIR /app
COPY --from=builder /app/build/native/nativeBuild/devtoolbelt /app/dev-toolbelt
ENTRYPOINT ["/app/dev-toolbelt"]