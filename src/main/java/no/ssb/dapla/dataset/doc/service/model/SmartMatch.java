package no.ssb.dapla.dataset.doc.service.model;

import java.util.List;
import java.util.Optional;

public class SmartMatch {
    String relationType;
    String id;
    List<ConceptTypeInfo> matchList;

    public String getFirstMatchId() {
        return matchList.get(0).getId();
    }

    public String getFieldName() {
        return id.split("/$")[1];
    }

    public String getTypeMatchId(String conceptType) {
        Optional<ConceptTypeInfo> first = matchList.stream()
                .filter(conceptTypeInfo -> conceptTypeInfo.getType().equals(conceptType))
                .findFirst();
        if (first.isEmpty()) return null;
        return first.get().getId();
    }

    public SmartMatch(String relationType, String id, List<ConceptTypeInfo> matchList) {
        this.relationType = relationType;
        this.id = id;
        this.matchList = matchList;
    }
}
