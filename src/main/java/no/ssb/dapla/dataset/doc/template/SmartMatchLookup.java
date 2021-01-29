package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.service.model.SmartMatch;

import java.util.List;

public interface SmartMatchLookup {
    public List<SmartMatch> getSmartId(String conceptType, String id);
}
