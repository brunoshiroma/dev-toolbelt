FROM ghcr.io/graalvm/graalvm-ce:latest as builder

RUN gu install native-image

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew nativeBuild
RUN ls -la 

FROM almalinux:minimal as runtime

WORKDIR /app
COPY --from=builder /app/build/native/devtoolbelt /app/devtoolbelt
ENTRYPOINT [ "/app/devtoolbelt" ]