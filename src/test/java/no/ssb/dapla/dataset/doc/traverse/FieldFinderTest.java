package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.dapla.dataset.doc.template.TestUtils;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FieldFinderTest {

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

    final FieldFinder fieldFinder = new FieldFinder(inputSchemaSkatt);

    void checkField(Function<String, List<FieldFinder.Field>> func, String name, Consumer<FieldFinder.Field> check) {
        List<FieldFinder.Field> fields = func.apply(name);
        assert fields.size() == 1;
        check.accept(fields.get(0));
    }

    @Test
    void checkThatWeCanFindFields() {
        checkField(fieldFinder::find, "fnr", f -> {
            assertThat(f.path).isEqualTo("fnr");
            assertThat(f.getMatchScore()).isEqualTo(1.0F);
        });

        checkField(fieldFinder::find, "innskudd", f -> {
            assertThat(f.path).isEqualTo("konto.innskudd");
            assertThat(f.getMatchScore()).isEqualTo(1.0F);
        });
    }

    @Test
    void checkThatWeCanFindNearFields() {
        checkField(fieldFinder::findNearMatches, "sum_innskudd", f -> {
            assertThat(f.path).isEqualTo("konto.innskudd");
            assertThat(f.getMatchScore()).isLessThan(1.0F);
        });
    }

    @Test
    void findInSkattSchema() {
        Schema schema = TestUtils.loadSchema("testdata/skatt-v0.68.avsc");

        FieldFinder fieldFinder = new FieldFinder(schema);

        List<FieldFinder.Field> instances = fieldFinder.find("organisasjonsnummer");
        List<String> paths = instances.stream().map(FieldFinder.Field::getPath).collect(Collectors.toList());
        assertThat(paths.size()).isEqualTo(12);

        fieldFinder.find("organisasjonsnummer").stream().map(FieldFinder.Field::getPath).forEach(System.out::println);
        System.out.println("----------------------");
        fieldFinder.find("personidentifikator").stream().map(FieldFinder.Field::getPath).forEach(System.out::println);
    }

}