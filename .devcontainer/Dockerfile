FROM ghcr.io/graalvm/native-image-community:21.0.0-muslib-ol9-20230919

RUN microdnf makecache
RUN microdnf install tar gzip git findutils make -y