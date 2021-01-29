package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.databind.JsonNode;
import no.ssb.dapla.dataset.doc.service.model.ConceptTypeInfo;
import no.ssb.dapla.dataset.doc.service.model.SmartMatch;

import java.util.List;

public class LdsSmartMatchLookup implements SmartMatchLookup {

    private final String path;
    private final JsonNode matches;

    public LdsSmartMatchLookup(String path, JsonNode matches) {
        this.path = path;
        this.matches = matches;
    }

    @Override
    public List<SmartMatch> getSmartId(String conceptType, String id) {
        // TODO: parse json and find matches

        return List.of(new SmartMatch("inherited", "felles.demo.dapla.oktober.kommune$fnr",
                List.of(
                        new ConceptTypeInfo("RepresentedVariable",
                                "6cdecd72-c4a8-4b56-9c0a-9fd01c4fea56",
                                "Fødselsnummer",
                                "BNJ"
                        ))));
    }
}
