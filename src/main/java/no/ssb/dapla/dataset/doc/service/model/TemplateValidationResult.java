package no.ssb.dapla.dataset.doc.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplateValidationResult {

    @JsonProperty
    private String status;

    @JsonProperty
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public TemplateValidationResult() {
    }

    public TemplateValidationResult(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
