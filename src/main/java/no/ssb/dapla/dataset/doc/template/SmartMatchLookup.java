package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.service.model.ConceptTypeInfo;

public interface SmartMatchLookup {
    ConceptTypeInfo getSmartId(String conceptType, String fieldName);
}
