package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ssb.dapla.dataset.doc.service.model.ConceptTypeInfo;

import java.beans.Introspector;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

public class LdsSmartMatchLookup implements SmartMatchLookup {

    private final String path;
    private final HashMap<String, Smart> idToSmartMatch = new HashMap<>();

    public LdsSmartMatchLookup(String path, JsonNode matches) {
        this.path = path;

        JsonNode first = matches.get(0);
        JsonNode unitDataSet = first.get("unitDataSet");

        JsonNode dataSourcePath = unitDataSet.get("dataSourcePath");

        JsonNode lineageDataset = unitDataSet.get("lineage").get("reverseLineageFieldLineageDataset");
        ObjectMapper objectMapper = new ObjectMapper();

        if (lineageDataset.isArray()) {
            for (JsonNode smartGroup : lineageDataset) {
                String fieldId = smartGroup.get("id").textValue().split("\\$")[1];
                JsonNode smartMatches = smartGroup.get("smart");
                for (JsonNode smartMatch : smartMatches) {
                    try {
                        Smart smart = objectMapper.treeToValue(smartMatch, Smart.class);
                        if (!smart.isValidRelationType()) continue;
                        if (!smart.isDocumented()) continue;
                        idToSmartMatch.put(fieldId, smart);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class Smart {
        static final Set<String> enums = Set.of("dataStructureComponentType");
        static final Set<String> sentinelValueDomains = Set.of("enumeratedValueDomain", "describedValueDomain");

        @JsonProperty
        String relationType;

        @JsonProperty
        String id;

        @JsonProperty
        JsonNode instanceVariable;

        boolean isDocumented() {
            return !instanceVariable.isNull();
        }

        boolean isValidRelationType() {
            return relationType.equals("inherited");
        }

        ConceptTypeInfo getConceptType(String conceptType) {
            if (!isDocumented()) return ConceptTypeInfo.createUnknown(conceptType, id);
            if (conceptType.equals("InstanceVariable")) {
                return new ConceptTypeInfo(
                        conceptType,
                        "",
                        getLanguageTextValue(instanceVariable, "name"),
                        instanceVariable.get("createdBy").asText(),
                        getLanguageTextValue(instanceVariable, "description"));
            }

            String decapitalizedConceptType = Introspector.decapitalize(conceptType);
            if (sentinelValueDomains.contains(decapitalizedConceptType)) {
                decapitalizedConceptType = "sentinelValueDomain";
            }
            JsonNode node = instanceVariable.get(decapitalizedConceptType);
            if (node == null) return ConceptTypeInfo.createUnknown(conceptType, id);

            if (enums.contains(decapitalizedConceptType)) {
                return ConceptTypeInfo.createEnum(conceptType, node.asText());
            }

            String nbName = getLanguageTextValue(node, "name");
            String nbDesc = getLanguageTextValue(node, "description");

            if (nbName.isEmpty()) return ConceptTypeInfo.createUnknown(conceptType, id);

            return new ConceptTypeInfo(
                    conceptType,
                    node.get("id").asText(),
                    nbName,
                    node.get("createdBy").asText(),
                    nbDesc);
        }

        private String getLanguageTextValue(JsonNode node, String field) {
            JsonNode value = node.get(field);
            if (value == null) return "";
            Optional<JsonNode> languageText = StreamSupport.stream(value.spliterator(), false)
                    .map(jsonNode -> jsonNode.get("languageText")).findFirst();
            if (languageText.isEmpty()) return "";
            return languageText.get().asText();
        }
    }

    @Override
    public ConceptTypeInfo
    getSmartId(String conceptType, String fieldName) {
        Smart smart = idToSmartMatch.get(fieldName);
        if (smart == null) return ConceptTypeInfo.createUnknown(conceptType, fieldName);
        return smart.getConceptType(conceptType);
    }
}
