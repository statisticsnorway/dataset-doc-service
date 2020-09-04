package no.ssb.dapla.dataset.doc.service;

import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;

import java.util.List;
import java.util.Map;

public class MockConceptLookup implements ConceptNameLookup {
    @Override
    public Map<String, String> getNameToIds(String conceptType) {
        switch (conceptType) {
            case "Population":
                return Map.of("some-id-could-be-guid", "All families 2018-01-01",
                        "Population_DUMMY", "Population_default");
            case "RepresentedVariable":
                return Map.of("some-id-could-be-guid", "NationalFamilyIdentifier",
                        "RepresentedVariable_DUMMY", "RepresentedVariable_default");
            case "EnumeratedValueDomain":
                return Map.of("some-id-could-be-guid", "Standard for gruppering av familier",
                        "EnumeratedValueDomain_DUMMY", "EnumeratedValueDomain_default");
            case "DescribedValueDomain":
                return Map.of("some-id-could-be-guid", "Heltall",
                        "DescribedValueDomain_DUMMY", "DescribedValueDomain_default",
                        "ValueDomain_DUMMY", "ValueDomain_default"
                );
            case "UnitType":
                return Map.of("some-id-could-be-guid", "Heltall",
                        "UnitType_DUMMY", "UnitType_default");
            default:
                throw new IllegalArgumentException("");
        }
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
