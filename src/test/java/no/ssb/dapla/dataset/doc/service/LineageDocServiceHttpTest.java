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
import no.ssb.dapla.dataset.doc.service.model.SchemaType;
import no.ssb.dapla.dataset.doc.service.model.SchemaWithDependencies;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.commons.collections.map.SingletonMap;
import org.apache.spark.sql.avro.SchemaConverters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.helidon.config.ConfigSources.*;


class LineageDocServiceHttpTest {

    protected static final Logger LOG = LoggerFactory.getLogger(LineageDocServiceHttpTest.class);

    protected static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    protected static WebServer webServer;

    @BeforeAll
    public static void startTheServer() {
        Config config = Config
                .builder(classpath("application-test.yaml"),
                        classpath("application-dev.yaml"))
                .metaConfig()
                .build();
        startServer(config);
    }

    static void startServer(Config config) {
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

    WebClientResponse getWebClientResponse(String json) {
        WebClient webClient = WebClient.builder()
                .baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(DefaultMediaSupport.create())
                .addMediaSupport(JacksonSupport.create(mapper))
                .build();

        return webClient.post()
                .path("/lineage/template")
                .submit(json).toCompletableFuture().join();
    }

    @Test
    void thatWeCanPostSparkSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("root")
                .fields()
                .name("userId").type().stringType().noDefault()
                .name("name").type().optional().stringType()
                .endRecord();

        SchemaConverters.SchemaType schemaType = SchemaConverters.toSqlType(schema);
        String schemaJson = schemaType.dataType().json();

        List<Map<String, SchemaType>> dependencies = new ArrayList<>();
        dependencies.add(new SingletonMap("/path/to/dataset", new SchemaType("SPARK", schemaJson)));
        SchemaWithDependencies schemaWithDependencies = new SchemaWithDependencies("SPARK", schemaJson, dependencies);
        String requestJson = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(schemaWithDependencies);
        System.out.println(requestJson);

        WebClientResponse response = getWebClientResponse(requestJson);

        Http.ResponseStatus status = response.status();
        assert status == Http.Status.OK_200;

        String body = response.content().as(String.class).toCompletableFuture().join();
        assert body.equals(expected_lineage_doc);

    }

    static String expected_lineage_doc = "{\n" +
            "  \"lineage\": {\n" +
            "    \"name\": \"spark_schema\",\n" +
            "    \"type\": \"structure\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"name\": \"userId\",\n" +
            "        \"type\": \"inherited\",\n" +
            "        \"confidence\": 0.9,\n" +
            "        \"sources\": [\n" +
            "          {\n" +
            "            \"field\": \"userId\",\n" +
            "            \"path\": \"/path/to/dataset\",\n" +
            "            \"version\": 123\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"name\",\n" +
            "        \"type\": \"inherited\",\n" +
            "        \"confidence\": 0.9,\n" +
            "        \"sources\": [\n" +
            "          {\n" +
            "            \"field\": \"name\",\n" +
            "            \"path\": \"/path/to/dataset\",\n" +
            "            \"version\": 123\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
}
