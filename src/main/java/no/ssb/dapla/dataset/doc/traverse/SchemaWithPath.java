package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaWithPath {
    static final float REQUIRED_MATCH_SCORE = 0.5F;// 1.0F is full match
    static final float FULL_MATCH_SCORE = 1.0F;// 1.0F is full match
    static final float MAX_AUTO_MATCH_CONFIDENCE = 0.9F;

    final Schema schema;
    final String path;
    final long version;

    private final FieldFinder fieldFinder;

    public SchemaWithPath(Schema schema, String path, long version) {
        fieldFinder = new FieldFinder(schema);
        this.schema = schema;
        this.path = path;
        this.version = version;
    }

    public Source asSource() {
        return new Source(null, path, version);
    }

    public Source getSource(String name) {
        List<FieldFinder.Field> fields = fieldFinder.find(name);
        if (fields.isEmpty()) {
            return findSource(fieldFinder.findNearMatches(name));
        }
        return findSource(fields);
    }

    private Source findSource(List<FieldFinder.Field> fields) {
        if (fields.isEmpty()) {
            return null;
        }
        List<FieldFinder.Field> validMatches = fields.stream()
                .filter(field -> field.getMatchScore() > REQUIRED_MATCH_SCORE)
                .collect(Collectors.toList());
        if (validMatches.isEmpty()) {
            return null;
        }

        // TODO: move this calculation into a method class to add better testing
        int fieldCount = validMatches.size();
        String paths = validMatches.stream().map(FieldFinder.Field::getPath).collect(Collectors.joining(","));
        float matchScore = validMatches.stream().map(FieldFinder.Field::getMatchScore).reduce((a, b) -> a * b).orElse(0F);
        float confidence = (MAX_AUTO_MATCH_CONFIDENCE / fieldCount) * matchScore;
        List<String> fieldCandidates = getFieldCandidates(validMatches, fieldCount, matchScore);
        return LineageBuilder.crateSourceBuilder()
                .field(fieldCount == 1 && matchScore == FULL_MATCH_SCORE ? paths : "")
                .fieldCandidates(fieldCandidates)
                .path(path)
                .version(version)
                .confidence(confidence)
                .matchScore(matchScore)
                .build();
    }

    private List<String> getFieldCandidates(List<FieldFinder.Field> fields, int fieldCount, Float matchScore) {
        if (fieldCount > 1 || matchScore < 1.0F) {
            return fields.stream().map(FieldFinder.Field::getPath).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
