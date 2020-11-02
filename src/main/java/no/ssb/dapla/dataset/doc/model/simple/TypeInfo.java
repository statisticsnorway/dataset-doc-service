package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonPropertyOrder({"value", "type"})
public class TypeInfo {
    @JsonProperty("selected-id")
    private String id;

    @JsonProperty("concept-type")
    private String type;

    @JsonProperty("candidates")
    private List<Candidate> candidates;

    public TypeInfo() {
    }

    public TypeInfo(String id, String type, Map<String, String> candidatesNameToId) {
        this.id = id;
        this.type = type;
        if (candidatesNameToId == null) {
            this.candidates = List.of();
        } else {
            this.candidates = candidatesNameToId.entrySet().stream()
                    .map(e -> new Candidate(e.getKey(), e.getValue()))
                    .sorted(Comparator.comparing(o -> o.id))
                    .collect(Collectors.toList());
        }
        makeSureWeHaveOneElement();
        makeSureWeHaveCorrectSelectedId();
    }

    private void makeSureWeHaveCorrectSelectedId() {
        if (candidates.stream().noneMatch(candidate -> candidate.id.equals(id))) {
            id = candidates.get(0).id;
        }
    }

    // Fix to avoid gui crash on emptyListForNow
    private void makeSureWeHaveOneElement() {
        if (candidates.isEmpty()) {
            String[] types = type.split(",");
            this.id = types[0] + "_DUMMY";
            for (String typeCandidates : types) {
                candidates.add(new Candidate(typeCandidates + "_DUMMY", typeCandidates + "_generated"));
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
