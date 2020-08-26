package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidateTemplateOptions {

    @JsonProperty
    private String datadocTemplate;

    @JsonProperty
    private String avroSchemaJson;

    public String getDatadocTemplate() {
        return datadocTemplate;
    }

    public String getAvroSchemaJson() {
        return avroSchemaJson;
    }

    public ValidateTemplateOptions() {
    }

    public ValidateTemplateOptions(String datadocTemplate, String avroSchemaJson) {
        this.datadocTemplate = datadocTemplate;
        this.avroSchemaJson = avroSchemaJson;
    }
}
