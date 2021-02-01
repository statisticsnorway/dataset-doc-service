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
    public SmartMatch getSmartId(String conceptType, String fieldName) {
        // TODO: parse json and find matches

        return  new SmartMatch("inherited", "felles.demo.dapla.oktober.kommune$fnr",
                List.of(
                        new ConceptTypeInfo("RepresentedVariable",
                                fieldName + "-from_SmartMatchLookup",
                                "Fødselsnummer",
                                "BNJ"),
                        new ConceptTypeInfo("Population",
                                fieldName + "-from_SmartMatchLookup",
                                "Personer med skatteplikt til Norge i 2019",
                                "BNJ")
                ));
    }
}
