package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ssb.dapla.dataset.doc.service.model.ConceptTypeInfo;

import java.beans.Introspector;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class LdsSmartMatchLookup implements SmartMatchLookup {

    private final String path;
    private final HashMap<String, Smart> idToSmartMatch = new HashMap<>();

    public LdsSmartMatchLookup(String path, JsonNode matches) {
        this.path = path;

        JsonNode first = matches.get(0);
        JsonNode unitDataSet = first.get("unitDataSet");

        JsonNode dataSourcePath = unitDataSet.get("dataSourcePath");
        System.out.println(dataSourcePath);

        JsonNode smartMatches = unitDataSet.get("lineage").get("reverseLineageFieldLineageDataset");
        ObjectMapper objectMapper = new ObjectMapper();

        if (smartMatches.isArray()) {
            for (JsonNode node : smartMatches) {
                String fieldId = node.get("id").textValue().split("\\$")[1];
                System.out.println(fieldId);
                JsonNode smartItems = node.get("smart");
                for (JsonNode smart1 : smartItems) {
                    System.out.println(smart1);
                    try {
                        Smart smart3 = objectMapper.treeToValue(smart1, Smart.class);
                        idToSmartMatch.put(fieldId, smart3);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class Smart {
        @JsonProperty
        String relationType;

        @JsonProperty
        String id;

        @JsonProperty
        JsonNode instanceVariable;

        boolean isDocumented() {
            return instanceVariable != null;
        }

        ConceptTypeInfo getConceptType(String conceptType) {
            if (!isDocumented())  return ConceptTypeInfo.createUnknown(conceptType, id);
            String decapitalizedConceptType = Introspector.decapitalize(conceptType);
            JsonNode node = instanceVariable.get(decapitalizedConceptType);
            if (node == null) return ConceptTypeInfo.createUnknown(conceptType, id);

            Optional<JsonNode> nbName = StreamSupport.stream(node.get("name").spliterator(), false)
                    .filter(jsonNode -> jsonNode.get("languageCode").asText().contains("nb"))
                    .map(jsonNode -> jsonNode.get("languageText")).findFirst();
            if (nbName.isEmpty()) return ConceptTypeInfo.createUnknown(conceptType, id);;

            return new ConceptTypeInfo(
                    conceptType,
                    node.get("id").asText(),
                    nbName.get().asText(),
                    node.get("createdBy").asText());
        }
    }

    @Override
    public ConceptTypeInfo getSmartId(String conceptType, String fieldName) {
        Smart smart = idToSmartMatch.get(fieldName);
        if (smart == null) return ConceptTypeInfo.createUnknown(conceptType, fieldName);
        return smart.getConceptType(conceptType);
    }
}
