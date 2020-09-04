package no.ssb.dapla.dataset.doc.service;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.spark.sql.avro.SchemaConverters;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.Test;

public class SparkSchemaConverterTest {


    @Test
    public void generateAvroSchema() {
        String ddl = "`PERSON_ID` STRING,`INCOME` BIGINT,`GENDER` STRING,`MARITAL_STATUS` STRING,`MUNICIPALITY` STRING,`DATA_QUALITY` STRING";
        StructType fromDDL = StructType.fromDDL(ddl);

        Schema schema = SchemaConverters.toAvroType(fromDDL, false, "spark_schema", "namespace");
        System.out.println(schema.toString(true));
    }

    @Test
    public void checkFailingSchema() {
//        StructType fromDDL = StructType([
//                StructField('variable1', StringType(), True)
//                ]);

//        Schema schema = SchemaConverters.toAvroType(fromDDL, false, "spark_schema", "namespace");
//        System.out.println(schema.toString(true));
    }


    @Test
    public void generateDDLFromAvroSchema() {
        Schema schema = SchemaBuilder
                .record("root")
                .fields()
                .name("userId").type().stringType().noDefault()
                .name("name").type().optional().stringType()
                .endRecord();

        SchemaConverters.SchemaType schemaType = SchemaConverters.toSqlType(schema);


        System.out.println(schemaType.toString());

//        String ddl = ((StructType) schemaType.dataType()).toDDL();
//        System.out.println(ddl);
    }

    @Test
    public void generateDDLFromAvroSchema2() {
        Schema schema = SchemaBuilder
                .record("root")
                .fields()
                .name("variable1").type().stringType().noDefault()
                .endRecord();

        SchemaConverters.SchemaType schemaType = SchemaConverters.toSqlType(schema);

        System.out.println(schemaType.toString());

        String json = ((StructType) schemaType.dataType()).json();
        System.out.println(json);
    }
}