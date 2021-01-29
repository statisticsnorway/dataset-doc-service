package no.ssb.dapla.dataset.doc.service.model;

public class ConceptTypeInfo {
    private String type;
    private String id;
    private String name;
    private String createdBy;

    public ConceptTypeInfo(String type, String id, String name, String createdBy) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
    }

    public String getId()   {
        return id;
    }

    public String getType() {
        return type;
    }
}
