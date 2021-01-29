package no.ssb.dapla.dataset.doc.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class SchemaWithOptions {
    @JsonProperty
    private boolean useSimpleFiltering;

    @JsonProperty
    private String datasetPath; // Used for adding smart match id's

    @JsonUnwrapped
    private SchemaType schemaType;

    public String getSchema() {
        return schemaType.getSchema();
    }

    public String getSchemaType() {
        return schemaType.getSchemaType();
    }

    public Boolean useSimpleFiltering() {
        return useSimpleFiltering;
    }

    public String getDatasetPath() {
        return datasetPath;
    }

    private SchemaWithOptions() {
    }

    public SchemaWithOptions(boolean useSimpleFiltering, String schemaType, String schema) {
        this.useSimpleFiltering = useSimpleFiltering;
        this.schemaType = new SchemaType(schemaType, schema, null);
    }
}
