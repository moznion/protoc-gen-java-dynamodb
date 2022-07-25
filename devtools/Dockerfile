FROM openjdk:11.0.16-jre-slim-bullseye

RUN apt-get update -y && apt-get install --no-install-recommends -y \
        curl \
        ca-certificates \
        unzip \
    && rm -rf /var/lib/apt/lists/*

RUN curl -Lo /tmp/protoc-3.20.1-linux-x86_64.zip https://github.com/protocolbuffers/protobuf/releases/download/v3.20.1/protoc-3.20.1-linux-x86_64.zip \
    && unzip /tmp/protoc-3.20.1-linux-x86_64.zip -d /protoc \
    && rm -rf /tmp/protoc*
ENV PATH="/protoc/bin:${PATH}"

WORKDIR /


