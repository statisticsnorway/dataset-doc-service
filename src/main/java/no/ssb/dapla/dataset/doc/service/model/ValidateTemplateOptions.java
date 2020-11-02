package no.ssb.dapla.dataset.doc.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ValidateTemplateOptions {

    @JsonProperty
    private String dataDocTemplate;

    public String getSchemaType() {
        return schemaType.getSchemaType();
    }

    public String getSchema() {
        return schemaType.getSchema();
    }

    @JsonUnwrapped
    private SchemaType schemaType;

    public String getDataDocTemplate() {
        return dataDocTemplate;
    }

    public ValidateTemplateOptions() {
    }

    public ValidateTemplateOptions(String dataDocTemplate, String schemaType, String schema) {
        this.dataDocTemplate = dataDocTemplate;
        this.schemaType = new SchemaType(schemaType, schema, null);
    }
}
