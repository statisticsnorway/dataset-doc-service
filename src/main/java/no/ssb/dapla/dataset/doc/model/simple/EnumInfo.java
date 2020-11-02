package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"value", "type"})
public class EnumInfo {
    @JsonProperty("selected-enum")
    private String value;

    @JsonProperty("enums")
    private List<String> enums;

    public EnumInfo() {
    }

    public String getValue() {
        return value;
    }

    public EnumInfo(String value, List<String> enums) {
        this.value = value;
        this.enums = enums;
    }
}
