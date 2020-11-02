package no.ssb.dapla.dataset.doc.template;

import java.util.List;
import java.util.Map;

public interface ConceptNameLookup {
    Map<String, String> getNameToIds(String conceptType);
    List<String> getGsimSchemaEnum(String conceptType, String enumType);
}
