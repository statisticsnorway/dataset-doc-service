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

    @JsonProperty
    private boolean simpleLineage;

    public String getSchema() {
        return schemaType.getSchema();
    }

    public String getSchemaType() {
        return schemaType.getSchemaType();
    }

    public List<Map<String, SchemaType>> getDependencies() {
        return dependencies;
    }

    public boolean isSimpleLineage() {
        return simpleLineage;
    }

    private SchemaWithDependencies() {
    }

    public SchemaWithDependencies(String schemaType, String schema, Long timestamp,
                                  List<Map<String, SchemaType>> dependencies,
                                  boolean simpleLineage) {
        this.schemaType = new SchemaType(schemaType, schema, timestamp);
        this.dependencies = dependencies;
        this.simpleLineage = simpleLineage;
    }
}
