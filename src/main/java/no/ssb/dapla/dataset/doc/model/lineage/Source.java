package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonPropertyOrder({"field_candidates", "field", "path", "version"})
public class Source {

    @JsonProperty("field_candidates")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> fieldCandidates = new ArrayList<>();

    @JsonProperty
    private String field;

    @JsonProperty
    private String path;

    @JsonProperty
    private long version;

    @JsonIgnore
    private double confidence;

    @JsonIgnore
    private double matchScore;

    public long getVersion() {
        return version;
    }

    public Source(String field, String path, long version) {
        this.field = field;
        this.path = path;
        this.version = version;
    }

    public Source() {
    }

    public String getField() {
        return field;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void addFieldCandidates(Collection<String> fields) {
        fieldCandidates.addAll(fields);
    }

    public String getPath() {
        return path;
    }

    public List<String> getFieldCandidates() {
        return fieldCandidates;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

}
