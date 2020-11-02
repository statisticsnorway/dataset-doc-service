package no.ssb.dapla.dataset.doc.template;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateValidatorTest {

    @Test
    void checkValidateOk() {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .endRecord();

        String template = """
                {
                  "name" : "",
                  "description" : "",
                  "instanceVariables" : [ {
                    "name" : "kontonummer",
                    "description" : ""
                  } ]
                }
                """;

        ValidateResult validateResult = new TemplateValidator(template).validate(schema);

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

        String template = """
                {
                  "name" : "",
                  "description" : "",
                  "instanceVariables" : [ {
                    "name" : "kontonummer",
                    "description" : ""
                  } ]
                }
                """;

        ValidateResult validateResult = new TemplateValidator(template).validate(schema);

        assertThat(validateResult.getMessage()).isEqualTo("New fields added in spark schema [innskudd] is not in doc template");
    }

    @Test
    void checkValidateFieldMissingInSchema() {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .endRecord();

        String template = """
                {
                  "name" : "",
                  "description" : "",
                  "instanceVariables" : [ {
                    "name" : "kontonummer",
                    "description" : ""
                  },{    "name" : "innskudd",
                    "description" : ""
                 }]
                }
                """;

        ValidateResult validateResult = new TemplateValidator(template).validate(schema);

        assertThat(validateResult.getMessage()).isEqualTo("Fields: [innskudd] Documented in doc template is no longer in spark schema");
    }
}