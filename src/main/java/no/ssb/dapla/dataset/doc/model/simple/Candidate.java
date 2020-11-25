package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Candidate {
    @JsonProperty
    String id;

    @JsonProperty
    String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Candidate() {
    }

    public Candidate(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
