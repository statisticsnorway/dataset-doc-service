package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.helidon.config.Config;
import io.helidon.webserver.WebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.helidon.config.ConfigSources.classpath;

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
        super.thatWeCanPostAvroSchemaAndGetTemplate();
    }

    @Test
    void thatWeCanPostSparkSchemaAndGetTemplate() throws JsonProcessingException {
        super.thatWeCanPostSparkSchemaAndGetTemplate();
    }

}
