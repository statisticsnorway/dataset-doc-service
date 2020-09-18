package no.ssb.dapla.dataset.doc.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaType {
    @JsonProperty
    private String schema;

    @JsonProperty
    private String schemaType;

    @JsonProperty
    private Long timestamp;

    public String getSchema() {
        return schema;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    private SchemaType() {
    }

    public SchemaType(String schemaType, String schema, Long timestamp) {
        this.schema = schema;
        this.schemaType = schemaType;
        this.timestamp = timestamp;
    }
}
