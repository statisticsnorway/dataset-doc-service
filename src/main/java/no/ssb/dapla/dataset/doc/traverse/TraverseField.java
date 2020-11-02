package no.ssb.dapla.dataset.doc.traverse;

public interface TraverseField<T> {
    void addChild(T child);
    String getName();
}
