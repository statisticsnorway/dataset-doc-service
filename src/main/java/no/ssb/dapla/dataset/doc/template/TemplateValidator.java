package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;
import org.apache.avro.Schema;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TemplateValidator {

    private final Record root;

    public TemplateValidator(String existingDocTemplate) {
        try {
            root = new ObjectMapper().readValue(existingDocTemplate, Record.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ValidateResult validate(String avroSchema) {
        Schema schema = new Schema.Parser().parse(avroSchema);
        return validate(schema);
    }

    public ValidateResult validate(Schema schema) {
        SchemaBuddy schemaBuddy = SchemaBuddy.parse(schema);
        Set<String> schemaFieldNames = schemaBuddy.getChildren()
                .stream()
                .map(SchemaBuddy::getName)
                .collect(Collectors.toSet());

        Set<String> templateFieldNames = root.getInstances().stream()
                .map(Instance::getName)
                .collect(Collectors.toSet());

        List<String> newSchemaFields = schemaFieldNames.stream()
                .filter(field -> !templateFieldNames.contains(field))
                .collect(Collectors.toList());

        List<String> removedSchemaFields = templateFieldNames.stream()
                .filter(field -> !schemaFieldNames.contains(field))
                .collect(Collectors.toList());

        return new ValidateResult(newSchemaFields, removedSchemaFields, ValidateResult.Type.DOC_TEMPLATE);
    }
}
