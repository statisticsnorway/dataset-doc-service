#
# Build Application image
#
FROM adoptopenjdk/openjdk14:alpine
RUN apk --no-cache add curl

#
# Resources from build image
#
COPY target/libs /app/lib/
COPY target/dataset-doc-service.jar /app/lib/
COPY target/classes/logback.xml /app/conf/
COPY target/classes/logback-bip.xml /app/conf/
COPY target/classes/application.yaml /app/conf/

WORKDIR /app

CMD ["java", "--enable-preview", "-cp", "/app/lib/*", "no.ssb.dapla.dataset.doc.service.DatasetDocApplication"]

EXPOSE 10190
