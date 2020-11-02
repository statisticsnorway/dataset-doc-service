package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.webclient.WebClientResponse;
import no.ssb.dapla.dataset.doc.service.model.SchemaWithOptions;
import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.helidon.config.ConfigSources.classpath;

class DatasetDocServiceHttpAgainstLdsTest extends DatasetDocServiceHttpTest {

    @BeforeAll
    public static void startTheServer() {
        Config config = Config
                .builder(classpath("application-dev.yaml"),
                        classpath("application.yaml"))
                .metaConfig()
                .build();
        startServer(config);
    }

    @Test
    @Disabled
    void thatWeCanPostAvroSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = DatasetDocServiceHttpTestHelper.getSchema();

        String jsonAvroSchemaString = schema.toString();
        SchemaWithOptions schemaWithOptions = new SchemaWithOptions(false, "AVRO", jsonAvroSchemaString);
        String json = new ObjectMapper().writeValueAsString(schemaWithOptions);

        WebClientResponse response = getWebClientResponse(json);

        String body = response.content().as(String.class).toCompletableFuture().join();
        System.out.println(body); // TODO: make test when structure is more stable

        Http.ResponseStatus status = response.status();
        assert status == Http.Status.OK_200;
    }

    @Test
    @Disabled
    void thatWeCanPostSparkSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = DatasetDocServiceHttpTestHelper.getSchema();
        SchemaConverters.SchemaType schemaType = SchemaConverters.toSqlType(schema);
        String schemaJson = schemaType.dataType().json();

        SchemaWithOptions schemaWithOptions = new SchemaWithOptions(false, "SPARK", schemaJson);
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
