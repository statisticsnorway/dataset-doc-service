package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.helidon.media.common.DefaultMediaSupport;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class DatasetDocServiceHttpTestHelper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    static Schema getSchema() {
        return SchemaBuilder
                .record("root")
                .fields()
                .name("userId").type().stringType().noDefault()
                .name("name").type().optional().stringType()
                .endRecord();
    }

    static WebClientResponse post(String resource, String json, int port) {
        WebClient webClient = WebClient.builder()
                .baseUri("http://localhost:" + port)
                .addMediaSupport(DefaultMediaSupport.create())
                .addMediaSupport(JacksonSupport.create(mapper))
                .build();

        return webClient.post()
                .path(resource)
                .submit(json).toCompletableFuture().join();
    }

    static WebClientResponse createTemplateFromSchema(String json, int port) {
        return post("/doc/template", json, port);
    }

    static WebClientResponse validateTemplateAgainstSchema(String json, int port) {
        return post("/doc/validate", json, port);
    }
}
