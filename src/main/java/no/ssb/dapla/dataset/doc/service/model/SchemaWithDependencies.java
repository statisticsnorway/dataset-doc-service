package no.ssb.dapla.dataset.doc.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;
import java.util.Map;

public class SchemaWithDependencies {

    @JsonUnwrapped
    private SchemaType schemaType;

    @JsonProperty
    List<Map<String, SchemaType>> dependencies;

    public String getSchema() {
        return schemaType.getSchema();
    }

    public String getSchemaType() {
        return schemaType.getSchemaType();
    }

    public List<Map<String, SchemaType>> getDependencies() {
        return dependencies;
    }

    private SchemaWithDependencies() {
    }

    public SchemaWithDependencies(String schemaType, String schema, List<Map<String, SchemaType>> dependencies) {
        this.schemaType = new SchemaType(schemaType, schema);
        this.dependencies = dependencies;
    }
}
