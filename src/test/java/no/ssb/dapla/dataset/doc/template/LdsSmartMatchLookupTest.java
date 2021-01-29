package no.ssb.dapla.dataset.doc.template;


import org.junit.jupiter.api.Test;

class LdsSmartMatchLookupTest {

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

    @Test
    void test() {

    }
}