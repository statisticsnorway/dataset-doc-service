package no.ssb.dapla.dataset.doc.traverse;

public interface ParentAware {
    String getName();
    ParentAware getParent();
}
