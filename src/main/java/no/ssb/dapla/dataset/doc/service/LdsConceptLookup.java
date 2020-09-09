package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.config.Config;
import io.helidon.media.common.DefaultMediaSupport;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LdsConceptLookup implements ConceptNameLookup {
    private final int port;
    private final String host;
    private final List<String> ldsSchemas;
    private static final ObjectMapper mapper = new ObjectMapper();

    public LdsConceptLookup(Config config) {
        port = config.get("concept-lds.port").asInt().get();
        host = config.get("concept-lds.host").asString().get();
        ldsSchemas = config.get("concept-lds.schemas").asList(String.class).get();
    }

    public Map<String, String> getNameToIds(String conceptType) {
        if (!ldsSchemas.contains(conceptType)) {
            String msg = String.format("concept lds do not provide %s resource", conceptType);
            throw new IllegalArgumentException(msg);
        }
        WebClient webClient = WebClient.builder()
                .baseUri(host + ":" + port + "/ns/" + conceptType)
                .addMediaSupport(DefaultMediaSupport.create())
                .addMediaSupport(JacksonSupport.create(mapper))
                .build();

        WebClientResponse response = webClient.get().submit().toCompletableFuture().join();
        JsonNode body = response.content().as(JsonNode.class).toCompletableFuture().join();
        HashMap<String, String> map = new HashMap<>();
        for (Iterator<JsonNode> it = body.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            // TODO: more checks, or move to graphQL
            String name = node.get("name").get(0).get("languageText").asText();
            map.put(node.get("id").asText(), name);
        }
        return map;
    }

    @Override
    public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
        switch (conceptType) {
            case "InstanceVariable":
                return processInstanceVariable(enumType);
            default:
                throw new IllegalArgumentException("");
        }
    }

    private List<String> processInstanceVariable(String enumType) {
        switch (enumType) {
            case "dataStructureComponentType":
                return List.of("IDENTIFIER", "MEASURE", "ATTRIBUTE");
            case "dataStructureComponentRole":
                return List.of("ENTITY", "IDENTITY", "COUNT", "TIME", "GEO");
            default:
                throw new IllegalArgumentException("");
        }
    }
}