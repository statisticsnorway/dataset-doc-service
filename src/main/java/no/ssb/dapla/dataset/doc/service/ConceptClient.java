package no.ssb.dapla.dataset.doc.service;

import io.helidon.config.Config;
import io.helidon.config.ConfigValue;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptClient {

    private final ConceptNameLookup conceptNameLookup;
    private static final Logger LOG = LoggerFactory.getLogger(ConceptClient.class);

    public ConceptClient(Config config) {
        ConfigValue<Boolean> booleanConfigValue = config.get("concept-lds").get("mock").asBoolean();
        if (booleanConfigValue.isPresent() && booleanConfigValue.get()) {
            LOG.info("Using mock concept service");
            conceptNameLookup = new MockConceptLookup();
        } else {
            conceptNameLookup = new LdsConceptLookup(config);
        }
    }

    public ConceptNameLookup getConceptNameLookup() {
        return conceptNameLookup;
    }
}
