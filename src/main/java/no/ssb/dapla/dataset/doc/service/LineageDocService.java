package no.ssb.dapla.dataset.doc.service;

import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.service.model.SchemaMapper;
import no.ssb.dapla.dataset.doc.service.model.SchemaType;
import no.ssb.dapla.dataset.doc.service.model.SchemaWithDependencies;
import no.ssb.dapla.dataset.doc.traverse.SchemaWithPath;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class LineageDocService implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(LineageDocService.class);

    LineageDocService(Config config) {
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.post("/template", Handler.create(SchemaWithDependencies.class, this::createTemplate));
    }

    private void createTemplate(ServerRequest req, ServerResponse res, SchemaWithDependencies schemaWithDependencies) {
        try {
            Schema avroSchema = SchemaMapper.getAvroSchema(schemaWithDependencies.getSchemaType(), schemaWithDependencies.getSchema());
            String result = convert(avroSchema, schemaWithDependencies.getDependencies(), schemaWithDependencies.isSimpleLineage());
            res.headers().contentType(MediaType.APPLICATION_JSON);
            res.status(Http.Status.OK_200).send(result);
        } catch (Exception e) {
            LOG.error("error", e);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.getMessage());
        }
    }

    private String convert(Schema avroSchema, List<Map<String, SchemaType>> dependencies, boolean isSimpleLineage) {
        LineageBuilder.SchemaToLineageBuilder lineageBuilder =
                LineageBuilder.createSchemaToLineageBuilder()
                        .simple(isSimpleLineage)
                        .outputSchema(avroSchema);

        for (Map<String, SchemaType> item : dependencies) {
            for (Map.Entry<String, SchemaType> entry : item.entrySet()) {
                Schema inputSchema = SchemaMapper.getAvroSchema(entry.getValue().getSchemaType(), entry.getValue().getSchema());
                lineageBuilder.addInput(new SchemaWithPath(inputSchema, entry.getKey(), entry.getValue().getTimestamp()));
            }
        }
        return lineageBuilder.build().generateTemplateAsJsonString();
    }
}
