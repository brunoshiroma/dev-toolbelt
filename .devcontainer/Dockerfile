FROM ghcr.io/graalvm/native-image:22.3.1@sha256:e1e9af57e6efe6fa5ab81644f0456caac91fa12a912c50131570354bd74add98

RUN microdnf makecache
RUN microdnf install tar gzip git findutils make -y