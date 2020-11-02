package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFilter("InstanceVariable_MinimumFilter")
@JsonPropertyOrder({"name", "description", "dataStructureComponentType", "representedVariable", "population", "sentinelValueDomain"})
public class Instance {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private EnumInfo dataStructureComponentType;

    @JsonProperty
    private TypeInfo population;

    @JsonProperty
    private TypeInfo representedVariable;

    @JsonProperty
    private TypeInfo sentinelValueDomain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeInfo getPopulation() {
        return population;
    }

    public void setPopulation(TypeInfo population) {
        this.population = population;
    }

    public EnumInfo getDataStructureComponentType() {
        return dataStructureComponentType;
    }

    public void setDataStructureComponentType(EnumInfo dataStructureComponentType) {
        this.dataStructureComponentType = dataStructureComponentType;
    }

    public TypeInfo getRepresentedVariable() {
        return representedVariable;
    }

    public void setRepresentedVariable(TypeInfo representedVariable) {
        this.representedVariable = representedVariable;
    }

    public TypeInfo getSentinelValueDomain() {
        return sentinelValueDomain;
    }

    public void setSentinelValueDomain(TypeInfo sentinelValueDomain) {
        this.sentinelValueDomain = sentinelValueDomain;
    }
}
