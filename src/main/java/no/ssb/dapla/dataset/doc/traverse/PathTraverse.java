package no.ssb.dapla.dataset.doc.traverse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.StringJoiner;

public class PathTraverse<T extends ParentAware> {
    private final T node;

    public PathTraverse(T node) {
        this.node = node;
    }

    public List<String> getParents(String skip) {
        ParentAware currentParent = node.getParent();
        List<String> parentList = new ArrayList<>();
        while (currentParent != null) {
            if (!currentParent.getName().equals(skip)) {
                parentList.add(currentParent.getName());
            }
            currentParent = currentParent.getParent();
        }
        return parentList;
    }

    public String getPath(String skip) {
        StringJoiner joiner = new StringJoiner(".");
        List<String> parents = getParents(skip);
        for (ListIterator<String> iter = parents.listIterator(parents.size()); iter.hasPrevious(); ) {
            joiner.add(iter.previous());
        }
        return joiner.add(node.getName()).toString();
    }

    String getIntendString() {
        int size = getParents("").size();
        if (size == 0) return "";
        if (size == 1) return " |-- ";
        return String.join("", Collections.nCopies(size - 1, " |   ")) + " |-- ";
    }
}
