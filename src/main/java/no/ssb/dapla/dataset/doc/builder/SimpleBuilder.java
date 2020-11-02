package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.simple.EnumInfo;
import no.ssb.dapla.dataset.doc.model.simple.TypeInfo;
import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleBuilder {

    private SimpleBuilder() {
    }

    public static LogicalRecordBuilder createLogicalRecordBuilder(ConceptNameLookup conceptNameLookup) {
        return new LogicalRecordBuilder(conceptNameLookup);
    }

    public static InstanceVariableBuilder createInstanceVariableBuilder(ConceptNameLookup conceptNameLookup) {
        return new InstanceVariableBuilder(conceptNameLookup);
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

        public InstanceVariableBuilder(ConceptNameLookup conceptNameLookup) {
            this.conceptNameLookup = conceptNameLookup;
        }

        public InstanceVariableBuilder dataStructureComponentType(String dataStructureComponentType) {
            List<String> enumList = conceptNameLookup.getGsimSchemaEnum(LDS_SCHEMA_NAME, "dataStructureComponentType");
            EnumInfo info = new EnumInfo(dataStructureComponentType, enumList);
            instance.setDataStructureComponentType(info);
            return this;
        }

        public InstanceVariableBuilder representedVariable(String representedVariable) {
            Map<String, String> nameToIds = conceptNameLookup.getNameToIds("RepresentedVariable");
            TypeInfo typeInfo = new TypeInfo(representedVariable, "RepresentedVariable", nameToIds);
            instance.setRepresentedVariable(typeInfo);
            return this;
        }

        public InstanceVariableBuilder name(String shortName) {
            instance.setName(shortName);
            return this;
        }

        public InstanceVariableBuilder population(String population) {
            Map<String, String> nameToIds = conceptNameLookup.getNameToIds("Population");
            TypeInfo typeInfo = new TypeInfo(population, "Population", nameToIds);
            instance.setPopulation(typeInfo);
            return this;
        }

        public InstanceVariableBuilder sentinelValueDomain(String sentinelValueDomain) {
            HashMap<String, String> result = new HashMap<>();
            result.putAll(conceptNameLookup.getNameToIds("EnumeratedValueDomain"));
            result.putAll(conceptNameLookup.getNameToIds("DescribedValueDomain"));
            TypeInfo typeInfo = new TypeInfo(sentinelValueDomain, "EnumeratedValueDomain,DescribedValueDomain", result);
            instance.setSentinelValueDomain(typeInfo);
            return this;
        }

        public InstanceVariableBuilder description(String description) {
            instance.setDescription(description);
            return this;
        }

        public Instance build() {
            return instance;
        }
    }
}
