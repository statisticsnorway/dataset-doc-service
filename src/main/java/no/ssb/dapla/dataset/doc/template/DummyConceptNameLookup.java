package no.ssb.dapla.dataset.doc.template;

import java.util.List;
import java.util.Map;

public class DummyConceptNameLookup implements ConceptNameLookup {
    @Override
    public Map<String, String> getNameToIds(String conceptType) {
        return Map.of(conceptType + "_default", conceptType + "_DUMMY");
    }

    @Override
    public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
        return List.of("DUMMY");
    }
}
