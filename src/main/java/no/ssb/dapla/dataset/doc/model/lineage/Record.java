package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.ssb.dapla.dataset.doc.traverse.ParentAware;
import no.ssb.dapla.dataset.doc.traverse.TraverseField;

public class Record extends Field implements TraverseField<Record>, ParentAware {

    public Record() {
        super();
        type = "structure"; // always structure for LogicalRecord
    }

    @JsonIgnore
    private Record parent;

    @Override
    public ParentAware getParent() {
        return parent;
    }

    @Override
    public void addChild(Record record) {
        fields.add(record);
    }

    public void addInstanceVariable(Instance instance) {
        fields.add(instance);
    }

    public void setParent(Record parent) {
        this.parent = parent;
    }
}
