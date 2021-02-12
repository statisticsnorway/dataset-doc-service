package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.media.common.DefaultMediaSupport;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import no.ssb.dapla.dataset.doc.template.ConceptNameLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LdsConceptLookup implements ConceptNameLookup {
    private final int port;
    private final String host;
    private final List<String> ldsSchemas;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(LdsConceptLookup.class);

    public LdsConceptLookup(Config config) {
        host = config.get("concept-lds").get("host").asString().orElseThrow(() ->
                new RuntimeException("missing configuration: concept-lds.host"));
        port = config.get("concept-lds").get("port").asInt().orElseThrow(() ->
                new RuntimeException("missing configuration: concept-lds.port"));

        LOG.info("Creating LdsConceptLookup with configuration: host={}, port={}", host, port);
        ldsSchemas = config.get("concept-lds").get("schemas").asList(String.class).get();
        LOG.info("Using schemas={}", ldsSchemas);
    }

    public Map<String, String> getNameToIds(String conceptType) {
        try {
            if (!ldsSchemas.contains(conceptType)) {
                String msg = String.format("concept lds do not provide %s resource", conceptType);
                throw new IllegalArgumentException(msg);
            }
            LOG.info("Calling {}:{}/ns/{}", host, port, conceptType);
            WebClient webClient = WebClient.builder()
                    .baseUri(host + ":" + port + "/ns/" + conceptType)
                    .addMediaSupport(DefaultMediaSupport.create())
                    .addMediaSupport(JacksonSupport.create(mapper))
                    .build();

            WebClientResponse response = webClient.get().contentType(MediaType.APPLICATION_JSON).submit().toCompletableFuture().join();
            JsonNode body = response.content().as(JsonNode.class).toCompletableFuture().join();
            HashMap<String, String> map = new HashMap<>();
            for (Iterator<JsonNode> it = body.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                // TODO: more checks, or move to graphQL
                JsonNode nameNode = node.get("name");
                // This should never happen, but we have data like this on staging now
                if(nameNode == null || nameNode.isNull()) {
                    LOG.warn("conceptType {} with id:{} is missing name", conceptType, node.get("id"));
                    continue;
                }

                String name = nameNode.get(0).get("languageText").asText();
                map.put(node.get("id").asText(), name);
            }
            LOG.info("Returned map size: {}", map.size());
            return map;
        } catch (Exception e) {
            LOG.info("Unexpected error", e);
            throw e;
        }
    }

    @Override
    public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
        switch (conceptType) {
            case "InstanceVariable":
                return processInstanceVariable(enumType);
            default:
                LOG.error("Unknown conceptType: " + conceptType);
                throw new IllegalArgumentException("Unknown conceptType: " + conceptType);
        }
    }

    private List<String> processInstanceVariable(String enumType) {
        switch (enumType) {
            case "dataStructureComponentType":
                return List.of("IDENTIFIER", "MEASURE", "ATTRIBUTE");
            case "dataStructureComponentRole":
                return List.of("ENTITY", "IDENTITY", "COUNT", "TIME", "GEO");
            default:
                LOG.error("Unknown enumType: " + enumType);
                throw new IllegalArgumentException("Unknown enumType: " + enumType);
        }
    }
}
