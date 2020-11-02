package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.webclient.WebClientResponse;
import no.ssb.dapla.dataset.doc.service.model.SchemaWithOptions;
import no.ssb.dapla.dataset.doc.service.model.TemplateValidationResult;
import no.ssb.dapla.dataset.doc.service.model.ValidateTemplateOptions;
import no.ssb.dapla.dataset.doc.template.ValidateResult;
import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.helidon.config.ConfigSources.classpath;
import static org.assertj.core.api.Assertions.assertThat;

class DatasetDocServiceHttpMockLdsTest extends DatasetDocServiceHttpTest {

    @BeforeAll
    public static void startTheServer() {
        Config config = Config
                .builder(classpath("application-test.yaml"),
                        classpath("application-dev.yaml"),
                        classpath("application.yaml"))
                .metaConfig()
                .build();
        startServer(config);
    }

    @Test
    void thatWeCanPostAvroSchemaAndGetTemplate() throws JsonProcessingException {
        Schema schema = DatasetDocServiceHttpTestHelper.getSchema();

        String jsonAvroSchemaString = schema.toString();
        SchemaWithOptions schemaWithOptions = new SchemaWithOptions(false, "AVRO", jsonAvroSchemaString);
        String json = new ObjectMapper().writeValueAsString(schemaWithOptions);

        WebClientResponse response = DatasetDocServiceHttpTestHelper.createTemplateFromSchema(json, webServer.port());
        Http.ResponseStatus status = response.status();
        assert status == Http.Status.OK_200;

        String body = response.content().as(String.class).toCompletableFuture().join();
        String result = TestUtils.load("testdata/data-doc-template-1.json");
        assertThat(body).isEqualTo(result);

    }

    @Test
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
        Http.ResponseStatus status = response.status();
        assert status == Http.Status.OK_200;

        String body = response.content().as(String.class).toCompletableFuture().join();
        String result = TestUtils.load("testdata/data-doc-template-1.json");
        assertThat(body).isEqualTo(result);

    }

    @Test
    void thatWeVerifyTemplateAndSchemaMatches() throws JsonProcessingException {
        Schema schema = DatasetDocServiceHttpTestHelper.getSchema();

        String jsonAvroSchemaString = schema.toString();
        String result = TestUtils.load("testdata/data-doc-template-1.json");
        ValidateTemplateOptions schemaWithOptions = new ValidateTemplateOptions(result, "AVRO", jsonAvroSchemaString);
        String json = new ObjectMapper().writeValueAsString(schemaWithOptions);

        WebClientResponse response = DatasetDocServiceHttpTestHelper.validateTemplateAgainstSchema(json, webServer.port());
        Http.ResponseStatus status = response.status();
        assert status == Http.Status.OK_200;

        TemplateValidationResult body = response.content().as(TemplateValidationResult.class).toCompletableFuture().join();
        assertThat(body.getStatus()).isEqualTo("ok");

    }
}
