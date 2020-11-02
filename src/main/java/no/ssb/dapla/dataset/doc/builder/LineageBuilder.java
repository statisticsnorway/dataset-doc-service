package no.ssb.dapla.dataset.doc.builder;

import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.model.lineage.Instance;
import no.ssb.dapla.dataset.doc.model.lineage.Record;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import no.ssb.dapla.dataset.doc.template.SchemaToLineageTemplate;
import no.ssb.dapla.dataset.doc.traverse.SchemaWithPath;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LineageBuilder {

    private LineageBuilder() {
    }

    public static LogicalRecordBuilder createLogicalRecordBuilder() {
        return new LogicalRecordBuilder();
    }

    public static InstanceVariableBuilder createInstanceVariableBuilder() {
        return new InstanceVariableBuilder();
    }

    public static DatasetBuilder createDatasetBuilder() {
        return new DatasetBuilder();
    }

    public static SchemaToLineageBuilder createSchemaToLineageBuilder() {
        return new SchemaToLineageBuilder();
    }

    public static SourceBuilder crateSourceBuilder() {
        return new SourceBuilder();
    }

    public static class SourceBuilder {
        private String field;
        private String path;
        private long version;
        private float confidence;
        private float matchScore;
        private List<String> fieldCandidates;

        public SourceBuilder field(String field) {
            this.field = field;
            return this;
        }

        public SourceBuilder fieldCandidates(Collection<String> fields) {
            this.fieldCandidates = new ArrayList<>(fields);
            return this;
        }

        public SourceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public SourceBuilder version(long version) {
            this.version = version;
            return this;
        }

        public SourceBuilder confidence(float confidence) {
            this.confidence = confidence;
            return this;
        }

        public SourceBuilder matchScore(float matchScore) {
            this.matchScore = matchScore;
            return this;
        }

        public Source build() {
            Source source = new Source(field, path, version);
            source.setConfidence(confidence);
            source.setMatchScore(matchScore);
            source.addFieldCandidates(fieldCandidates);
            return source;
        }
    }

    public static class SchemaToLineageBuilder {
        private final List<SchemaWithPath> schemaWithPaths = new ArrayList<>();
        Schema outputSchema;
        boolean simple = false;

        public SchemaToLineageBuilder addInput(SchemaWithPath schemaWithPath) {
            schemaWithPaths.add(schemaWithPath);
            return this;
        }

        public SchemaToLineageBuilder outputSchema(Schema outputSchema) {
            this.outputSchema = outputSchema;
            return this;
        }

        public SchemaToLineageBuilder simple(boolean simple) {
            this.simple = simple;
            return this;
        }

        public SchemaToLineageTemplate build() {
            return new SchemaToLineageTemplate(schemaWithPaths, outputSchema, simple);
        }
    }

    public static class DatasetBuilder {
        private final Dataset dataset = new Dataset();

        public DatasetBuilder root(Record path) {
            dataset.setRoot(path);
            return this;
        }

        public Dataset build() {
            return dataset;
        }
    }

    public static class LogicalRecordBuilder {
        private final Record record = new Record();

        public LogicalRecordBuilder name(String shortName) {
            record.setName(shortName);
            return this;
        }

        public LogicalRecordBuilder addSources(Collection<Source> sources) {
            record.addSources(sources);
            return this;
        }

        public LogicalRecordBuilder parent(Record parent) {
            record.setParent(parent);
            return this;
        }

        public Record build() {
            return record;
        }

    }

    public static class InstanceVariableBuilder {
        private final Instance instance = new Instance();

        public InstanceVariableBuilder name(String name) {
            instance.setName(name);
            return this;
        }

        public InstanceVariableBuilder type(String type) {
            instance.setType(type);
            return this;
        }

        public InstanceVariableBuilder confidence(Double confidence) {
            instance.setConfidence(confidence);
            return this;
        }

        public InstanceVariableBuilder addSource(Source source) {
            instance.addSource(source);
            return this;
        }

        public InstanceVariableBuilder addTypeCandidates(Collection<String> typeCandidates) {
            instance.addTypeCandidates(typeCandidates);
            return this;
        }

        public InstanceVariableBuilder addSources(Collection<Source> sources) {
            instance.addSources(sources);
            return this;
        }

        public Instance build() {
            return instance;
        }
    }
}
