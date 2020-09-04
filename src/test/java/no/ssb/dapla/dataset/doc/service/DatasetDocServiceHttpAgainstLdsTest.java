package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.helidon.config.Config;
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
        // Enable for testing against local lds
        super.thatWeCanPostAvroSchemaAndGetTemplate();
    }

    @Test
    @Disabled
    void thatWeCanPostSparkSchemaAndGetTemplate() throws JsonProcessingException {
        // Enable for testing against local lds
        super.thatWeCanPostSparkSchemaAndGetTemplate();
    }
}
