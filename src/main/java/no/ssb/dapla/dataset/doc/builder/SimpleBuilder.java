package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.simple.EnumInfo;
import no.ssb.dapla.dataset.doc.model.simple.TypeInfo;
import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;
import no.ssb.dapla.dataset.doc.service.model.ConceptTypeInfo;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;
import no.ssb.dapla.dataset.doc.template.SmartMatchLookup;

import java.util.List;
import java.util.Map;


public class SimpleBuilder {

    private SimpleBuilder() {
    }

    public static LogicalRecordBuilder createLogicalRecordBuilder(ConceptNameLookup conceptNameLookup) {
        return new LogicalRecordBuilder(conceptNameLookup);
    }

    public static InstanceVariableBuilder createInstanceVariableBuilder(ConceptNameLookup conceptNameLookup, SmartMatchLookup smartMatchLookup, String name) {
        return new InstanceVariableBuilder(conceptNameLookup, smartMatchLookup, name);
    }

    public static class LogicalRecordBuilder {
        static final String LDS_SCHEMA_NAME = "LogicalRecord";

        private final Record record = new Record();
        private final ConceptNameLookup conceptNameLookup;

        public LogicalRecordBuilder(ConceptNameLookup conceptNameLookup) {
            this.conceptNameLookup = conceptNameLookup;
        }

        public LogicalRecordBuilder name(String shortName) {
            record.setName(shortName);
            return this;
        }

        public LogicalRecordBuilder description(String description) {
            record.setDescription(description);
            return this;
        }


        public LogicalRecordBuilder unitType(String unitTypeId) {
            Map<String, String> nameToIds = conceptNameLookup.getNameToIds("UnitType");
            TypeInfo typeInfo = new TypeInfo(unitTypeId, "UnitType", nameToIds);
            record.setUnitType(typeInfo);
            return this;
        }

        public Record build() {
            return record;
        }

    }

    public static class InstanceVariableBuilder {
        static final String LDS_SCHEMA_NAME = "InstanceVariable";

        private final Instance instance = new Instance();
        private final ConceptNameLookup conceptNameLookup;
        private final SmartMatchLookup smartMatchLookup;
        private final String fieldName;

        public InstanceVariableBuilder(ConceptNameLookup conceptNameLookup, SmartMatchLookup smartMatchLookup, String fieldName) {
            this.conceptNameLookup = conceptNameLookup;
            this.smartMatchLookup = smartMatchLookup;
            this.fieldName = fieldName;
            instance.setName(fieldName);
        }

        public InstanceVariableBuilder dataStructureComponentType() {
            List<String> enumList = conceptNameLookup.getGsimSchemaEnum(LDS_SCHEMA_NAME, "dataStructureComponentType");
            ConceptTypeInfo smartMatch = smartMatchLookup.getSmartId("dataStructureComponentType", fieldName);
            EnumInfo info = new EnumInfo("", enumList, smartMatch.getId());
            instance.setDataStructureComponentType(info);
            return this;
        }

        public InstanceVariableBuilder representedVariable() {
            ConceptTypeInfo smartMatch = smartMatchLookup.getSmartId("RepresentedVariable", fieldName);
            TypeInfo typeInfo = new TypeInfo(smartMatch.getId(), "RepresentedVariable", smartMatch.getId());
            instance.setRepresentedVariable(typeInfo);
            return this;
        }

        public InstanceVariableBuilder population() {
            ConceptTypeInfo smartMatch = smartMatchLookup.getSmartId("Population", fieldName);
            TypeInfo typeInfo = new TypeInfo(smartMatch.getId(), "Population", smartMatch.getId());
            instance.setPopulation(typeInfo);
            return this;
        }

        public InstanceVariableBuilder sentinelValueDomain() {
            ConceptTypeInfo smartMatch = smartMatchLookup.getSmartId("EnumeratedValueDomain", fieldName);
            if (smartMatch.isUnknown()) {
                smartMatch = smartMatchLookup.getSmartId("DescribedValueDomain", fieldName);
            }
            TypeInfo typeInfo = new TypeInfo(smartMatch.getId(), "EnumeratedValueDomain,DescribedValueDomain", smartMatch.getId());
            instance.setSentinelValueDomain(typeInfo);
            return this;
        }

        public InstanceVariableBuilder description(String description) {
            ConceptTypeInfo smartMatch = smartMatchLookup.getSmartId("InstanceVariable", fieldName);
            String smartMatchDescription = smartMatch.getDescription();
            instance.setDescription(smartMatchDescription.isEmpty() ? description : smartMatchDescription);
            return this;
        }

        public Instance build() {
            return instance;
        }
    }
}
