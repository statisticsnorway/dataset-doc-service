package no.ssb.dapla.dataset.doc.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaType {
    @JsonProperty
    private String schema;

    @JsonProperty
    private String schemaType;

    public String getSchema() {
        return schema;
    }

    public String getSchemaType() {
        return schemaType;
    }

    private SchemaType() {
    }

    public SchemaType(String schemaType, String schema) {
        this.schema = schema;
        this.schemaType = schemaType;
    }
}
