package no.ssb.dapla.dataset.doc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
                              id
                              createdBy
                              name {
                                languageCode
                                languageText
                              }
                            }
                            population {
                              id
                              createdBy
                              name {
                                languageCode
                                languageText
                              }
                            }
                            sentinelValueDomain {
                              __typename
                              ... on DescribedValueDomain {
                                id
                                name {
                                  languageCode
                                  languageText
                                }
                              }
                              ... on EnumeratedValueDomain {
                                id
                                name {
                                  languageCode
                                  languageText
                                }
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

        String testData = """
            [ {
              "unitDataSet" : {
                "lineage" : {
                  "reverseLineageFieldLineageDataset" : [ {
                    "smart" : [ {
                      "relationType" : "",
                      "id" : "felles.demo.dapla.oktober.formue$formue",
                      "instanceVariable" : null
                    }, {
                      "relationType" : "inherited",
                      "id" : "felles.demo.dapla.oktober.kommune_formue$formue",
                      "instanceVariable" : null
                    } ],
                    "id" : "felles.demo.dapla.oktober.formue$formue"
                  }, {
                    "smart" : [ {
                      "relationType" : "inherited",
                      "id" : "felles.demo.dapla.oktober.formue$fnr",
                      "instanceVariable" : null
                    }, {
                      "relationType" : "inherited",
                      "id" : "felles.demo.dapla.oktober.kommune_formue$fnr",
                      "instanceVariable" : null
                    }, {
                      "relationType" : "inherited",
                      "id" : "felles.demo.dapla.oktober.kommune$fnr",
                      "instanceVariable" : {
                        "representedVariable" : {
                          "name" : [ {
                            "languageCode" : "en",
                            "languageText" : "NationalPersonIdentifier"
                          }, {
                            "languageCode" : "nb",
                            "languageText" : "Fødselsnummer"
                          } ],
                          "id" : "6cdecd72-c4a8-4b56-9c0a-9fd01c4fea56",
                          "createdBy" : "BNJ"
                        },
                        "createdBy" : "rune.lind@ssb.no",
                        "name" : [ {
                          "languageText" : "fnr"
                        } ],
                        "description" : [ {
                          "languageText" : "Personens fødselsnummer"
                        } ],
                        "sentinelValueDomain" : {
                          "name" : [ {
                            "languageCode" : "en",
                            "languageText" : "Norwegian National identity number"
                          }, {
                            "languageCode" : "nb",
                            "languageText" : "Fødselsnummer"
                          } ],
                          "id" : "c14bf95e-a116-489b-827b-686650024930",
                          "__typename" : "DescribedValueDomain"
                        },
                        "dataStructureComponentType" : "IDENTIFIER",
                        "shortName" : "fnr",
                        "population" : {
                          "name" : [ {
                            "languageCode" : "nb",
                            "languageText" : "Personer med skatteplikt til Norge i 2019"
                          } ],
                          "id" : "96869bb9-ebcd-4630-ab73-005a4c4b4674",
                          "createdBy" : "Test user"
                        }
                      }
                    } ],
                    "id" : "felles.demo.dapla.oktober.formue$fnr"
                  } ]
                },
                "dataSourcePath" : "/felles/demo/dapla/oktober/formue"
              }
            } ]
                        
                        """;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(testData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        var query = createExplorationQuery(path);
//
//        WebClient webClient = WebClient.builder()
//                .baseUri(host + ":" + port + "/graphql")
//                .addMediaSupport(DefaultMediaSupport.create())
//                .addMediaSupport(JacksonSupport.create(mapper))
//                .build();
//
//        WebClientResponse response = webClient
//                .post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .submit(query)
//                .toCompletableFuture()
//                .join();
//
//        JsonNode result = response
//                .content()
//                .as(JsonNode.class)
//                .toCompletableFuture()
//                .join();
//        return result.get("data");
    }
}
