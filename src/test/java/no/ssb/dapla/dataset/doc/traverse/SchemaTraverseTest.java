package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.template.TestUtils;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaTraverseTest {

    public static class Field implements TraverseField<Field>, ParentAware {

        private final List<Field> children = new ArrayList<>();
        private final Field parent;
        private final String name;
        private PathTraverse<Field> pathTraverse = null;

        public Field(String name, Field parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public void addChild(Field child) {
            children.add(child);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ParentAware getParent() {
            return parent;
        }

        private PathTraverse<Field> getPathTraverse() {
            if (pathTraverse == null) {
                pathTraverse = new PathTraverse<>(this);
            }
            return pathTraverse;
        }

        public String toString(boolean recursive) {
            StringBuilder sb = new StringBuilder();
            if (recursive) {
                sb.append(String.format("%s%s%n", getPathTraverse().getIntendString(), name));
                for (Field child : children) {
                    sb.append(child.toString(true));
                }
            } else {
                sb.append(name);
            }
            return sb.toString();
        }
    }

    static class MySchemaTraverse extends SchemaTraverse<Field> {

        @Override
        protected Field createChild(SchemaBuddy schemaBuddy, Field parent) {
            return new Field(schemaBuddy.getName(), parent);
        }

        @Override
        protected void processField(SchemaBuddy schemaBuddy, Field parent) {
            parent.addChild(new Field(schemaBuddy.getName(), parent));
        }
    }

    @Test
    void traverse() {
        Schema inputSchemaSkatt = TestUtils.loadSchema("testdata/skatt-v0.68.avsc");

        MySchemaTraverse mySchemaTraverse = new MySchemaTraverse();
        Field traverse = mySchemaTraverse.traverse(SchemaBuddy.parse(inputSchemaSkatt));

        String expected = TestUtils.load("testdata/PathTravereseResultSkatt-skatt-v0.68.txt");
        assertThat(traverse.toString(true)).isEqualTo(expected);

    }
}