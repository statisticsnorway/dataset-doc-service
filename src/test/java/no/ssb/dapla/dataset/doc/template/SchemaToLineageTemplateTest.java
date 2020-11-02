package no.ssb.dapla.dataset.doc.template;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.traverse.SchemaWithPath;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaToLineageTemplateTest {

    @Test
    void testWithTwoLevels() {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("group").type().stringType().noDefault()
                .name("person").type().optional().type(
                        SchemaBuilder.record("person")
                                .fields()
                                .name("name").type().stringType().noDefault()
                                .name("sex").type().optional().stringType()
                                .name("address").type().optional().type(
                                SchemaBuilder.record("address")
                                        .fields()
                                        .name("street").type().stringType().noDefault()
                                        .name("postcode").type().stringType().noDefault()
                                        .endRecord())
                                .endRecord())
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(schema, // use output schema for input for test to run for now
                                "/kilde/freg",
                                123456789))
                        .outputSchema(schema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();
        System.out.println(jsonString);
    }

    @Test
    void testWithOneLevel() throws JsonProcessingException, JSONException {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("konto").type().optional().type(
                        SchemaBuilder.record("konto")
                                .fields()
                                .name("saldo").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(schema, "/kilde/skatt", 123456789))
                        .outputSchema(schema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println(jsonStringForDataSet);
        String expected = TestUtils.load("testdata/lineage/one-level.json");
        assertThat(jsonString).isEqualTo(expected);
        JSONAssert.assertEquals(expected, jsonString, true);
    }

    @Test
    void testWithOneLevelSourceOnly() throws JsonProcessingException, JSONException {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("konto").type().optional().type(
                        SchemaBuilder.record("konto")
                                .fields()
                                .name("saldo").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(schema, "/kilde/skatt", 123456789))
                        .outputSchema(schema)
                        .simple(true)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println(jsonStringForDataSet);
        String expected = TestUtils.load("testdata/lineage/one-level-simple.json");
        assertThat(jsonString).isEqualTo(expected);
        JSONAssert.assertEquals(expected, jsonString, true);
    }

    @Test
    void testJoinTwoSources() throws JsonProcessingException {
        Schema inputSchemaSkatt = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("sum_innskudd").type().intType().noDefault()
                .endRecord();

        Schema inputSchemaFreg = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("alder").type().stringType().noDefault()
                .endRecord();

        Schema outputSchema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("sum_innskudd").type().intType().noDefault()
                .name("alder").type().stringType().noDefault()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(inputSchemaSkatt, "/kilde/skatt/konto/innskudd", 123456789))
                        .addInput(new SchemaWithPath(inputSchemaFreg, "/kilde/freg/alder", 123456789))
                        .outputSchema(outputSchema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet =
                new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(root);

        System.out.println(jsonStringForDataSet);
    }

    @Test
    void testJoinTwoSourcesFromComplexSchema() throws JsonProcessingException, JSONException {
        Schema inputSchemaSkatt = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("konto").type().optional().type(
                        SchemaBuilder.record("konto")
                                .fields()
                                .name("innskudd").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        Schema inputSchemaFreg = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("person").type().optional().type(
                        SchemaBuilder.record("person")
                                .fields()
                                .name("alder").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        Schema outputSchema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("sum_innskudd").type().intType().noDefault()
                .name("alder").type().stringType().noDefault()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(inputSchemaSkatt, "/kilde/skatt/konto/innskudd", 123456789))
                        .addInput(new SchemaWithPath(inputSchemaFreg, "/kilde/freg/alder", 123456789))
                        .outputSchema(outputSchema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        String expected = TestUtils.load("testdata/lineage/lineage-partial-match.json");
        assertThat(jsonString).isEqualTo(expected);
        JSONAssert.assertEquals(expected, jsonString, true);

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println(jsonStringForDataSet);
    }

    @Test
    void testRawDataSimelarNames() throws JsonProcessingException {
        Schema inputSchemaSkatt = TestUtils.loadSchema("testdata/skatt-v0.68.avsc");

        Schema outputSchema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("personidentifikator").type().stringType().noDefault()
                .name("organisasjonsnummer").type().intType().noDefault()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(inputSchemaSkatt, "/kilde/skatt", 123456789))
                        .outputSchema(outputSchema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();
        System.out.println(jsonString);

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
//        System.out.println(jsonStringForDataSet);
    }

    @Test
    void checkSkattRawToKontoFields() throws JsonProcessingException, JSONException {
        Schema inputSchemaSkatt = TestUtils.loadSchema("testdata/skatt-v0.68.avsc");

        Schema outputSchema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("personidentifikator").type().stringType().noDefault()
                .name("kontonummer").type().intType().noDefault()
                .name("innskudd").type().intType().noDefault()
                .name("gjeld").type().intType().noDefault()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(inputSchemaSkatt, "/kilde/skatt", 123456789))
                        .outputSchema(outputSchema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(new JsonParser().parse(jsonString));

        String expected = TestUtils.load("testdata/lineage/checkSkattRawToKontoFields.json");
        assertThat(jsonOutput).isEqualTo(expected);
        JSONAssert.assertEquals(expected, jsonOutput, true);
    }
}