package no.ssb.dapla.dataset.doc.service;

import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.ssb.dapla.dataset.doc.model.simple.Candidate;
import no.ssb.dapla.dataset.doc.service.model.SchemaMapper;
import no.ssb.dapla.dataset.doc.service.model.SchemaWithOptions;
import no.ssb.dapla.dataset.doc.service.model.TemplateValidationResult;
import no.ssb.dapla.dataset.doc.service.model.ValidateTemplateOptions;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;
import no.ssb.dapla.dataset.doc.template.SchemaToTemplate;
import no.ssb.dapla.dataset.doc.template.TemplateValidator;
import no.ssb.dapla.dataset.doc.template.ValidateResult;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatasetDocService implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetDocService.class);

    private final ConceptClient conceptClient;
    private final ExplorationClient explorationClient;

    DatasetDocService(Config config) {
        conceptClient = new ConceptClient(config);
        explorationClient = new ExplorationClient(config);
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.post("/template", Handler.create(SchemaWithOptions.class, this::createTemplate));
        rules.post("/validate", Handler.create(ValidateTemplateOptions.class, this::validateTemplate));
        rules.get("/candidates/{type}", this::candidates);
        rules.get("/info", this::info);
    }

    private void createTemplate(ServerRequest req, ServerResponse res, SchemaWithOptions schemaWithOptions) {
        try {
            Schema avroSchema = SchemaMapper.getAvroSchema(schemaWithOptions.getSchemaType(), schemaWithOptions.getSchema());
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
            Schema avroSchema = SchemaMapper.getAvroSchema(validateTemplateOptions.getSchemaType(), validateTemplateOptions.getSchema());
            String dataDocTemplate = validateTemplateOptions.getDataDocTemplate();

            ValidateResult validateResult = new TemplateValidator(dataDocTemplate).validate(avroSchema);
            String status = validateResult.isOk() ? "ok" : "differ";
            TemplateValidationResult templateValidationResult = new TemplateValidationResult(status, validateResult.getMessage());

            res.headers().contentType(MediaType.APPLICATION_JSON);
            res.status(Http.Status.OK_200).send(templateValidationResult);
        } catch (Exception e) {
            LOG.error("error", e);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.getMessage());
        }
    }

    private void candidates(ServerRequest req, ServerResponse res) {
        try {
            String type = req.path().param("type");
            res.headers().contentType(MediaType.APPLICATION_JSON);
            List<Candidate> candidates = getCandidatesFromLds(type);
            res.status(Http.Status.OK_200).send(candidates);
        } catch (Exception e) {
            LOG.error("error", e);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.getMessage());
        }
    }

    private List<Candidate> getCandidatesFromLds(String type) {
        ConceptNameLookup conceptNameLookup = conceptClient.getConceptNameLookup();

        Map<String, String> nameToIds;
        if (type.equals("SentinelValueDomain")) {
            nameToIds = new HashMap<>();
            nameToIds.putAll(conceptNameLookup.getNameToIds("EnumeratedValueDomain"));
            nameToIds.putAll(conceptNameLookup.getNameToIds("DescribedValueDomain"));
        } else {
            nameToIds = conceptNameLookup.getNameToIds(type);
        }

        return nameToIds.entrySet().stream()
                .map(e -> new Candidate(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(Candidate::getId))
                .collect(Collectors.toList());
    }

    private String convert(Schema schema, boolean useSimpleFiltering) {
        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(schema, conceptClient.getConceptNameLookup()).withDoSimpleFiltering(useSimpleFiltering);
        return schemaToTemplate.generateSimpleTemplateAsJsonString();
    }

    private void info(ServerRequest req, ServerResponse res) {
        try {
            String path = req.queryParams().first("path").orElseThrow();
            res.headers().contentType(MediaType.APPLICATION_JSON);
            var data = explorationClient.getExplorationMeta(path);
            res.status(Http.Status.OK_200).send(data);
        } catch (Exception e) {
            LOG.error("error", e);
            res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.getMessage());
        }
    }

}
