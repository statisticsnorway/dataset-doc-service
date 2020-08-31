package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaWithOptions {
    @JsonProperty
    private boolean useSimpleFiltering;

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

    public Boolean useSimpleFiltering() {
        return useSimpleFiltering;
    }

    public SchemaWithOptions() {
    }

    public SchemaWithOptions(boolean useSimpleFiltering, String schemaType, String schema) {
        this.useSimpleFiltering = useSimpleFiltering;
        this.schema = schema;
        this.schemaType = schemaType;
    }
}
