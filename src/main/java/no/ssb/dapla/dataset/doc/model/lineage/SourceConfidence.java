package no.ssb.dapla.dataset.doc.model.lineage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class SourceConfidence {
    private final Collection<Source> sources;

    private final double matchScore;

    public SourceConfidence(Collection<Source> sources) {
        this.sources = sources;

        matchScore = sources.stream().map(Source::getMatchScore).reduce((a, b) -> a * b).orElse(0.0);
    }

    public double getAverageConfidenceOfSources() {
        Optional<Double> confidence = sources.stream().map(Source::getConfidence).reduce(Double::sum);
        double sum = confidence.orElse(0.0);
        return sum != 0.0 ? sum / sources.size() : 0.0;
    }

    public Collection<String> getTypeCandidates() {
        if (matchScore < 1.0) {
//            return sources.stream().map(Source::getType).collect(Collectors.toSet());
            // for now we have no way to now so we returned the two possibilities
            return Arrays.asList("created", "derived");
        }
        return Collections.emptyList();
    }

    public String getFieldType() {
        if (matchScore >= 1.0f) {
            return "inherited";
        }
        return "";
    }
}
