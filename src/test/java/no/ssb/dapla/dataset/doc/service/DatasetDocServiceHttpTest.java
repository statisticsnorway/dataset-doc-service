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
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static io.helidon.config.ConfigSources.classpath;

class DatasetDocServiceHttpTest {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetDocServiceHttpTest.class);

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private static WebServer webServer;

    @BeforeAll
    public static void startTheServer() {
        Config config = Config
                .builder(classpath("application-dev.yaml"),
                        classpath("application.yaml"))
                .metaConfig()
                .build();
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

    @Test
//    @Disabled()
    public void thatWeCanPostSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("root")
                .fields()
                .name("userId").type().stringType().noDefault()
                .name("name").type().optional().stringType()
                .endRecord();

        String jsonAvroSchemaString = schema.toString();
        SchemaWithOptions schemaWithOptions = new SchemaWithOptions(false, jsonAvroSchemaString);
        String json = new ObjectMapper().writeValueAsString(schemaWithOptions);

        WebClient webClient = WebClient.builder()
                .baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(DefaultMediaSupport.create())
                .addMediaSupport(JacksonSupport.create(mapper))
                .build();

        WebClientResponse response = webClient.post()
                .path("/doc/template")
                .submit(json).toCompletableFuture().join();

        String body = response.content().as(String.class).toCompletableFuture().join();
        System.out.println(body);

        Http.ResponseStatus status = response.status();
        System.out.println(status);
    }
}
