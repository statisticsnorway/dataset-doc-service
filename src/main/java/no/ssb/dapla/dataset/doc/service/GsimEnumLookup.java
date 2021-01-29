package no.ssb.dapla.dataset.doc.service;

import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;

import java.util.List;
import java.util.Map;

public class GsimEnumLookup implements ConceptNameLookup {
    @Override
    public Map<String, String> getNameToIds(String conceptType) {
        return Map.of();
    }

    @Override
    public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
        switch (conceptType) {
            case "InstanceVariable":
                return processInstanceVariable(enumType);
            default:
                throw new IllegalArgumentException("");
        }
    }

    private List<String> processInstanceVariable(String enumType) {
        switch (enumType) {
            case "dataStructureComponentType":
                return List.of("IDENTIFIER", "MEASURE", "ATTRIBUTE");
            case "dataStructureComponentRole":
                return List.of("ENTITY", "IDENTITY", "COUNT", "TIME", "GEO");
            default:
                throw new IllegalArgumentException("");
        }
    }
}
