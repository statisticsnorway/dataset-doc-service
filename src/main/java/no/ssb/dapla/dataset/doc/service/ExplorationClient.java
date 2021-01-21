package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.media.common.DefaultMediaSupport;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExplorationClient {

    private static final Logger LOG = LoggerFactory.getLogger(ExplorationClient.class);

    private final int port;
    private final String host;
    private static final ObjectMapper mapper = new ObjectMapper();

    public ExplorationClient(Config config) {
        host = config.get("exploration-lds").get("host").asString().orElseThrow(() ->
                new RuntimeException("missing configuration: exploration-lds.host"));
        port = config.get("exploration-lds").get("port").asInt().orElseThrow(() ->
                new RuntimeException("missing configuration: exploration-lds.port"));
    }

    ObjectNode createExplorationQuery(String path) {
        ObjectNode node = mapper.createObjectNode();

        String query = """
                {
                  unitDataSet(filter: {dataSourcePath: $path}) {
                    dataSourcePath
                    lineage {
                      reverseLineageFieldLineageDataset {
                        id
                        smart {
                          relationType
                          id
                          instanceVariable {
                            createdBy
                            shortName
                            name {
                              languageText
                            }
                            description {
                              languageText
                            }
                            dataStructureComponentType
                            representedVariable {
                              createdBy
                              shortName
                              id
                            }
                            population {
                              createdBy
                              shortName
                              id
                            }
                            sentinelValueDomain {
                              __typename
                              ... on DescribedValueDomain {
                                shortName
                              }
                              ... on EnumeratedValueDomain {
                                shortName
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """;

        node.put("query", query);
        node.putObject("variables").put("path", path);
        return node;
    }

    public JsonNode getExplorationMeta(String path) {
        LOG.info("Calling {}:{}/graphql - filter on dataSourcePath {}", host, port, path);
        var query = createExplorationQuery(path);

        WebClient webClient = WebClient.builder()
                .baseUri(host + ":" + port + "/graphql")
                .addMediaSupport(DefaultMediaSupport.create())
                .addMediaSupport(JacksonSupport.create(mapper))
                .build();

        WebClientResponse response = webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .submit(query)
                .toCompletableFuture()
                .join();

        JsonNode result = response
                .content()
                .as(JsonNode.class)
                .toCompletableFuture()
                .join();
        return result.get("data");
    }
}
