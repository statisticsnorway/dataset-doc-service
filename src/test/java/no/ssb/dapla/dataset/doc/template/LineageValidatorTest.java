package no.ssb.dapla.dataset.doc.template;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineageValidatorTest {

    @Test
    void checkValidateOk() {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .endRecord();

        String lineage = """
                {
                  "lineage": {
                    "fields": [
                      {
                        "name": "kontonummer"
                      }
                    ]
                  }
                }
                """;

        ValidateResult validateResult = new LineageValidator(lineage).validate(schema);

        assertThat(validateResult.getMessage()).isEmpty();
    }

    @Test
    void checkValidateFieldMissingInTemplate() {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .name("innskudd").type().stringType().noDefault()
                .endRecord();

        String lineage = """
                {
                  "lineage": {
                    "fields": [
                      {
                        "name": "kontonummer"
                      }
                    ]
                  }
                }
                """;

        ValidateResult validateResult = new LineageValidator(lineage).validate(schema);

        assertThat(validateResult.getMessage()).isEqualTo("New fields added in spark schema [innskudd] is not in lineage template");
    }

    @Test
    void checkValidateFieldMissingInSchema() {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .endRecord();

        String lineage = """
                {
                  "lineage": {
                    "fields": [
                      {
                        "name": "kontonummer"
                      },
                      {
                        "name": "innskudd"
                      }
                    ]
                  }
                }
                """;

        ValidateResult validateResult = new LineageValidator(lineage).validate(schema);

        assertThat(validateResult.getMessage()).isEqualTo("Fields: [innskudd] Documented in lineage template is no longer in spark schema");
    }
}