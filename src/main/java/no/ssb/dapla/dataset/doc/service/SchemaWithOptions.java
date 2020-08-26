package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaWithOptions {

    @JsonProperty
    private boolean useSimpleFiltering;

    @JsonProperty
    private String avroSchemaJson;

    public String getAvroSchemaJson() {
        return avroSchemaJson;
    }

    public Boolean useSimpleFiltering() {
        return useSimpleFiltering;
    }

    public SchemaWithOptions() {
    }

    public SchemaWithOptions(boolean simple, String avroSchemaJson) {
        this.useSimpleFiltering = simple;
        this.avroSchemaJson = avroSchemaJson;
    }
}
