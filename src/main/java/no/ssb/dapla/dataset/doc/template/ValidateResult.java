package no.ssb.dapla.dataset.doc.template;

import java.util.List;
import java.util.Map;

public class ValidateResult {
    private final List<String> missingTemplateFields;
    private final List<String> missingSchemaFields;
    private final Type type;

    public enum Type {
        DOC_TEMPLATE,
        LINEAGE_TEMPLATE
    }

    public ValidateResult(List<String> missingTemplateFields, List<String> missingSchemaFields, Type type) {
        this.missingTemplateFields = missingTemplateFields;
        this.missingSchemaFields = missingSchemaFields;
        this.type = type;
    }

    public boolean isOk() {
        return missingSchemaFields.isEmpty() && missingTemplateFields.isEmpty();
    }

    public String getMessage() {
        Map<Type, String> doc = Map.of(
                Type.DOC_TEMPLATE, "doc",
                Type.LINEAGE_TEMPLATE, "lineage"
        );

        StringBuilder sb = new StringBuilder();
        if (missingTemplateFields.size() > 0) {
            sb.append("New fields added in spark schema ")
                    .append(missingTemplateFields)
                    .append(" is not in ")
                    .append(doc.get(type))
                    .append(" template");
        }
        if (missingSchemaFields.size() > 0) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("Fields: ")
                    .append(missingSchemaFields)
                    .append(" Documented in ")
                    .append(doc.get(type))
                    .append(" template is no longer in spark schema");
        }
        return sb.toString();
    }

}
