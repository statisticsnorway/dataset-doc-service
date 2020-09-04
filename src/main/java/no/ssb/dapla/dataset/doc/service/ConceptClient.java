package no.ssb.dapla.dataset.doc.service;

import io.helidon.config.Config;
import io.helidon.config.ConfigValue;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;

public class ConceptClient {

    private final ConceptNameLookup conceptNameLookup;

    public ConceptClient(Config config) {
        ConfigValue<Boolean> booleanConfigValue = config.get("concept-lds.mock").asBoolean();
        if (booleanConfigValue.isPresent() && booleanConfigValue.get()) {
            conceptNameLookup = new MockConceptLookup();
        } else {
            conceptNameLookup = new LdsConceptLookup(config);
        }
    }

    public ConceptNameLookup getConceptNameLookup() {
        return conceptNameLookup;
    }
}
