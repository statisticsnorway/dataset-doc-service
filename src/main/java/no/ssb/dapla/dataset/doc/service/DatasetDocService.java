package no.ssb.dapla.dataset.doc.service;

import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.ssb.dapla.dataset.doc.template.SchemaToTemplate;
import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetDocService implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetDocService.class);

    private final ConceptClient conceptClient;

    DatasetDocService(Config config) {
        conceptClient = new ConceptClient(config);
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.post("/template", Handler.create(SchemaWithOptions.class, this::createTemplate));
        rules.post("/validate", Handler.create(ValidateTemplateOptions.class, this::validateTemplate));
    }

    private void createTemplate(ServerRequest req, ServerResponse res, SchemaWithOptions schemaWithOptions) {
        try {
            Schema avroSchema = getAvroSchema(schemaWithOptions.getSchemaType(), schemaWithOptions.getSchema());
            String result = convert(avroSchema, schemaWithOptions.useSimpleFiltering());
            res.headers().contentType(MediaType.APPLICATION_JSON);
            res.status(Http.Status.OK_200).send(result);
        } catch (Exception e) {
            LOG.error("error", e);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.getMessage());
        }
    }

    private void validateTemplate(ServerRequest req, ServerResponse res, ValidateTemplateOptions validateTemplateOptions) {
        try {
            // TODO: validate
            // update SchemaToTemplate with validate
            res.status(Http.Status.OK_200).send();
        } catch (Exception e) {
            LOG.error("error", e);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.getMessage());
        }
    }

    private Schema getAvroSchema(String schemaType, String schema) {
        switch (schemaType) {
            case "AVRO":
                return new Schema.Parser().parse(schema);
            case "SPARK":
                DataType fromDDL = StructType.fromJson(schema);
                return SchemaConverters.toAvroType(fromDDL, false, "spark_schema", "namespace");
            default:
                throw new IllegalArgumentException("SchemaType " + schemaType + " not supported");
        }
    }

    private String convert(Schema schema, boolean useSimpleFiltering) {
        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(schema, conceptClient.getConceptNameLookup()).withDoSimpleFiltering(useSimpleFiltering);
        return schemaToTemplate.generateSimpleTemplateAsJsonString();
    }
}
