package no.ssb.dapla.dataset.doc.service.model;

import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;

public class SchemaMapper {

    public static Schema getAvroSchema(String schemaType, String schema) {
        switch (schemaType) {
            case "AVRO":
                return new Schema.Parser().parse(schema);
            case "SPARK":
                DataType fromDDL = StructType.fromJson(schema);
                return SchemaConverters.toAvroType(fromDDL, false, "spark_schema", "namespace");
            default:
                throw new IllegalArgumentException("SchemaType " + schemaType + " not supported");
        }
    }

}
