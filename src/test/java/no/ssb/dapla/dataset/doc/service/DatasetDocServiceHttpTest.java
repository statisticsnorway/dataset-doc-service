package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.media.common.DefaultMediaSupport;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import io.helidon.webserver.WebServer;
import no.ssb.dapla.dataset.doc.service.model.SchemaWithOptions;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.spark.sql.avro.SchemaConverters;
import org.junit.jupiter.api.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


class DatasetDocServiceHttpTest {

    protected static final Logger LOG = LoggerFactory.getLogger(DatasetDocServiceHttpTest.class);

    protected static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    protected static WebServer webServer;

    protected static void startServer(Config config) {
        long webServerStart = System.currentTimeMillis();
        webServer = new DatasetDocApplication(config).get(WebServer.class);
        webServer.start().toCompletableFuture()
                .thenAccept(webServer -> {
                    long duration = System.currentTimeMillis() - webServerStart;
                    LOG.info("Server started in {} ms, listening at port {}", duration, webServer.port());
                })
                .orTimeout(5, TimeUnit.SECONDS)
                .join();
    }
    @AfterAll
    public static void stopServer() {
        if (webServer != null) {
            webServer.shutdown()
                    .toCompletableFuture()
                    .orTimeout(10, TimeUnit.SECONDS)
                    .join();
        }
    }

    protected WebClientResponse getWebClientResponse(String json) {
        WebClient webClient = WebClient.builder()
                .baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(DefaultMediaSupport.create())
                .addMediaSupport(JacksonSupport.create(mapper))
                .build();

        return webClient.post()
                .path("/doc/template")
                .submit(json).toCompletableFuture().join();
    }

    void thatWeCanPostAvroSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("root")
                .fields()
                .name("userId").type().stringType().noDefault()
                .name("name").type().optional().stringType()
                .endRecord();

        String jsonAvroSchemaString = schema.toString();
        SchemaWithOptions schemaWithOptions = new SchemaWithOptions(false, "AVRO", jsonAvroSchemaString);
        String json = new ObjectMapper().writeValueAsString(schemaWithOptions);

        WebClientResponse response = getWebClientResponse(json);

        String body = response.content().as(String.class).toCompletableFuture().join();
        System.out.println(body); // TODO: make test when structure is more stable

        Http.ResponseStatus status = response.status();
        assert status == Http.Status.OK_200;
    }

    void thatWeCanPostSparkSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("root")
                .fields()
                .name("userId").type().stringType().noDefault()
                .name("name").type().optional().stringType()
                .endRecord();

        SchemaConverters.SchemaType schemaType = SchemaConverters.toSqlType(schema);
        String schameJson = schemaType.dataType().json();

        SchemaWithOptions schemaWithOptions = new SchemaWithOptions(false, "SPARK", schameJson);
        String optionsJson = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(schemaWithOptions);
        System.out.println(optionsJson);

        WebClientResponse response = getWebClientResponse(optionsJson);

        String body = response.content().as(String.class).toCompletableFuture().join();
        System.out.println(body); // TODO: make test when structure is more stable

        Http.ResponseStatus status = response.status();
        System.out.println(status);
        assert status== Http.Status.OK_200;
    }
}
