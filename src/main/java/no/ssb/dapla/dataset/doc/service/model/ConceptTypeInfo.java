package no.ssb.dapla.dataset.doc.service.model;

public class ConceptTypeInfo {
    private final String type;
    private final String id;
    private final String name;
    private final String description;
    private final String createdBy;

    public static ConceptTypeInfo createUnknown(String type, String name) {
        return new ConceptTypeInfo(type, "", name, "", "");
    }

    public static ConceptTypeInfo createEnum(String type, String name) {
        return new ConceptTypeInfo(type, name, name, "", "");
    }

    // TODO: create builder
    public ConceptTypeInfo(String type, String id, String name, String createdBy, String description) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUnknown() {
        return id.equals("unknown");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
