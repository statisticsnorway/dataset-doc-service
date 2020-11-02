package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class SchemaWithPathTest {

    final Schema inputSchemaSkatt = SchemaBuilder
            .record("spark_schema").namespace("no.ssb.dataset")
            .fields()
            .name("fnr").type().stringType().noDefault()
            .name("konto").type().optional().type(
                    SchemaBuilder.record("konto")
                            .fields()
                            .name("innskudd").type().stringType().noDefault()
                            .endRecord())
            .endRecord();

    SchemaWithPath schemaWithPath = new SchemaWithPath(inputSchemaSkatt, "/path/to/data", 123);

    @Test
    void checkNoMatch() {
        Source s = schemaWithPath.getSource("bla");
        assert s == null;
    }

    @Test
    void checkThatMatches() {
        Source s = schemaWithPath.getSource("innskudd");
        assertThat(s.getField()).isEqualTo("konto.innskudd");
        assertThat(s.getFieldCandidates()).hasSize(0);
        assertThat(s.getConfidence()).isEqualTo(0.9F);
    }

    @Test
    void checkThatHavePartialMatch() {
        Source s = schemaWithPath.getSource("sum_innskudd");
        assertThat(s.getField()).isEqualTo("");
        assertThat(s.getFieldCandidates()).hasSize(1);
        assertThat(s.getFieldCandidates().get(0)).isEqualTo("konto.innskudd");
        assertThat(s.getConfidence()).isLessThan(0.9F);
        assertThat(s.getConfidence()).isGreaterThan(0.5F);
    }

    @Test
    void checkThatHavePartialMatchWithMinimumChars() {
        Source s = schemaWithPath.getSource("innsk");
        assertThat(s.getField()).isEqualTo("");
        assertThat(s.getFieldCandidates()).hasSize(1);
        assertThat(s.getFieldCandidates().get(0)).isEqualTo("konto.innskudd");
        assertThat(s.getConfidence()).isLessThan(0.6F);
        assertThat(s.getConfidence()).isGreaterThan(0.5F);
    }

    @Test
    void checkThatShouldNotMatch3LettersOrLess() {
        Source s = schemaWithPath.getSource("inns");
        assertThat(s).isNull();
    }

}