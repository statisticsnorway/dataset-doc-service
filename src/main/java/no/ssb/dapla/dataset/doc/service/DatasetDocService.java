package no.ssb.dapla.dataset.doc.service;

import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;
import no.ssb.dapla.dataset.doc.template.SchemaToTemplate;
import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DatasetDocService implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetDocService.class);

    // Only for testing before we connect to concept service
    ConceptNameLookup conceptNameLookup = new ConceptNameLookup() {
        @Override
        public Map<String, String> getNameToIds(String conceptType) {
            switch (conceptType) {
                case "Population":
                    return Map.of("some-id-could-be-guid", "All families 2018-01-01",
                            "Population_DUMMY", "Population_default");
                case "RepresentedVariable":
                    return Map.of("some-id-could-be-guid", "NationalFamilyIdentifier",
                            "RepresentedVariable_DUMMY", "RepresentedVariable_default");
                case "EnumeratedValueDomain":
                    return Map.of("some-id-could-be-guid", "Standard for gruppering av familier",
                            "EnumeratedValueDomain_DUMMY", "EnumeratedValueDomain_default");
                case "DescribedValueDomain":
                    return Map.of("some-id-could-be-guid", "Heltall",
                            "DescribedValueDomain_DUMMY", "DescribedValueDomain_default",
                            "ValueDomain_DUMMY", "ValueDomain_default"
                    );
                case "UnitType":
                    return Map.of("some-id-could-be-guid", "Heltall",
                            "UnitType_DUMMY", "UnitType_default");
                default:
                    throw new IllegalArgumentException("");
            }
        }

        @Override
        public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
            switch (conceptType) {
                case "InstanceVariable":
                    return processInstanceVariable(enumType);
                default:
                    throw new IllegalArgumentException("");
            }
        }

        private List<String> processInstanceVariable(String enumType) {
            switch (enumType) {
                case "dataStructureComponentType":
                    return List.of("IDENTIFIER", "MEASURE", "ATTRIBUTE");
                case "dataStructureComponentRole":
                    return List.of("ENTITY", "IDENTITY", "COUNT", "TIME", "GEO");
                default:
                    throw new IllegalArgumentException("");
            }
        }
    };


    DatasetDocService(Config config) {
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
        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(schema, conceptNameLookup).withDoSimpleFiltering(useSimpleFiltering);
        return schemaToTemplate.generateSimpleTemplateAsJsonString();
    }
}
