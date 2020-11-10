FROM alpine:latest as build

RUN apk --no-cache add binutils curl tar gzip

#
# Install JDK
#
RUN curl https://cdn.azul.com/zulu/bin/zulu15.28.13-ca-jdk15.0.1-linux_musl_x64.tar.gz -o /jdk.tar.gz
RUN mkdir -p /jdk
RUN tar xzf /jdk.tar.gz --strip-components=1 -C /jdk
ENV PATH=/jdk/bin:$PATH
ENV JAVA_HOME=/jdk

#
# Build stripped JVM
#
RUN ["jlink", "--strip-debug", "--no-header-files", "--no-man-pages", "--compress=2", "--module-path", "/jdk/jmods", "--output", "/linked",\
 "--add-modules", "jdk.unsupported,java.base,java.management,java.net.http,java.xml,java.naming,java.desktop,java.sql,jdk.jcmd,jdk.jartool,jdk.jdi,jdk.jfr"]

#
# Build Application image
#
FROM alpine:latest as base

RUN apk --no-cache add curl tar gzip nano jq

#
# Resources from build image
#
COPY --from=build /linked /jdk/
COPY run.sh /app/
COPY target/libs /app/lib/
COPY target/dataset-doc-service.jar /app/lib/
COPY target/classes/logback.xml /app/conf/
COPY target/classes/logback-bip.xml /app/conf/
COPY target/classes/application.yaml /app/conf/

ENV PATH=/jdk/bin:$PATH

WORKDIR /app

EXPOSE 10190

CMD ["./run.sh"]

