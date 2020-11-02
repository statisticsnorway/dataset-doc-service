package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.avro.convert.core.SchemaBuddy;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldFinder extends SchemaTraverse<FieldFinder.Record> {

    protected static class Record implements TraverseField<FieldFinder.Record>, ParentAware {
        final Record parent;
        final String name;
        private final List<Record> children = new ArrayList<>();
        private final List<Field> fields = new ArrayList<>();

        private Record(Record parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public ParentAware getParent() {
            return parent;
        }

        @Override
        public void addChild(Record child) {
            children.add(child);
        }

        @Override
        public String getName() {
            return name;
        }

        public List<Record> getChildren() {
            return children;
        }

        public String getPath() {
            PathTraverse<Record> pathTraverse = new PathTraverse<>(this);
            return pathTraverse.getPath("spark_schema");
        }

        public void addField(Field field) {
            fields.add(field);
        }

        public List<Field> find(String name) {
            return fields.stream()
                    .filter(i -> i.isMatch(name))
                    .collect(Collectors.toList());
        }

        public List<Field> findNearMatch(String name) {
            return fields.stream()
                    .filter(i -> i.isNearMatch(name))
                    .collect(Collectors.toList());
        }
    }

    static class Field {
        final String name;
        final String path;

        public Field(String name, String path) {
            this.name = name;
            this.path = path;
        }

        private float matchScore = 1.0F;

        public String getPath() {
            return path;
        }

        public float getMatchScore() {
            return matchScore;
        }

        public boolean isMatch(String name) {
            return name.equals(this.name);
        }

        public boolean isNearMatch(String name) {
            if (this.name.equals(name)) {
                throw new IllegalStateException("isNearMatch should not be used for exact match");
            }
            if (this.name.contains(name)) {
                matchScore = name.length() / (float) this.name.length();
                return true;
            }
            if (name.contains(this.name)) {
                matchScore = this.name.length() / (float) name.length();
                return true;
            }
            return false;
        }

    }

    final SchemaBuddy schemaBuddy;
    final Record root;

    public FieldFinder(Schema schema) {
        this.schemaBuddy = SchemaBuddy.parse(schema);
        root = traverse(schemaBuddy);
    }

    public List<Field> find(String field) {
        List<Field> result = new ArrayList<>();
        search(field, root, result);
        return result;
    }

    public List<Field> findNearMatches(String field) {
        List<Field> result = new ArrayList<>();
        searchNearMatches(field, root, result);
        return result;
    }

    private void search(String name, Record parent, List<Field> result) {
        result.addAll(parent.find(name));
        for (Record child : parent.getChildren()) {
            search(name, child, result);
        }
    }

    private void searchNearMatches(String name, Record parent, List<Field> result) {
        result.addAll(parent.findNearMatch(name));
        for (Record child : parent.getChildren()) {
            searchNearMatches(name, child, result);
        }
    }

    @Override
    protected FieldFinder.Record createChild(SchemaBuddy schemaBuddy, FieldFinder.Record parent) {
        return new FieldFinder.Record(parent, schemaBuddy.getName());
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, Record parent) {
        // TODO: Find a better to deal with "spark_schema" root
        String path = parent.getPath().equals("spark_schema") ? "" : parent.getPath() + ".";
        Field field = new Field(schemaBuddy.getName(), path + schemaBuddy.getName());
        parent.addField(field);
    }
}
