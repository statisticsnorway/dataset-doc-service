package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import no.ssb.dapla.dataset.doc.traverse.TraverseField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonFilter("LogicalRecord_MinimumFilter")
@JsonPropertyOrder({"name", "description", "unitType", "instances", "records"})
public class Record implements TraverseField<Record> {
    public interface CreateIdHandler {

        String createId(Instance name);
    }
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private TypeInfo unitType;

    @JsonProperty("instanceVariables")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Instance> instances = new ArrayList<>();

    @JsonProperty("logicalRecords")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Record> records = new ArrayList<>();

    @Override
    public void addChild(Record child) {
        records.add(child);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeInfo getUnitType() {
        return unitType;
    }

    public void setUnitType(TypeInfo unitType) {
        this.unitType = unitType;
    }

    public void addLogicalRecord(Record record) {
        records.add(record);
    }

    public List<Record> getRecords() {
        return records;
    }

    public void addInstanceVariable(Instance instance) {
        instances.add(instance);
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public List<String> getInstanceVariableIds(CreateIdHandler createIdHandler) {
        return instances.stream().map(i -> "/" + createIdHandler.createId(i)).collect(Collectors.toList());
    }
}
